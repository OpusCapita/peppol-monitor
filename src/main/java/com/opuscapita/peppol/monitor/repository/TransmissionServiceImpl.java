package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.commons.container.state.log.DocumentLog;
import com.opuscapita.peppol.commons.storage.Storage;
import com.opuscapita.peppol.commons.storage.StorageException;
import com.opuscapita.peppol.monitor.controller.dtos.TransmissionRequestDto;
import com.opuscapita.peppol.monitor.entity.MessageStatus;
import com.opuscapita.peppol.monitor.entity.Transmission;
import com.opuscapita.peppol.monitor.util.TransmissionHistorySerializer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransmissionServiceImpl implements TransmissionService {

    private static final Logger logger = LoggerFactory.getLogger(TransmissionServiceImpl.class);

    private final Storage storage;
    private final TransmissionRepository repository;
    private final TransmissionHistorySerializer historySerializer;

    @Autowired
    public TransmissionServiceImpl(Storage storage, TransmissionRepository repository, TransmissionHistorySerializer historySerializer) {
        this.storage = storage;
        this.repository = repository;
        this.historySerializer = historySerializer;
    }

    @Override
    public Transmission saveTransmission(Transmission transmission) {
        try {
            transmission = repository.saveAndFlush(transmission);
            logger.info("Monitor saved the message: " + transmission.getFilename());
        } catch (Exception e) {
            logger.error("Error occurred while saving the message: " + transmission.getFilename() + ", reason: " + e.getMessage());
        }

        return transmission;
    }

    @Override
    public Transmission getTransmission(Long id) {
        Transmission transmission = repository.findById(id).orElse(null);
        return loadTransmissionHistory(transmission);
    }

    @Override
    public Transmission getTransmission(String transmissionId) {
        return repository.findByTransmissionId(transmissionId);
    }

    @Override
    public Transmission getByFilename(String filename) {
        filename = filename.trim();
        if (StringUtils.isBlank(filename)) {
            return null;
        }

        List<Transmission> transmissions = repository.findByFilename(filename);
        if (transmissions == null || transmissions.isEmpty()) {
            return null;
        }

        Transmission transmission = transmissions.stream().max(Comparator.comparing(Transmission::getId)).orElse(null);
        return loadTransmissionHistory(transmission);
    }

    @Override
    public Page<Transmission> getAllTransmissions(TransmissionRequestDto request) {
        Specification<Transmission> spec = TransmissionFilterSpecification.filter(request.getFilter(), request.getPagination().getSorted());
        Pageable pageable = PageRequest.of(request.getPagination().getPage(), request.getPagination().getPageSize());
        return repository.findAll(spec, pageable);
    }

    @Override
    public List<Transmission> getAllTransmissions(String messageId) {
        List<Transmission> transmissionList = repository.findByMessageMessageId(messageId);
        return transmissionList.stream()
                .peek(t -> t.setLogs(historySerializer.fromJson(t.getRawHistory())))
                .collect(Collectors.toList());
    }

    @Override
    public InputStream getFileContent(String path) throws StorageException {
        return storage.get(path);
    }

    @Override
    public String getMlrPath(Transmission transmission) {
        String extension = "";
        if (transmission.getLogs().stream().anyMatch(DocumentLog::isValidationError)) {
            extension = "re";
        } else if (transmission.getLogs().stream().anyMatch(DocumentLog::isError)) {
            extension = "er";
        } else if (transmission.getStatus().equals(MessageStatus.sending)) {
            extension = "ab";
        } else if (transmission.getStatus().equals(MessageStatus.delivered)) {
            extension = "ap";
        }

        String pathName = FilenameUtils.getFullPath(transmission.getFilename());
        String baseName = FilenameUtils.getBaseName(transmission.getFilename());
        String fileName = baseName + "-" + extension + "-mlr.xml";
        return pathName + fileName;
    }

    @Override
    public void updateFileContent(InputStream content, Transmission transmission) throws StorageException {
        storage.update(content, transmission.getFilename());
    }

    @Override
    public void addMessageToHistoryOfTransmission(Transmission transmission, DocumentLog log) {
        transmission.getLogs().add(log);
        transmission.setRawHistory(historySerializer.toJson(transmission.getLogs()));

        saveTransmission(transmission);
    }

    @Override
    public Transmission loadTransmissionHistory(Transmission transmission) {
        if (transmission != null) {
            transmission.setLogs(historySerializer.fromJson(transmission.getRawHistory()));
        }
        return transmission;
    }

}
