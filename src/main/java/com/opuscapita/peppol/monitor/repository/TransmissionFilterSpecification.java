package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.monitor.controller.dtos.TransmissionFilterDto;
import com.opuscapita.peppol.monitor.controller.dtos.TransmissionPaginationDto;
import com.opuscapita.peppol.monitor.entity.Transmission;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
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
                predicates.add(createLikeCriteria(filterDto.getId(), root.get("transmissionId"), criteriaBuilder));
            }

            if (StringUtils.isNotBlank(filterDto.getMessageId())) {
                predicates.add(createLikeCriteria(filterDto.getMessageId(), root.join("message").get("messageId"), criteriaBuilder));
            }

            if (StringUtils.isNotBlank(filterDto.getFilename())) {
                predicates.add(createLikeCriteria(filterDto.getFilename(), root.get("filename"), criteriaBuilder));
            }

            if (StringUtils.isNotBlank(filterDto.getSender())) {
                predicates.add(createLikeCriteria(filterDto.getSender(), root.get("sender"), criteriaBuilder));
            }

            if (StringUtils.isNotBlank(filterDto.getReceiver())) {
                predicates.add(createLikeCriteria(filterDto.getReceiver(), root.get("receiver"), criteriaBuilder));
            }

            if (StringUtils.isNotBlank(filterDto.getAccessPoint())) {
                predicates.add(createLikeCriteria(filterDto.getAccessPoint(), root.get("accessPoint"), criteriaBuilder));
            }

            if (StringUtils.isNotBlank(filterDto.getInvoiceNumber())) {
                predicates.add(createEqualsCriteria(filterDto.getInvoiceNumber(), root.get("invoiceNumber"), criteriaBuilder));
            }

            if (StringUtils.isNotBlank(filterDto.getHistory())) {
                predicates.add(createLikeCriteria(filterDto.getHistory(), root.get("rawHistory"), criteriaBuilder));
            }

            if (StringUtils.isNotBlank(filterDto.getErrorType())) {
                predicates.add(createLikeCriteria(filterDto.getErrorType(), root.get("rawHistory"), criteriaBuilder));
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

    private static Predicate createLikeCriteria(String value, Expression<String> path, CriteriaBuilder criteriaBuilder) {
        boolean isNot = value.startsWith("!");
        return criteriaBuilder.and(
                isNot
                        ? criteriaBuilder.notLike(path, "%" + value.substring(1) + "%")
                        : criteriaBuilder.like(path, "%" + value + "%")
        );
    }

    private static Predicate createEqualsCriteria(String value, Expression<String> path, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(criteriaBuilder.equal(path, value));
    }
}
