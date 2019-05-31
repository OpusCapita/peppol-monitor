package com.opuscapita.peppol.monitor.mlrreport;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.queue.MessageQueue;
import com.opuscapita.peppol.monitor.entity.Transmission;
import com.opuscapita.peppol.monitor.util.TransmissionContainerMessageConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MlrReportManager {

    private static final Logger logger = LoggerFactory.getLogger(MlrReportManager.class);

    @Value("${peppol.mlr-reporter.queue.in.name}")
    private String mlrQueue;

    private final MessageQueue messageQueue;
    private final TransmissionContainerMessageConverter converter;

    @Autowired
    public MlrReportManager(MessageQueue messageQueue, TransmissionContainerMessageConverter converter) {
        this.converter = converter;
        this.messageQueue = messageQueue;
    }

    public void sendToMlrReporter(ContainerMessage cm) throws Exception {
        if (cm.isOutbound()) {
            messageQueue.convertAndSend(mlrQueue, cm);
            logger.debug("Monitor send the message to mlr-reporter: " + cm.getFileName());
        }
    }

    public void sendToMlrReporter(Transmission transmission) throws Exception {
        ContainerMessage cm = converter.convert(transmission);
        sendToMlrReporter(cm);
    }
}