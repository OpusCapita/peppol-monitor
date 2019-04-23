package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.monitor.controller.dtos.TransmissionFilterDto;
import com.opuscapita.peppol.monitor.controller.dtos.TransmissionPaginationDto;
import com.opuscapita.peppol.monitor.entity.Transmission;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class TransmissionFilterSpecification {

    public static Specification<Transmission> filter(TransmissionFilterDto filterDto, List<TransmissionPaginationDto.SortingDto> sortingDtos) {
        return (Specification<Transmission>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(filterDto.getId())) {
                Predicate idPredicate = criteriaBuilder.and(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("transmissionId"), "%" + filterDto.getId() + "%"),
                        criteriaBuilder.like(root.join("message").get("messageId"), "%" + filterDto.getId() + "%")
                ));
                predicates.add(idPredicate);
            }

            if (StringUtils.isNotBlank(filterDto.getFilename())) {
                Predicate filenamePredicate = criteriaBuilder.and(
                        criteriaBuilder.like(root.get("filename"), "%" + filterDto.getFilename() + "%")
                );
                predicates.add(filenamePredicate);
            }

            if (StringUtils.isNotBlank(filterDto.getParticipant())) {
                Predicate participantPredicate = criteriaBuilder.and(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("sender"), "%" + filterDto.getParticipant() + "%"),
                        criteriaBuilder.like(root.get("receiver"), "%" + filterDto.getParticipant() + "%")
                ));
                predicates.add(participantPredicate);
            }

            if (StringUtils.isNotBlank(filterDto.getAccessPoint())) {
                Predicate accessPointPredicate = criteriaBuilder.and(
                        criteriaBuilder.like(root.get("accessPoint"), "%" + filterDto.getAccessPoint() + "%")
                );
                predicates.add(accessPointPredicate);
            }

            if (filterDto.getSources() != null && !filterDto.getSources().isEmpty()) {
                Predicate sourcePredicate = criteriaBuilder.and(
                        criteriaBuilder.in(root.get("source")).value(filterDto.getSources())
                );
                predicates.add(sourcePredicate);
            }

            if (filterDto.getStatuses() != null && !filterDto.getStatuses().isEmpty()) {
                Predicate statusPredicate = criteriaBuilder.and(
                        criteriaBuilder.in(root.get("status")).value(filterDto.getStatuses())
                );
                predicates.add(statusPredicate);
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
