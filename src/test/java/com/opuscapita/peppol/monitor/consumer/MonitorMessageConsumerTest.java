package com.opuscapita.peppol.monitor.consumer;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.metadata.PeppolMessageMetadata;
import com.opuscapita.peppol.monitor.entity.Message;
import com.opuscapita.peppol.monitor.entity.MessageStatus;
import com.opuscapita.peppol.monitor.repository.MessageService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
public class MonitorMessageConsumerTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MonitorMessageConsumer consumer;

    @Test
    @Ignore
    public void testStore() throws Exception {
        String messageId = UUID.randomUUID().toString();

        ContainerMessage cm = new ContainerMessage();
        cm.setFileName("test_" + System.currentTimeMillis() + ".xml");
        cm.getHistory().addError("Test Failed");

        PeppolMessageMetadata metadata = PeppolMessageMetadata.createDummy();
        metadata.setMessageId(messageId);
        cm.setMetadata(metadata);

        Message notExists = messageService.getMessage(messageId);
        assertNull(notExists);

        consumer.consume(cm);

        Message exists = messageService.getMessage(messageId);
        assertNotNull(exists);
        assertEquals(exists.getFilename(), cm.getFileName());
        assertEquals(exists.getStatus(), MessageStatus.failed);

        cm.getMetadata().setProfileTypeIdentifier("new-profile-type-id");
        consumer.consume(cm);

        Message updated = messageService.getMessage(messageId);
        assertNotNull(updated);
        assertEquals(updated.getFilename(), cm.getFileName());
        assertEquals(updated.getProfileId(), cm.getMetadata().getProfileTypeIdentifier());
    }

}