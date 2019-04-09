package com.opuscapita.peppol.monitor.controller.dtos;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ProcessRequestDto {

    private ProcessFilterDto filter;
    private ProcessPaginationDto pagination;

    public ProcessFilterDto getFilter() {
        return filter;
    }

    public void setFilter(ProcessFilterDto filter) {
        this.filter = filter;
    }

    public ProcessPaginationDto getPagination() {
        return pagination;
    }

    public void setPagination(ProcessPaginationDto pagination) {
        this.pagination = pagination;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
