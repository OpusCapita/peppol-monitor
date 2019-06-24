package com.opuscapita.peppol.monitor.controller.dtos;

public class TransmissionStatisticsDto {

    private String doc_type;
    private String direction;
    private Integer files;

    public String getDoc_type() {
        return doc_type;
    }

    public void setDoc_type(String doc_type) {
        this.doc_type = doc_type;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Integer getFiles() {
        return files;
    }

    public void setFiles(Integer files) {
        this.files = files;
    }
}
