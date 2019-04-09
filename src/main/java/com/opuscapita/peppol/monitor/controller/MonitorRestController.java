package com.opuscapita.peppol.monitor.controller;

import com.opuscapita.peppol.commons.container.state.log.DocumentLog;
import com.opuscapita.peppol.monitor.controller.dtos.ProcessDto;
import com.opuscapita.peppol.monitor.controller.dtos.ProcessRequestDto;
import com.opuscapita.peppol.monitor.controller.dtos.ProcessResponseDto;
import com.opuscapita.peppol.monitor.entity.Process;
import com.opuscapita.peppol.monitor.repository.MessageService;
import com.opuscapita.peppol.monitor.repository.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MonitorRestController {

    private final ProcessService processService;

    @Autowired
    public MonitorRestController(MessageService messageService, ProcessService processService) {
        this.processService = processService;
    }

    @GetMapping("/get-processes")
    public ProcessResponseDto getProcesses(ProcessRequestDto request) {
        Page<Process> processes = processService.getProcesses(request);
        return new ProcessResponseDto(processes.getContent(), processes.getTotalPages());
    }

    @GetMapping("/get-process-by-id/{id}")
    public ResponseEntity<?> getProcessById(@PathVariable Long id) {
        Process process = processService.getProcess(id);
        return wrap(ProcessDto.of(process));
    }

    @GetMapping("/get-process-by-transmissionId/{transmissionId}")
    public ResponseEntity<?> getProcessByTransmissionId(@PathVariable String transmissionId) {
        Process process = processService.getProcess(transmissionId);
        return wrap(ProcessDto.of(process));
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
