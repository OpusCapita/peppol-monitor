package com.opuscapita.peppol.monitor.reprocess;

import com.opuscapita.peppol.commons.auth.AuthorizationService;
import com.opuscapita.peppol.commons.container.state.Source;
import com.opuscapita.peppol.commons.storage.Storage;
import com.opuscapita.peppol.commons.storage.StorageException;
import com.opuscapita.peppol.monitor.entity.Process;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;

@Component
public class ReprocessManager {

    private static final Logger logger = LoggerFactory.getLogger(ReprocessManager.class);

    private final Storage storage;
    private final RestTemplate restTemplate;
    private final AuthorizationService authService;

    public ReprocessManager(Storage storage, AuthorizationService authService, RestTemplateBuilder restTemplateBuilder) {
        this.storage = storage;
        this.authService = authService;
        this.restTemplate = restTemplateBuilder.build();
    }

    public void reprocessMessage(Process process) throws IOException {
        logger.debug("Message reprocess requested for process: " + process.getTransmissionId());
        String endpoint = getEndpoint(process.getFilename(), process.getSource());
        logger.info("Sending reprocess request to endpoint: " + endpoint + " for file: " + process.getFilename());

        HttpHeaders headers = new HttpHeaders();
        authService.setAuthorizationHeader(headers);
        headers.set("Transfer-Encoding", "chunked");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        HttpEntity<Resource> entity = new HttpEntity<>(getFileContent(process), headers);

        try {
            ResponseEntity<String> result = restTemplate.exchange(endpoint, HttpMethod.PUT, entity, String.class);
            logger.debug("Reprocess request successfully sent, got response: " + result.toString());
        } catch (Exception e) {
            throw new IOException("Error occurred while trying to send the REPROCESS request for file: " + process.getFilename(), e);
        }
    }

    private InputStreamResource getFileContent(Process process) throws StorageException {
        InputStream content = storage.get(process.getFilename());
        return new InputStreamResource(content);
    }

    private String getEndpoint(String filename, Source source) {
        String baseName = FilenameUtils.getName(filename);
        return UriComponentsBuilder
                .fromUriString("http://peppol-inbound")
                .port(3037)
                .path("/reprocess")
                .queryParam("filename", baseName)
                .queryParam("source", source.name())
                .toUriString();
    }
}
