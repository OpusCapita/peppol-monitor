package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.monitor.entity.Process;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessRepository extends JpaRepository<Process, Long> {

    Process findByTransmissionId(String transmissionId);

    List<Process> findByMessageId(Long messageId);

    List<Process> findByMessageMessageId(String messageId);

}
