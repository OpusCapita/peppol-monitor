package com.opuscapita.peppol.monitor.controller;

import com.opuscapita.peppol.commons.container.state.log.DocumentLog;
import com.opuscapita.peppol.commons.container.state.log.DocumentLogLevel;
import com.opuscapita.peppol.monitor.controller.dtos.ProcessDto;
import com.opuscapita.peppol.monitor.controller.dtos.ProcessRequestDto;
import com.opuscapita.peppol.monitor.controller.dtos.ProcessResponseDto;
import com.opuscapita.peppol.monitor.entity.Process;
import com.opuscapita.peppol.monitor.repository.ProcessService;
import com.opuscapita.peppol.monitor.reprocess.ReprocessManager;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final ProcessService processService;
    private final ReprocessManager reprocessManager;

    @Autowired
    public MonitorRestController(ProcessService processService, ReprocessManager reprocessManager) {
        this.processService = processService;
        this.reprocessManager = reprocessManager;
    }

    @PostMapping("/get-processes")
    public ProcessResponseDto getProcesses(@RequestBody ProcessRequestDto request) {
        Page<Process> processes = processService.getProcesses(request);
        return new ProcessResponseDto(processes.getContent(), processes.getTotalPages());
    }

    @GetMapping("/get-process-by-id/{processId}")
    public ResponseEntity<?> getProcessById(@PathVariable Long processId) {
        Process process = processService.getProcess(processId);
        return wrap(ProcessDto.of(process));
    }

    @GetMapping("/get-process-by-transmissionId/{transmissionId}")
    public ResponseEntity<?> getProcessByTransmissionId(@PathVariable String transmissionId) {
        Process process = processService.getProcess(transmissionId);
        return wrap(ProcessDto.of(process));
    }

    @PostMapping("/upload-file/{processId}")
    public ResponseEntity<?> uploadFileOfProcess(@PathVariable Long processId, @RequestParam("file") MultipartFile multipartFile) throws IOException {
        Process process = processService.getProcess(processId);
        try (InputStream inputStream = multipartFile.getInputStream()) {
            processService.updateFileContent(inputStream, process);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download-file/{processId}")
    public ResponseEntity<byte[]> downloadFileOfProcess(@PathVariable Long processId) throws IOException {
        Process process = processService.getProcess(processId);
        InputStream inputStream = processService.getFileContent(process);
        byte[] rawData = IOUtils.toByteArray(inputStream);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.TEXT_XML);
        header.setContentLength(rawData.length);
        header.set("Content-Disposition", "attachment; filename=" + FilenameUtils.getName(process.getFilename()));
        return new ResponseEntity<>(rawData, header, HttpStatus.OK);
    }

    @GetMapping("/reprocess-message/{processId}")
    public ResponseEntity<?> reprocessMessage(@PathVariable Long processId) throws IOException {
        Process process = processService.getProcess(processId);
        if (process == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        DocumentLog log = new DocumentLog("Sending REPROCESS request for the message", DocumentLogLevel.INFO);
        processService.addMessageToHistoryOfProcess(process, log);

        reprocessManager.reprocessMessage(process);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-history/{messageId}")
    public List<DocumentLog> getMessageHistory(@PathVariable String messageId) {
        List<Process> processes = processService.getAllProcesses(messageId);
        return processes.stream().flatMap(process -> process.getLogs().stream()).collect(Collectors.toList());
    }

    private <T> ResponseEntity<T> wrap(T body) {
        if (body != null) {
            return ResponseEntity.ok(body);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
