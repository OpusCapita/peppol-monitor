package com.opuscapita.peppol.monitor.consumer;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.eventing.EventReporter;
import com.opuscapita.peppol.commons.eventing.TicketReporter;
import com.opuscapita.peppol.commons.queue.MessageQueue;
import com.opuscapita.peppol.commons.queue.consume.ContainerMessageConsumer;
import com.opuscapita.peppol.commons.storage.Storage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MonitorMessageConsumer implements ContainerMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MonitorMessageConsumer.class);

    private Storage storage;
    private MessageQueue messageQueue;
    private EventReporter eventReporter;
    private TicketReporter ticketReporter;

    @Autowired
    public MonitorMessageConsumer(Storage storage, MessageQueue messageQueue,
                                  EventReporter eventReporter, TicketReporter ticketReporter) {
        this.storage = storage;
        this.messageQueue = messageQueue;
        this.eventReporter = eventReporter;
        this.ticketReporter = ticketReporter;
    }

    @Override
    public void consume(@NotNull ContainerMessage cm) throws Exception {
        logger.info("Monitor received the message: " + cm.toKibana());


    }

}
