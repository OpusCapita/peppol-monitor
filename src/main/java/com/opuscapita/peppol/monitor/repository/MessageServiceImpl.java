package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.monitor.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    private final MessageRepository repository;

    @Autowired
    public MessageServiceImpl(MessageRepository repository) {
        this.repository = repository;
    }

    @Override
    public Message saveMessage(Message message) {
        try {
            message = repository.saveAndFlush(message);
        } catch (Exception e) {
            logger.error("Error occurred while saving the message, reason: " + e.getMessage());
            message = getMessage(message.getMessageId());
        }

        return message;
    }

    @Override
    public Message getMessage(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Message getMessage(String messageId) {
        return repository.findByMessageId(messageId);
    }

    @Override
    public void deleteMessage(Message message) {
        repository.delete(message);
    }

    @Override
    public void deleteMessage(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Message> getAllMessages(int pageNumber, int pageSize) {
        return repository.findAll(PageRequest.of(pageNumber, pageSize)).getContent();
    }

    @Override
    public long countMessages() {
        return repository.count();
    }

}
