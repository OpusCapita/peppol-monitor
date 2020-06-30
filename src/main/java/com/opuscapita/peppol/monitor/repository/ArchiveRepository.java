package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.monitor.entity.Archive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArchiveRepository extends JpaRepository<Archive, Long>, JpaSpecificationExecutor<Archive> {

    Archive findByTransmissionId(String transmissionId);

    List<Archive> findByMessageId(Long messageId);

    List<Archive> findByFilename(String filename);

    List<Archive> findByInvoiceNumber(String invoiceNumber);

}
