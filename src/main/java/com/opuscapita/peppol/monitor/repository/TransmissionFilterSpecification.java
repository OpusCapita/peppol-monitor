package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.monitor.controller.dtos.TransmissionFilterDto;
import com.opuscapita.peppol.monitor.controller.dtos.TransmissionPaginationDto;
import com.opuscapita.peppol.monitor.entity.Transmission;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransmissionFilterSpecification {

    public static Specification<Transmission> filter(TransmissionFilterDto filterDto, List<TransmissionPaginationDto.SortingDto> sortingDtos) {
        return (Specification<Transmission>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(filterDto.getId())) {
                Predicate idPredicate = criteriaBuilder.and(
                        criteriaBuilder.like(root.get("transmissionId"), "%" + filterDto.getId() + "%")
                );
                predicates.add(idPredicate);
            }

            if (StringUtils.isNotBlank(filterDto.getMessageId())) {
                Predicate messageIdPredicate = criteriaBuilder.and(
                        criteriaBuilder.like(root.join("message").get("messageId"), "%" + filterDto.getMessageId() + "%")
                );
                predicates.add(messageIdPredicate);
            }

            if (StringUtils.isNotBlank(filterDto.getFilename())) {
                Predicate filenamePredicate = criteriaBuilder.and(
                        criteriaBuilder.like(root.get("filename"), "%" + filterDto.getFilename() + "%")
                );
                predicates.add(filenamePredicate);
            }

            if (StringUtils.isNotBlank(filterDto.getSender())) {
                Predicate senderPredicate = criteriaBuilder.and(
                        criteriaBuilder.like(root.get("sender"), "%" + filterDto.getSender() + "%")
                );
                predicates.add(senderPredicate);
            }

            if (StringUtils.isNotBlank(filterDto.getReceiver())) {
                Predicate receiverPredicate = criteriaBuilder.and(
                        criteriaBuilder.like(root.get("receiver"), "%" + filterDto.getReceiver() + "%")
                );
                predicates.add(receiverPredicate);
            }

            if (StringUtils.isNotBlank(filterDto.getAccessPoint())) {
                Predicate accessPointPredicate = criteriaBuilder.and(
                        criteriaBuilder.like(root.get("accessPoint"), "%" + filterDto.getAccessPoint() + "%")
                );
                predicates.add(accessPointPredicate);
            }

            if (StringUtils.isNotBlank(filterDto.getInvoiceNumber())) {
                Predicate invoiceNumberPredicate = criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("invoiceNumber"), filterDto.getInvoiceNumber())
                );
                predicates.add(invoiceNumberPredicate);
            }

            if (StringUtils.isNotBlank(filterDto.getHistory())) {
                Predicate historyPredicate = criteriaBuilder.and(
                        criteriaBuilder.like(root.get("rawHistory"), "%" + filterDto.getHistory() + "%")
                );
                predicates.add(historyPredicate);
            }

            if (StringUtils.isNotBlank(filterDto.getErrorType())) {
                Predicate errorTypePredicate = criteriaBuilder.and(
                        criteriaBuilder.like(root.get("rawHistory"), "%" + filterDto.getErrorType() + "%")
                );
                predicates.add(errorTypePredicate);
            }

            if (filterDto.getSources() != null && !filterDto.getSources().isEmpty()) {
                Predicate sourcePredicate = criteriaBuilder.and(
                        criteriaBuilder.in(root.get("source")).value(filterDto.getSources())
                );
                predicates.add(sourcePredicate);
            }

            if (filterDto.getDestinations() != null && !filterDto.getDestinations().isEmpty()) {
                Predicate destinationPredicate = criteriaBuilder.and(
                        criteriaBuilder.in(root.get("direction")).value(filterDto.getDestinations())
                );
                predicates.add(destinationPredicate);
            }

            if (filterDto.getDocumentTypeIds() != null && !filterDto.getDocumentTypeIds().isEmpty()) {
                Predicate documentTypeIdPredicate = criteriaBuilder.and(
                        criteriaBuilder.in(root.get("documentTypeId")).value(filterDto.getDocumentTypeIds())
                );
                predicates.add(documentTypeIdPredicate);
            }

            if (filterDto.getStatuses() != null && !filterDto.getStatuses().isEmpty()) {
                Predicate statusPredicate = criteriaBuilder.and(
                        criteriaBuilder.in(root.get("status")).value(filterDto.getStatuses())
                );
                predicates.add(statusPredicate);
            }

            if (filterDto.getStartDate() != null) {
                Predicate datePredicate = criteriaBuilder.and(
                        criteriaBuilder.greaterThan(root.<Date>get("arrivedAt"), filterDto.getStartDate())
                );
                predicates.add(datePredicate);
            }

            if (filterDto.getEndDate() != null) {
                Predicate datePredicate = criteriaBuilder.and(
                        criteriaBuilder.lessThan(root.<Date>get("arrivedAt"), filterDto.getEndDate())
                );
                predicates.add(datePredicate);
            }

            if (sortingDtos != null && !sortingDtos.isEmpty()) {
                List<Order> orderList = new ArrayList<>();
                for (TransmissionPaginationDto.SortingDto sortingDto : sortingDtos) {
                    orderList.add(sortingDto.getDesc()
                            ? criteriaBuilder.desc(root.get(sortingDto.getId()))
                            : criteriaBuilder.asc(root.get(sortingDto.getId()))
                    );
                }
                query.orderBy(orderList);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
