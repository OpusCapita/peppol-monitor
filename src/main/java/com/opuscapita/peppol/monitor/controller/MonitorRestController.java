package com.opuscapita.peppol.monitor.controller;

import com.opuscapita.peppol.commons.container.state.log.DocumentLog;
import com.opuscapita.peppol.monitor.controller.dtos.MessageDto;
import com.opuscapita.peppol.monitor.entity.Message;
import com.opuscapita.peppol.monitor.entity.Process;
import com.opuscapita.peppol.monitor.repository.MessageService;
import com.opuscapita.peppol.monitor.repository.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    private static final Logger logger = LoggerFactory.getLogger(MonitorRestController.class);

    @Value("${support-ui.page-size:20}")
    private Integer pageSize;

    private final MessageService messageService;
    private final ProcessService processService;

    @Autowired
    public MonitorRestController(MessageService messageService, ProcessService processService) {
        this.messageService = messageService;
        this.processService = processService;
    }

    @GetMapping("/message-count")
    public Long getMessageCount() {
        return messageService.countMessages();
    }

    @GetMapping("/messages/{pageNumber}")
    public List<MessageDto> getMessages(@PathVariable Integer pageNumber) {
        pageNumber = pageNumber == null ? 0 : pageNumber;
        List<Message> messages = messageService.getAllMessages(pageNumber, pageSize);
        return messages.stream().map(MessageDto::of).collect(Collectors.toList());
    }

    @GetMapping("/message-by-messageId/{messageId}")
    public ResponseEntity<?> getByMessageId(@PathVariable String messageId) {
        Message message = messageService.getMessage(messageId);
        if (message != null) {
            return ResponseEntity.ok(MessageDto.of(message));
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/message-history/{messageId}")
    public List<DocumentLog> getMessageHistory(@PathVariable String messageId) {
        List<Process> processes = processService.getAllProcesses(messageId);
        return processes.stream().flatMap(process -> process.getLogs().stream()).collect(Collectors.toList());
    }

}
