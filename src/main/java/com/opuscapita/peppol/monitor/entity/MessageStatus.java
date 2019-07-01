package com.opuscapita.peppol.monitor.entity;

public enum MessageStatus {
    failed, received, processing, validating, sending, delivered, fixed, unknown;

    public boolean isFinal() {
        return this.equals(failed) || this.equals(delivered) || this.equals(fixed);
    }
}
