package com.opuscapita.peppol.monitor.repository;

import com.opuscapita.peppol.monitor.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, String> {

}
