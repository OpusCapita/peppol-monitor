package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.monitor.entity.Message;
import com.opuscapita.peppol.monitor.util.MessageHistorySerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageServiceImpl implements MessageService {

    private final MessageRepository repository;
    private final MessageHistorySerializer historySerializer;

    @Autowired
    public MessageServiceImpl(MessageRepository repository, MessageHistorySerializer historySerializer) {
        this.repository = repository;
        this.historySerializer = historySerializer;
    }

    @Override
    public Message saveMessage(Message message) {
        return repository.save(message);
    }

    @Override
    public Message getMessage(Long id) {
        Message message = repository.findById(id).orElse(null);
        if (message != null) {
            message.setLogs(historySerializer.fromJson(message.getRawHistory()));
        }
        return message;
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
