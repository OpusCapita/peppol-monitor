package com.opuscapita.peppol.monitor;

import com.opuscapita.peppol.commons.container.state.ProcessFlow;
import com.opuscapita.peppol.commons.container.state.ProcessStep;
import com.opuscapita.peppol.commons.container.state.Source;
import com.opuscapita.peppol.commons.container.state.log.DocumentLog;
import com.opuscapita.peppol.commons.container.state.log.DocumentLogLevel;
import com.opuscapita.peppol.monitor.entity.Message;
import com.opuscapita.peppol.monitor.entity.MessageStatus;
import com.opuscapita.peppol.monitor.repository.MessageService;
import com.opuscapita.peppol.monitor.repository.ParticipantRepository;
import com.opuscapita.peppol.monitor.util.MessageHistorySerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MonitorDataInitializer implements CommandLineRunner {

    private final MessageService messageService;
    private final MessageHistorySerializer historySerializer;
    private final ParticipantRepository participantRepository;

    @Autowired
    public MonitorDataInitializer(MessageService messageService, MessageHistorySerializer historySerializer, ParticipantRepository participantRepository) {
        this.messageService = messageService;
        this.historySerializer = historySerializer;
        this.participantRepository = participantRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Message message = new Message();
        message.setMessageId("9f8e3770-1abc-4d94-95b8-5bd2d2ca9a05");
        message.setSource(Source.NETWORK);
        message.setDirection(ProcessFlow.IN);
        message.setArrivedAt(new Date());
        message.setFilename("9f8e3770-1abc-4d94-95b8-5bd2d2ca9a05.xml");
        message.setStatus(MessageStatus.processing);
        message.setSender("9908:919779445");
        message.setReceiver("9908:919779445");
        message.setAccessPoint("PNO000104");
        message.setDocumentType(null); // need to find a way to set this
        message.setDocumentTypeId(null);
        message.setProfileId(null);
        message.setRawHistory(historySerializer.toJson(createHistory()));

        this.messageService.saveMessage(message);
    }

    private List<DocumentLog> createHistory() {
        List<DocumentLog> logs = new ArrayList<>();
        logs.add(new DocumentLog("Received file from NETWORK", DocumentLogLevel.INFO));
        logs.get(0).setSource(ProcessStep.INBOUND);
        logs.add(new DocumentLog("Received and started processing", DocumentLogLevel.INFO));
        logs.get(1).setSource(ProcessStep.PROCESS);
        return logs;
    }

}
