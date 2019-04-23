package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.monitor.entity.Transmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransmissionRepository extends JpaRepository<Transmission, Long>, JpaSpecificationExecutor<Transmission> {

    Transmission findByTransmissionId(String transmissionId);

    List<Transmission> findByMessageId(Long messageId);

    List<Transmission> findByMessageMessageId(String messageId);

}
