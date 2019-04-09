package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.monitor.controller.dtos.ProcessFilterDto;
import com.opuscapita.peppol.monitor.entity.Process;

import java.util.List;

public interface ProcessService {

    Process saveProcess(Process process);

    Process getProcess(Long id);

    Process getProcess(String transmissionId);

    List<Process> getAllProcesses(String messageId);

    List<Process> filterProcesses(ProcessFilterDto filterDto);

    void deleteProcess(Process process);

    void deleteProcess(Long id);

    List<Process> getAllProcesses(int pageNumber, int pageSize);

    long countProcesses();

}
