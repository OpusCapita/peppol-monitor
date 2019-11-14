package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.commons.auth.AuthorizationService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransmissionServiceImpl implements TransmissionService {

    private static final Logger logger = LoggerFactory.getLogger(TransmissionServiceImpl.class);

    @Value("${peppol.storage.blob.url}")
    private String host;
    @Value("${peppol.storage.blob.port}")
    private String port;

    @Value("${peppol.auth.tenant.id}")
    private String tenant;

    private RestTemplate restTemplate;
    private AuthorizationService authService;

    private final Storage storage;
    private final TransmissionRepository repository;
    private final TransmissionHistorySerializer historySerializer;

    @Autowired
    public TransmissionServiceImpl(Storage storage, TransmissionRepository repository, TransmissionHistorySerializer historySerializer,
                                   AuthorizationService authService, RestTemplateBuilder restTemplateBuilder) {
        this.storage = storage;
        this.repository = repository;
        this.historySerializer = historySerializer;

        this.authService = authService;
        this.restTemplate = restTemplateBuilder.build();
        this.restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
    }

    @Override
    public Transmission saveTransmission(Transmission transmission) {
        return repository.save(transmission);
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
        return getFile(path);
    }

    private InputStream getFile(String path) throws StorageException {
        logger.debug("File requested from blob service for path: " + path);
        try {
            ResponseEntity<String> result = get(path, String.class);
            logger.debug("File fetched successfully from blob service for path: " + path);
            return new ByteArrayInputStream(result.getBody().getBytes());
        } catch (Exception e) {
            throw new StorageException("Error occurred while trying to read the file from blob service.", e);
        }
    }

    private <T> ResponseEntity<T> get(String path, Class<T> type) throws StorageException {
        String endpoint = getEndpoint(path, false);
        logger.debug("Reading file from endpoint: " + endpoint);

        HttpHeaders headers = new HttpHeaders();
        authService.setAuthorizationHeader(headers);
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        logger.debug("Setting http headers content type to application json");

        return restTemplate.exchange(endpoint, HttpMethod.GET, entity, type);
    }

    private String getEndpoint(String path, boolean createMissing) throws StorageException {
        if (StringUtils.isBlank(tenant)) {
            throw new StorageException("Blob service cannot be used: Missing configuration \"peppol.auth.tenant.id\".");
        }

        return UriComponentsBuilder
                .fromUriString("http://" + host)
                .port(port)
                .path("/api/" + tenant + "/files" + path)
                .queryParam("inline", "true")
                .queryParam("createMissing", String.valueOf(createMissing))
                .queryParam("recursive", "true")
                .toUriString();
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
