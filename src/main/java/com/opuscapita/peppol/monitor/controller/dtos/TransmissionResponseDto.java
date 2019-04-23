package com.opuscapita.peppol.monitor.controller.dtos;

import com.opuscapita.peppol.monitor.entity.Transmission;

import java.util.List;
import java.util.stream.Collectors;

public class TransmissionResponseDto {

    private Integer pages;
    private List<TransmissionDto> data;

    public TransmissionResponseDto(List<Transmission> transmissionList, Integer pages) {
        this.pages = pages;
        this.data = transmissionList.stream().map(TransmissionDto::of).collect(Collectors.toList());
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public List<TransmissionDto> getData() {
        return data;
    }

    public void setData(List<TransmissionDto> data) {
        this.data = data;
    }
}
