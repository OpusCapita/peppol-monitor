package com.opuscapita.peppol.monitor.controller.dtos;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class ProcessPaginationDto {

    private Integer page;
    private Integer pageSize;
    private List<SortingDto> sorted;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<SortingDto> getSorted() {
        return sorted;
    }

    public void setSorted(List<SortingDto> sorted) {
        this.sorted = sorted;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static class SortingDto {
        private String id;
        private Boolean desc;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Boolean getDesc() {
            return desc;
        }

        public void setDesc(Boolean desc) {
            this.desc = desc;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }
}
