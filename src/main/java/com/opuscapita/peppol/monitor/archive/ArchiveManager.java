package com.opuscapita.peppol.monitor.archive;

import com.opuscapita.peppol.commons.storage.Storage;
import com.opuscapita.peppol.commons.storage.StorageException;
import com.opuscapita.peppol.monitor.controller.dtos.TransmissionFilterDto;
import com.opuscapita.peppol.monitor.controller.dtos.TransmissionPaginationDto;
import com.opuscapita.peppol.monitor.controller.dtos.TransmissionRequestDto;
import com.opuscapita.peppol.monitor.entity.Archive;
import com.opuscapita.peppol.monitor.entity.Transmission;
import com.opuscapita.peppol.monitor.repository.ArchiveRepository;
import com.opuscapita.peppol.monitor.repository.TransmissionRepository;
import com.opuscapita.peppol.monitor.repository.TransmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArchiveManager {

    private static final Logger logger = LoggerFactory.getLogger(ArchiveManager.class);

    @Value("${archive.period-in-months:6}")
    private Integer periodInMonths;

    @Value("${archive.block-size:1000}")
    private Integer blockSize;

    private final Storage storage;
    private final ArchiveRepository archiveRepository;
    private final TransmissionService transmissionService;
    private final TransmissionRepository transmissionRepository;

    @Autowired
    public ArchiveManager(Storage storage, ArchiveRepository archiveRepository,
                          TransmissionService transmissionService, TransmissionRepository transmissionRepository) {
        this.storage = storage;
        this.archiveRepository = archiveRepository;
        this.transmissionService = transmissionService;
        this.transmissionRepository = transmissionRepository;
    }

    public void archive() {
        TransmissionRequestDto request = initRequest();

        Page<Transmission> transmissions = transmissionService.getAllTransmissions(request);
        archiveBlock(transmissions.getContent());

        for (int i = 1; i < transmissions.getTotalPages(); i++) {
            request.getPagination().setPage(i);
            transmissions = transmissionService.getAllTransmissions(request);
            archiveBlock(transmissions.getContent());
        }
    }

    private void archiveBlock(List<Transmission> transmissionList) {
        // create and save archive records
        try {
            List<Archive> archiveList = transmissionList.stream().map(Archive::convert).collect(Collectors.toList());
            archiveRepository.saveAll(archiveList);
        } catch (Exception e) {
            logger.warn("Failed to save archive records", e);
        }

        // remove files from blob storage
        for (Transmission transmission : transmissionList) {
            try {
                storage.remove(transmission.getFilename());
            } catch (StorageException e) {
                logger.info("Failed to remove/archive file " + transmission.getFilename());
            }
        }

        // remove transmission records
        try {
            transmissionRepository.deleteInBatch(transmissionList);
        } catch (Exception e) {
            logger.warn("Failed to remove transmission records", e);
        }
    }

    private TransmissionRequestDto initRequest() {
        TransmissionRequestDto requestDto = new TransmissionRequestDto();
        TransmissionFilterDto filterDto = new TransmissionFilterDto();
        filterDto.setEndDate(getArchivingDate());
        requestDto.setFilter(filterDto);
        TransmissionPaginationDto paginationDto = new TransmissionPaginationDto();
        paginationDto.setPage(0);
        paginationDto.setPageSize(blockSize);
        requestDto.setPagination(paginationDto);
        return requestDto;
    }

    private Date getArchivingDate() {
        Calendar currentDate = Calendar.getInstance();
        currentDate.add(Calendar.MONTH, ((-1) * periodInMonths));
        return currentDate.getTime();
    }
}
