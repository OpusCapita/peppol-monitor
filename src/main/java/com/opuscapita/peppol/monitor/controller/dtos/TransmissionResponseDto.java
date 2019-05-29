package com.opuscapita.peppol.monitor.controller.dtos;

import com.opuscapita.peppol.monitor.entity.Transmission;

import java.util.List;
import java.util.stream.Collectors;

public class TransmissionResponseDto {

    private Long totalCount;
    private List<TransmissionDto> data;

    public TransmissionResponseDto(List<Transmission> transmissionList, Long totalCount) {
        this.totalCount = totalCount;
        this.data = transmissionList.stream().map(TransmissionDto::of).collect(Collectors.toList());
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public List<TransmissionDto> getData() {
        return data;
    }

    public void setData(List<TransmissionDto> data) {
        this.data = data;
    }
}
