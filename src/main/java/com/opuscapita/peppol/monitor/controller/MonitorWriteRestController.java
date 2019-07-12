package com.opuscapita.peppol.monitor.controller;

import com.opuscapita.peppol.commons.container.state.ProcessStep;
import com.opuscapita.peppol.commons.container.state.log.DocumentLog;
import com.opuscapita.peppol.commons.container.state.log.DocumentLogLevel;
import com.opuscapita.peppol.monitor.entity.AccessPoint;
import com.opuscapita.peppol.monitor.entity.MessageStatus;
import com.opuscapita.peppol.monitor.entity.Transmission;
import com.opuscapita.peppol.monitor.mlrreport.MlrReportManager;
import com.opuscapita.peppol.monitor.repository.AccessPointRepository;
import com.opuscapita.peppol.monitor.repository.TransmissionService;
import com.opuscapita.peppol.monitor.reprocess.ReprocessManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MonitorWriteRestController {

    private static final Logger logger = LoggerFactory.getLogger(MonitorWriteRestController.class);

    private final MlrReportManager mlrManager;
    private final ReprocessManager reprocessManager;
    private final TransmissionService transmissionService;
    private final AccessPointRepository accessPointRepository;

    @Autowired
    public MonitorWriteRestController(MlrReportManager mlrManager, TransmissionService transmissionService,
                                      ReprocessManager reprocessManager, AccessPointRepository accessPointRepository) {
        this.mlrManager = mlrManager;
        this.reprocessManager = reprocessManager;
        this.transmissionService = transmissionService;
        this.accessPointRepository = accessPointRepository;
    }

    @PostMapping("/upload-file/{userId}/{transmissionId}")
    public ResponseEntity<?> uploadFileOfTransmission(@PathVariable String userId, @PathVariable Long transmissionId,
                                                      @RequestParam("file") MultipartFile multipartFile) throws IOException {
        Transmission transmission = transmissionService.getTransmission(transmissionId);
        logger.info("Upload file requested for file: " + transmission.getFilename() + " by: " + userId);
        try (InputStream inputStream = multipartFile.getInputStream()) {
            transmissionService.updateFileContent(inputStream, transmission);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/send-mlr/{userId}/{transmissionId}")
    public ResponseEntity<?> sendMlrOfTransmission(@PathVariable String userId, @PathVariable Long transmissionId) throws Exception {
        return sendMlrOfSingleTransmission(transmissionId, userId);
    }

    @PostMapping("/send-mlrs/{userId}")
    public ResponseEntity<?> sendMlrOfTransmissions(@PathVariable String userId, @RequestBody List<String> transmissionIds) {
        for (String transmissionId : transmissionIds) {
            try {
                sendMlrOfSingleTransmission(Long.parseLong(transmissionId), userId);
            } catch (Exception e) {
                logger.error("Async bulk send-mlr operation failed for transmission: " + transmissionId, e);
            }
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-mlrs-advanced/{userId}")
    public ResponseEntity<?> sendMlrOfTransmissionsAdvanced(@PathVariable String userId, @RequestBody List<String> transmissionList) {
        for (String filename : transmissionList) {
            try {
                Transmission transmission = transmissionService.getByFilename(filename);
                if (transmission != null) {
                    logger.info("Send MLR requested for file: " + transmission.getFilename() + " by: " + userId);
                    mlrManager.sendToMlrReporter(transmission);
                }
            } catch (Exception e) {
                logger.error("Advanced bulk send-mlr operation failed for file: " + filename, e);
            }
        }
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<?> sendMlrOfSingleTransmission(Long transmissionId, String userId) throws Exception {
        Transmission transmission = transmissionService.getTransmission(transmissionId);
        if (transmission == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        logger.info("Send MLR requested for file: " + transmission.getFilename() + " by: " + userId);
        mlrManager.sendToMlrReporter(transmission);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mark-fixed-message/{userId}/{transmissionId}")
    public ResponseEntity<?> markAsFixedMessage(@PathVariable String userId, @PathVariable Long transmissionId, @RequestBody String fixComment) {
        try {
            logger.info("fix comment: " + fixComment);
            fixComment = URLDecoder.decode(fixComment, StandardCharsets.UTF_8.name());
            logger.info("decoded fix comment: " + fixComment);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return markAsFixedSingleMessage(transmissionId, userId, fixComment);
    }

    @PostMapping("/mark-fixed-messages/{userId}")
    public ResponseEntity<?> markAsFixedMessages(@PathVariable String userId, @RequestBody List<String> transmissionIds) {
        for (String transmissionId : transmissionIds) {
            try {
                markAsFixedSingleMessage(Long.parseLong(transmissionId), userId, "");
            } catch (Exception e) {
                logger.error("Async bulk mark-as-fixed operation failed for transmission: " + transmissionId, e);
            }
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mark-fixed-messages-advanced/{userId}")
    public ResponseEntity<?> markAsFixedMessagesAdvanced(@PathVariable String userId, @RequestBody List<String> transmissionList) {
        for (String filename : transmissionList) {
            try {
                Transmission transmission = transmissionService.getByFilename(filename);
                if (transmission != null) {
                    logger.info("Mark as Fixed requested for file: " + transmission.getFilename() + " by: " + userId);
                    DocumentLog log = new DocumentLog("Message marked as fixed by " + userId, DocumentLogLevel.INFO);
                    log.setSource(ProcessStep.WEB);
                    transmission.setStatus(MessageStatus.fixed);

                    transmissionService.addMessageToHistoryOfTransmission(transmission, log);
                }
            } catch (Exception e) {
                logger.error("Advanced bulk mark-as-fixed operation failed for file: " + filename, e);
            }
        }
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<?> markAsFixedSingleMessage(Long transmissionId, String userId, String fixComment) {
        Transmission transmission = transmissionService.getTransmission(transmissionId);
        if (transmission == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        logger.info("Mark as Fixed requested for file: " + transmission.getFilename() + " by: " + userId);
        DocumentLog log = new DocumentLog(userId + " fixed the message, explanation: " + fixComment, DocumentLogLevel.INFO);
        log.setSource(ProcessStep.WEB);
        transmission.setStatus(MessageStatus.fixed);
        transmissionService.addMessageToHistoryOfTransmission(transmission, log);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/reprocess-message/{userId}/{transmissionId}")
    public ResponseEntity<?> reprocessMessage(@PathVariable String userId, @PathVariable Long transmissionId) throws IOException {
        return reprocessSingleMessage(transmissionId, userId);
    }

    @PostMapping("/reprocess-messages/{userId}")
    public ResponseEntity<?> reprocessMessages(@PathVariable String userId, @RequestBody List<String> transmissionIds) {
        for (String transmissionId : transmissionIds) {
            try {
                reprocessSingleMessage(Long.parseLong(transmissionId), userId);
            } catch (Exception e) {
                logger.error("Async bulk reprocess operation failed for transmission: " + transmissionId, e);
            }
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reprocess-messages-advanced/{userId}")
    public ResponseEntity<?> reprocessMessagesAdvanced(@PathVariable String userId, @RequestBody List<String> transmissionList) {
        for (String filename : transmissionList) {
            try {
                Transmission transmission = transmissionService.getByFilename(filename);
                if (transmission != null) {
                    logger.info("Reprocess requested for file: " + transmission.getFilename() + " by: " + userId);
                    DocumentLog log = new DocumentLog("Sending REPROCESS request for the message, triggered by " + userId, DocumentLogLevel.INFO);
                    log.setSource(ProcessStep.REPROCESSOR);
                    transmission.setStatus(MessageStatus.fixed);
                    transmissionService.addMessageToHistoryOfTransmission(transmission, log);

                    reprocessManager.reprocessMessage(transmission);
                }
            } catch (Exception e) {
                logger.error("Advanced bulk reprocess operation failed for file: " + filename, e);
            }
        }
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<?> reprocessSingleMessage(Long transmissionId, String userId) throws IOException {
        Transmission transmission = transmissionService.getTransmission(transmissionId);
        if (transmission == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        logger.info("Reprocess operation requested for file: " + transmission.getFilename() + " by: " + userId);
        DocumentLog log = new DocumentLog("Sending REPROCESS request for the message, triggered by " + userId, DocumentLogLevel.INFO);
        log.setSource(ProcessStep.REPROCESSOR);
        transmission.setStatus(MessageStatus.fixed);
        transmissionService.addMessageToHistoryOfTransmission(transmission, log);

        reprocessManager.reprocessMessage(transmission);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update-access-point")
    public ResponseEntity<?> updateAccessPoint(@RequestBody AccessPoint accessPoint) {
        AccessPoint persisted = accessPointRepository.save(accessPoint);
        return wrap(persisted);
    }

    private <T> ResponseEntity<T> wrap(T body) {
        if (body != null) {
            return ResponseEntity.ok(body);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
