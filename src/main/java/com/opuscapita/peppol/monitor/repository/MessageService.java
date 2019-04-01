package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.monitor.entity.Message;

import java.util.List;

public interface MessageService {

    Message saveMessage(Message message);

    Message getMessage(Long id);

    Message getMessage(String messageId);

    void deleteMessage(Message message);

    void deleteMessage(Long id);

    List<Message> getAllMessages(int pageNumber, int pageSize);

    long countMessages();

}
