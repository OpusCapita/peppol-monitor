package com.opuscapita.peppol.monitor.consumer;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.metadata.ContainerMessageMetadata;
import com.opuscapita.peppol.commons.container.state.ProcessStep;
import com.opuscapita.peppol.commons.container.state.Source;
import com.opuscapita.peppol.monitor.entity.Message;
import com.opuscapita.peppol.monitor.entity.MessageStatus;
import com.opuscapita.peppol.monitor.entity.Process;
import com.opuscapita.peppol.monitor.repository.MessageService;
import com.opuscapita.peppol.monitor.repository.ProcessService;
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
    private ProcessService processService;

    @Autowired
    private MonitorMessageConsumer consumer;

    @Test
    @Ignore
    public void testStore() throws Exception {
        String filename = "test_" + System.currentTimeMillis() + ".xml";
        ContainerMessage cm = new ContainerMessage(filename, Source.UNKNOWN, ProcessStep.TEST);
        cm.getHistory().addError("Test Failed");

        ContainerMessageMetadata metadata = ContainerMessageMetadata.createDummy();
        String messageId = UUID.randomUUID().toString();
        String transmissionId = UUID.randomUUID().toString();
        metadata.setMessageId(messageId);
        metadata.setTransmissionId(transmissionId);
        cm.setMetadata(metadata);

        Message notExists1 = messageService.getMessage(messageId);
        assertNull(notExists1);

        Process notExists2 = processService.getProcess(transmissionId);
        assertNull(notExists2);

        consumer.consume(cm);

        Message exists1 = messageService.getMessage(messageId);
        assertNotNull(exists1);

        Process exists2 = processService.getProcess(transmissionId);
        assertNotNull(exists2);
        assertEquals(exists2.getFilename(), cm.getFileName());
        assertEquals(exists2.getStatus(), MessageStatus.failed);

        cm.getMetadata().setProfileTypeIdentifier("new-profile-type-id");
        consumer.consume(cm);

        Message updated = messageService.getMessage(messageId);
        assertNotNull(updated);
        assertEquals(updated.getProcesses().size(), 1);
        assertEquals(updated.getProcesses().get(0).getProfileId(), cm.getMetadata().getProfileTypeIdentifier());
    }

}