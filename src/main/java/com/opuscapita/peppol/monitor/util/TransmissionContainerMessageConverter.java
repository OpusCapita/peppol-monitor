package com.opuscapita.peppol.monitor.util;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.metadata.ContainerMessageMetadata;
import com.opuscapita.peppol.commons.container.metadata.ContainerValidationRule;
import com.opuscapita.peppol.commons.container.state.ProcessFlow;
import com.opuscapita.peppol.commons.container.state.ProcessStep;
import com.opuscapita.peppol.monitor.entity.MessageStatus;
import com.opuscapita.peppol.monitor.entity.Transmission;
import org.springframework.stereotype.Component;

@Component
public class TransmissionContainerMessageConverter {

    public ContainerMessage convert(Transmission transmission) {
        ContainerMessage cm = new ContainerMessage(transmission.getFilename());
        cm.setSource(transmission.getSource());
        cm.setStep(convertStatusToStep(transmission));
        cm.getHistory().setLogs(transmission.getLogs());

        ContainerMessageMetadata metadata = new ContainerMessageMetadata();
        metadata.setTransmissionId(transmission.getTransmissionId());
        metadata.setMessageId(transmission.getMessage().getMessageId());
        metadata.setTimestamp(transmission.getArrivedAt());
        metadata.setSenderId(transmission.getSender());
        metadata.setRecipientId(transmission.getReceiver());
        if (ProcessFlow.IN.equals(transmission.getDirection())) {
            metadata.setSendingAccessPoint(transmission.getAccessPoint());
        } else {
            metadata.setReceivingAccessPoint(transmission.getAccessPoint());
        }
        metadata.setValidationRule(convertValidationRule(transmission));
        metadata.setDocumentTypeIdentifier(transmission.getDocumentTypeId());
        metadata.setProfileTypeIdentifier(transmission.getProfileId());
        cm.setMetadata(metadata);

        return cm;
    }

    // reverting toString result to object, from "ValidationRule {id: %s, name: %s}"
    private ContainerValidationRule convertValidationRule(Transmission transmission) {
        try {
            String ruleString = transmission.getDocumentType();
            String temp1 = ruleString.split("\\{")[1];
            temp1 = temp1.substring(0, temp1.length() - 1);
            String[] temp2 = temp1.split(", ");
            String id = temp2[0].split(": ")[1];
            String name = temp2[1].split(": ")[1];

            ContainerValidationRule rule = new ContainerValidationRule();
            rule.setId(Integer.parseInt(id));
            rule.setDescription(name);
            return rule;
        } catch (Exception ignored) {
            return null;
        }
    }

    private ProcessStep convertStatusToStep(Transmission transmission) {
        if (MessageStatus.delivered.equals(transmission.getStatus())) {
            return ProcessStep.NETWORK;
        }
        if (MessageStatus.sending.equals(transmission.getStatus())) {
            return ProcessStep.OUTBOUND;
        }
        if (MessageStatus.validating.equals(transmission.getStatus())) {
            return ProcessStep.VALIDATOR;
        }
        if (MessageStatus.processing.equals(transmission.getStatus())) {
            return ProcessStep.PROCESSOR;
        }
        if (MessageStatus.received.equals(transmission.getStatus())) {
            return ProcessStep.INBOUND;
        }
        // consider fixed status
        return ProcessStep.UNKNOWN;
    }
}
