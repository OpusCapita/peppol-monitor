package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.commons.storage.StorageException;
import com.opuscapita.peppol.monitor.controller.dtos.ProcessFilterDto;
import com.opuscapita.peppol.monitor.controller.dtos.ProcessRequestDto;
import com.opuscapita.peppol.monitor.entity.Process;
import org.springframework.data.domain.Page;

import java.io.InputStream;
import java.util.List;

public interface ProcessService {

    Process saveProcess(Process process);

    Process getProcess(Long id);

    Process getProcess(String transmissionId);

    List<Process> getAllProcesses(String messageId);

    List<Process> filterProcesses(ProcessFilterDto filterDto);

    List<Process> getAllProcesses(int pageNumber, int pageSize);

    Page<Process> getProcesses(ProcessRequestDto request);

    InputStream getFileContent(Process process) throws StorageException;

    void updateFileContent(InputStream content, Process process) throws StorageException;

    void deleteProcess(Process process);

    void deleteProcess(Long id);

    long countProcesses();

}
