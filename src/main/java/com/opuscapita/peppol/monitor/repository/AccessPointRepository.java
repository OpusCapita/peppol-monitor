package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.monitor.entity.AccessPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessPointRepository extends JpaRepository<AccessPoint, String> {

}
