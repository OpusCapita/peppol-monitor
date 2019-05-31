package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.commons.container.state.log.DocumentLog;
import com.opuscapita.peppol.commons.storage.StorageException;
import com.opuscapita.peppol.monitor.controller.dtos.TransmissionFilterDto;
import com.opuscapita.peppol.monitor.controller.dtos.TransmissionRequestDto;
import com.opuscapita.peppol.monitor.entity.Transmission;
import org.springframework.data.domain.Page;

import java.io.InputStream;
import java.util.List;

public interface TransmissionService {

    Transmission saveTransmission(Transmission transmission);

    Transmission getTransmission(Long id);

    Transmission getTransmission(String transmissionId);

    Transmission getByFilename(String filename);

    List<Transmission> getAllTransmissions(String messageId);

    List<Transmission> filterTransmissions(TransmissionFilterDto filterDto);

    List<Transmission> getAllTransmissions(int pageNumber, int pageSize);

    Page<Transmission> getAllTransmissions(TransmissionRequestDto request);

    InputStream getFileContent(String path) throws StorageException;

    String getMlrPath(Transmission transmission) throws StorageException;

    void updateFileContent(InputStream content, Transmission transmission) throws StorageException;

    void addMessageToHistoryOfTransmission(Transmission transmission, DocumentLog log);

}
