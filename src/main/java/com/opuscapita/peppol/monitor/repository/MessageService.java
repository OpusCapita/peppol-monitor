package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.monitor.entity.Message;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.List;

public interface MessageService {

    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    Message saveMessage(Message message);

    Message getMessage(Long id);

    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    Message getMessage(String messageId);

    void deleteMessage(Message message);

    void deleteMessage(Long id);

    List<Message> getAllMessages(int pageNumber, int pageSize);

    long countMessages();

}
