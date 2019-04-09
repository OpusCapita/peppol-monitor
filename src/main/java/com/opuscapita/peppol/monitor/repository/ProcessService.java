package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.monitor.controller.dtos.ProcessFilterDto;
import com.opuscapita.peppol.monitor.controller.dtos.ProcessRequestDto;
import com.opuscapita.peppol.monitor.entity.Process;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProcessService {

    Process saveProcess(Process process);

    Process getProcess(Long id);

    Process getProcess(String transmissionId);

    List<Process> getAllProcesses(String messageId);

    List<Process> filterProcesses(ProcessFilterDto filterDto);

    List<Process> getAllProcesses(int pageNumber, int pageSize);

    Page<Process> getProcesses(ProcessRequestDto request);

    void deleteProcess(Process process);

    void deleteProcess(Long id);

    long countProcesses();

}
