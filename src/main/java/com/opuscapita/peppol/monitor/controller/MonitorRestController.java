package com.opuscapita.peppol.monitor.controller;

import com.opuscapita.peppol.monitor.entity.Message;
import com.opuscapita.peppol.monitor.repository.MessageService;
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

@RestController
@RequestMapping("/api")
public class MonitorRestController {

    private static final Logger logger = LoggerFactory.getLogger(MonitorRestController.class);

    @Value("${support-ui.page-size:20}")
    private Integer pageSize;

    private final MessageService messageService;

    @Autowired
    public MonitorRestController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/message-count")
    public Long getMessageCount() {
        return messageService.countMessages();
    }

    @GetMapping("/messages/{pageNumber}")
    public List<Message> getMessages(@PathVariable Integer pageNumber) {
        pageNumber = pageNumber == null ? 0 : pageNumber;
        List<Message> result = messageService.getAllMessages(pageNumber, pageSize);
        logger.info(String.format("GetMessages invoked, returning %s record for page: %s", result.size(), pageNumber));
        return result;
    }

    @GetMapping("/message/{id}")
    public ResponseEntity<?> getMessage(@PathVariable Long id) {
        Message message = messageService.getMessage(id);
        if (message != null) {
            return ResponseEntity.ok(message);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
