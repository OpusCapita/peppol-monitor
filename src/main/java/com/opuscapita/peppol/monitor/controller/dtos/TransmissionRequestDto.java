package com.opuscapita.peppol.monitor.controller.dtos;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class TransmissionRequestDto {

    private TransmissionFilterDto filter;
    private TransmissionPaginationDto pagination;

    public TransmissionFilterDto getFilter() {
        return filter;
    }

    public void setFilter(TransmissionFilterDto filter) {
        this.filter = filter;
    }

    public TransmissionPaginationDto getPagination() {
        return pagination;
    }

    public void setPagination(TransmissionPaginationDto pagination) {
        this.pagination = pagination;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
