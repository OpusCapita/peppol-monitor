package com.opuscapita.peppol.monitor.controller;

import com.opuscapita.peppol.commons.container.state.ProcessStep;
import com.opuscapita.peppol.commons.container.state.log.DocumentLog;
import com.opuscapita.peppol.commons.container.state.log.DocumentLogLevel;
import com.opuscapita.peppol.monitor.controller.dtos.TransmissionDto;
import com.opuscapita.peppol.monitor.controller.dtos.TransmissionRequestDto;
import com.opuscapita.peppol.monitor.controller.dtos.TransmissionResponseDto;
import com.opuscapita.peppol.monitor.entity.AccessPoint;
import com.opuscapita.peppol.monitor.entity.MessageStatus;
import com.opuscapita.peppol.monitor.entity.Participant;
import com.opuscapita.peppol.monitor.entity.Transmission;
import com.opuscapita.peppol.monitor.mlrreport.MlrReportManager;
import com.opuscapita.peppol.monitor.repository.AccessPointRepository;
import com.opuscapita.peppol.monitor.repository.ParticipantRepository;
import com.opuscapita.peppol.monitor.repository.TransmissionService;
import com.opuscapita.peppol.monitor.reprocess.ReprocessManager;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MonitorRestController {

    private static final Logger logger = LoggerFactory.getLogger(MonitorRestController.class);

    @Value("${manual-operation}")
    private String filenames;

    private final MlrReportManager mlrManager;
    private final ReprocessManager reprocessManager;
    private final TransmissionService transmissionService;
    private final ParticipantRepository participantRepository;
    private final AccessPointRepository accessPointRepository;

    @Autowired
    public MonitorRestController(MlrReportManager mlrManager, TransmissionService transmissionService, ReprocessManager reprocessManager,
                                 ParticipantRepository participantRepository, AccessPointRepository accessPointRepository) {
        this.mlrManager = mlrManager;
        this.reprocessManager = reprocessManager;
        this.transmissionService = transmissionService;
        this.participantRepository = participantRepository;
        this.accessPointRepository = accessPointRepository;
    }

    @PostMapping("/get-transmissions")
    public TransmissionResponseDto getTransmissions(@RequestBody TransmissionRequestDto request) {
        Page<Transmission> transmissions = transmissionService.getAllTransmissions(request);
        return new TransmissionResponseDto(transmissions.getContent(), transmissions.getTotalElements());
    }

    @GetMapping("/get-transmission-by-id/{id}")
    public ResponseEntity<?> getTransmissionById(@PathVariable Long id) {
        Transmission transmission = transmissionService.getTransmission(id);
        return wrap(TransmissionDto.of(transmission));
    }

    @GetMapping("/get-transmission-by-transmissionId/{transmissionId}")
    public ResponseEntity<?> getTransmissionByTransmissionId(@PathVariable String transmissionId) {
        Transmission transmission = transmissionService.getTransmission(transmissionId);
        return wrap(TransmissionDto.of(transmission));
    }

    @PostMapping("/upload-file/{transmissionId}")
    public ResponseEntity<?> uploadFileOfTransmission(@PathVariable Long transmissionId, @RequestParam("file") MultipartFile multipartFile) throws IOException {
        Transmission transmission = transmissionService.getTransmission(transmissionId);
        try (InputStream inputStream = multipartFile.getInputStream()) {
            transmissionService.updateFileContent(inputStream, transmission);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download-file/{transmissionId}")
    public ResponseEntity<byte[]> downloadFileOfTransmission(@PathVariable Long transmissionId) throws IOException {
        Transmission transmission = transmissionService.getTransmission(transmissionId);
        InputStream inputStream = transmissionService.getFileContent(transmission.getFilename());
        return wrapFileToDownload(inputStream, FilenameUtils.getName(transmission.getFilename()));
    }

    @GetMapping("/download-mlr/{transmissionId}")
    public ResponseEntity<byte[]> downloadMlrOfTransmission(@PathVariable Long transmissionId) throws IOException {
        Transmission transmission = transmissionService.getTransmission(transmissionId);
        String mlrPath = transmissionService.getMlrPath(transmission);
        InputStream inputStream = transmissionService.getFileContent(mlrPath);
        return wrapFileToDownload(inputStream, FilenameUtils.getName(mlrPath));
    }

    private ResponseEntity<byte[]> wrapFileToDownload(InputStream inputStream, String filename) throws IOException {
        byte[] rawData = IOUtils.toByteArray(inputStream);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.TEXT_XML);
        header.setContentLength(rawData.length);
        header.set("Content-Disposition", "attachment; filename=" + filename);
        return new ResponseEntity<>(rawData, header, HttpStatus.OK);
    }

    @GetMapping("/send-mlr/{transmissionId}")
    public ResponseEntity<?> sendMlrOfTransmission(@PathVariable Long transmissionId) throws Exception {
        Transmission transmission = transmissionService.getTransmission(transmissionId);
        if (transmission == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        mlrManager.sendToMlrReporter(transmission);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mark-fixed-message/{transmissionId}")
    public ResponseEntity<?> markAsFixedMessage(@PathVariable Long transmissionId) {
        Transmission transmission = transmissionService.getTransmission(transmissionId);
        if (transmission == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        DocumentLog log = new DocumentLog("Message marked as fixed", DocumentLogLevel.INFO);
        log.setSource(ProcessStep.WEB);
        transmission.setStatus(MessageStatus.fixed);
        transmissionService.addMessageToHistoryOfTransmission(transmission, log);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/reprocess-message/{transmissionId}")
    public ResponseEntity<?> reprocessMessage(@PathVariable Long transmissionId) throws IOException {
        return reprocessSingleMessage(transmissionId);
    }

    @GetMapping("/reprocess-messages/{transmissionIds}")
    public ResponseEntity<?> reprocessMessages(@PathVariable String transmissionIds) {
        new Thread(() -> {
            for (String transmissionId : transmissionIds.split("-")) {
                try {
                    reprocessSingleMessage(Long.parseLong(transmissionId));
                } catch (IOException e) {
                    logger.error("Async bulk reprocess operation failed for transmission: " + transmissionId, e);
                }
            }
        }).start();

        return ResponseEntity.ok().build();
    }

    private ResponseEntity<?> reprocessSingleMessage(Long transmissionId) throws IOException {
        Transmission transmission = transmissionService.getTransmission(transmissionId);
        if (transmission == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        DocumentLog log = new DocumentLog("Sending REPROCESS request for the message", DocumentLogLevel.INFO);
        log.setSource(ProcessStep.REPROCESSOR);
        transmission.setStatus(MessageStatus.fixed);
        transmissionService.addMessageToHistoryOfTransmission(transmission, log);

        reprocessManager.reprocessMessage(transmission);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/manual-operation/{operationName}")
    public ResponseEntity<?> manualOperation(@PathVariable String operationName) throws Exception {
        String[] list = filenames.split("\\n");
        for (String filename : list) {
            Transmission transmission = transmissionService.getByFilename(filename);
            if (transmission != null) {
                if (operationName.equals("send-mlr")) {
                    mlrManager.sendToMlrReporter(transmission);
                }
            }
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-history/{messageId}")
    public List<DocumentLog> getMessageHistory(@PathVariable String messageId) {
        List<Transmission> transmissionList = transmissionService.getAllTransmissions(messageId);
        return transmissionList.stream().flatMap(t -> t.getLogs().stream()).collect(Collectors.toList());
    }

    @GetMapping("/get-access-points")
    public ResponseEntity<?> getAccessPoints() {
        List<AccessPoint> accessPoints = accessPointRepository.findAll();
        return wrap(accessPoints);
    }

    @PostMapping("/update-access-point")
    public ResponseEntity<?> updateAccessPoint(@RequestBody AccessPoint accessPoint) {
        AccessPoint persisted = accessPointRepository.save(accessPoint);
        return wrap(persisted);
    }

    @GetMapping("/get-participants")
    public ResponseEntity<?> getParticipants() {
        List<Participant> participants = participantRepository.findAll();
        return wrap(participants);
    }

    private <T> ResponseEntity<T> wrap(T body) {
        if (body != null) {
            return ResponseEntity.ok(body);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
