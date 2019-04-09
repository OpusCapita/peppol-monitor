package com.opuscapita.peppol.monitor.controller.dtos;

import com.opuscapita.peppol.monitor.entity.Process;

import java.util.List;
import java.util.stream.Collectors;

public class ProcessResponseDto {

    private Integer pages;
    private List<ProcessDto> data;

    public ProcessResponseDto(List<Process> processes, Integer pages) {
        this.pages = pages;
        this.data = processes.stream().map(ProcessDto::of).collect(Collectors.toList());
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public List<ProcessDto> getData() {
        return data;
    }

    public void setData(List<ProcessDto> data) {
        this.data = data;
    }
}
