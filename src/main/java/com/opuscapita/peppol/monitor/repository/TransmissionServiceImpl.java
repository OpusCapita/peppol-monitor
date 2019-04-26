package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.commons.container.state.log.DocumentLog;
import com.opuscapita.peppol.commons.storage.Storage;
import com.opuscapita.peppol.commons.storage.StorageException;
import com.opuscapita.peppol.monitor.controller.dtos.TransmissionFilterDto;
import com.opuscapita.peppol.monitor.controller.dtos.TransmissionRequestDto;
import com.opuscapita.peppol.monitor.entity.Transmission;
import com.opuscapita.peppol.monitor.util.TransmissionHistorySerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransmissionServiceImpl implements TransmissionService {

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
        return repository.save(transmission);
    }

    @Override
    public Transmission getTransmission(Long id) {
        Transmission transmission = repository.findById(id).orElse(null);
        if (transmission != null) {
            transmission.setLogs(historySerializer.fromJson(transmission.getRawHistory()));
        }
        return transmission;
    }

    @Override
    public Transmission getTransmission(String transmissionId) {
        return repository.findByTransmissionId(transmissionId);
    }

    @Override
    public List<Transmission> filterTransmissions(TransmissionFilterDto filterDto) {
        return repository.findAll(TransmissionFilterSpecification.filter(filterDto, null));
    }

    @Override
    public List<Transmission> getAllTransmissions(int pageNumber, int pageSize) {
        return repository.findAll(PageRequest.of(pageNumber, pageSize)).getContent();
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
    public InputStream getFileContent(Transmission transmission) throws StorageException {
        return storage.get(transmission.getFilename());
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

}