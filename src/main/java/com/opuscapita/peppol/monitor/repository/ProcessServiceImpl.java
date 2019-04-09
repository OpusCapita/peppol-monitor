package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.monitor.controller.dtos.ProcessFilterDto;
import com.opuscapita.peppol.monitor.entity.Process;
import com.opuscapita.peppol.monitor.util.ProcessHistorySerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProcessServiceImpl implements ProcessService {

    private final ProcessRepository repository;
    private final ProcessHistorySerializer historySerializer;

    @Autowired
    public ProcessServiceImpl(ProcessRepository repository, ProcessHistorySerializer historySerializer) {
        this.repository = repository;
        this.historySerializer = historySerializer;
    }

    @Override
    public Process saveProcess(Process process) {
        return repository.save(process);
    }

    @Override
    public Process getProcess(Long id) {
        Process process = repository.findById(id).orElse(null);
        if (process != null) {
            process.setLogs(historySerializer.fromJson(process.getRawHistory()));
        }
        return process;
    }

    @Override
    public Process getProcess(String transmissionId) {
        return repository.findByTransmissionId(transmissionId);
    }

    @Override
    public List<Process> getAllProcesses(String messageId) {
        List<Process> processes = repository.findByMessageMessageId(messageId);
        return processes.stream().peek(process -> process.setLogs(historySerializer.fromJson(process.getRawHistory()))).collect(Collectors.toList());
    }

    @Override
    public List<Process> filterProcesses(ProcessFilterDto filterDto) {
        return repository.findAll(ProcessFilterSpecification.filter(filterDto));
    }

    @Override
    public void deleteProcess(Process process) {
        repository.delete(process);
    }

    @Override
    public void deleteProcess(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Process> getAllProcesses(int pageNumber, int pageSize) {
        return repository.findAll(PageRequest.of(pageNumber, pageSize)).getContent();
    }

    @Override
    public long countProcesses() {
        return repository.count();
    }
}
