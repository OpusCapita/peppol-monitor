package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.monitor.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Message findByMessageId(String messageId);

}
