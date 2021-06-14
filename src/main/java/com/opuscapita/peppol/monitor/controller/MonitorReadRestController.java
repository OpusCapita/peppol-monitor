package com.opuscapita.peppol.monitor.controller;

import com.opuscapita.peppol.commons.container.state.log.DocumentLog;
import com.opuscapita.peppol.monitor.controller.dtos.TransmissionDto;
import com.opuscapita.peppol.monitor.controller.dtos.TransmissionRequestDto;
import com.opuscapita.peppol.monitor.controller.dtos.TransmissionResponseDto;
import com.opuscapita.peppol.monitor.controller.dtos.TransmissionStatisticsDto;
import com.opuscapita.peppol.monitor.entity.*;
import com.opuscapita.peppol.monitor.repository.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.opuscapita.peppol.commons.eventing.TicketReporter;





import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MonitorReadRestController {

    private final MessageService messageService;
    private final StatisticsService statisticsService;
    private final TransmissionService transmissionService;
    private final ParticipantRepository participantRepository;
    private final AccessPointRepository accessPointRepository;
    private final TicketReporter ticketReporter;

    @Autowired
    public MonitorReadRestController(MessageService messageService, StatisticsService statisticsService, TransmissionService transmissionService,
                                     ParticipantRepository participantRepository, AccessPointRepository accessPointRepository,
                                     TicketReporter ticketReporter) {
        this.messageService = messageService;
        this.statisticsService = statisticsService;
        this.transmissionService = transmissionService;
        this.participantRepository = participantRepository;
        this.accessPointRepository = accessPointRepository;
        this.ticketReporter = ticketReporter;
    }

    @GetMapping("/trigger-dummy-ticket")
    public String triggerDummyTicket() throws Exception {
      try {
        this.ticketReporter.reportWithoutContainerMessage(
            "opuscapita", "dummyfile.xml", new Exception("this is a dummy exception"), "this is a dummy ticket"
            );

        return "ok";
      }
      catch ( Exception e ) {
          return e.toString();
      }


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
        try {
            InputStream inputStream = transmissionService.getFileContent(mlrPath);
            return wrapFileToDownload(inputStream, FilenameUtils.getName(mlrPath));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-history/{messageId}")
    public List<DocumentLog> getMessageHistory(@PathVariable String messageId) {
        List<Transmission> transmissionList = transmissionService.getAllTransmissions(messageId);
        return transmissionList.stream().flatMap(t -> t.getLogs().stream()).collect(Collectors.toList());
    }

    @GetMapping("/get-message-status/{messageId}")
    public MessageStatus getMessageStatus(@PathVariable String messageId) {
        Message message = messageService.getMessage(messageId);
        if (message == null || message.getLastTransmission() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return message.getLastTransmission().getStatus();
    }

    @GetMapping("/get-access-points")
    public ResponseEntity<?> getAccessPoints() {
        List<AccessPoint> accessPoints = accessPointRepository.findAll();
        return wrap(accessPoints);
    }

    @GetMapping("/get-access-point/{accessPointId}")
    public ResponseEntity<?> getAccessPoint(@PathVariable String accessPointId) {
        Optional<AccessPoint> accessPoint = accessPointRepository.findById(accessPointId);
        return wrap(accessPoint.orElse(null));
    }

    @GetMapping("/get-participants")
    public ResponseEntity<?> getParticipants() {
        List<Participant> participants = participantRepository.findAll();
        return wrap(participants);
    }

    @GetMapping("/get-statistics/{from}/{to}")
    public List<TransmissionStatisticsDto> getStatistics(@PathVariable String from, @PathVariable String to) {
        return statisticsService.get(from, to);
    }

    private ResponseEntity<byte[]> wrapFileToDownload(InputStream inputStream, String filename) throws IOException {
        byte[] rawData = IOUtils.toByteArray(inputStream);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.TEXT_XML);
        header.setContentLength(rawData.length);
        header.set("Content-Disposition", "attachment; filename=" + filename);
        return new ResponseEntity<>(rawData, header, HttpStatus.OK);
    }

    private <T> ResponseEntity<T> wrap(T body) {
        if (body != null) {
            return ResponseEntity.ok(body);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
