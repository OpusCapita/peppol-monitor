package com.opuscapita.peppol.monitor.reprocess;

import com.opuscapita.peppol.commons.auth.AuthorizationService;
import com.opuscapita.peppol.commons.container.metadata.AccessPointInfo;
import com.opuscapita.peppol.commons.container.metadata.ContainerMessageMetadata;
import com.opuscapita.peppol.commons.container.state.Source;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class StandaloneSender {

    private static final Logger logger = LoggerFactory.getLogger(StandaloneSender.class);

    private final RestTemplate restTemplate;
    private final AuthorizationService authService;

    @Autowired
    public StandaloneSender(AuthorizationService authService, RestTemplateBuilder restTemplateBuilder) {
        this.authService = authService;
        this.restTemplate = restTemplateBuilder.build();
    }

    public void sendFile(InputStream inputStream, String filename, Source source) throws IOException {
        String endpoint = getEndpoint(filename);
        HttpHeaders headers = new HttpHeaders();
        authService.setAuthorizationHeader(headers);
        headers.set("Transfer-Encoding", "chunked");
        headers.set("Access-Point", AccessPointInfo.parseFromCommonName(ContainerMessageMetadata.OC_AP_COMMON_NAME).getId());
        headers.set("Peppol-Source", source.name());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        HttpEntity<Resource> entity = new HttpEntity<>(new InputStreamResource(inputStream), headers);

        try {
            ResponseEntity<String> result = restTemplate.exchange(endpoint, HttpMethod.POST, entity, String.class);
            logger.debug("Send request successfully sent, got response: " + result.toString());
        } catch (Exception e) {
            throw new IOException("Error occurred while trying to send the SEND request for file: " + filename, e);
        }
    }

    private String getEndpoint(String filename) {
        String baseName = FilenameUtils.getName(filename);
        return UriComponentsBuilder
                .fromUriString("http://peppol-inbound")
                .port(3037)
                .path("/reprocess")
                .queryParam("filename", baseName)
                .toUriString();
    }
}
