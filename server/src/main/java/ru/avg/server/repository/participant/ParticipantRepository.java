package ru.avg.server.repository.participant;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.avg.server.model.participant.Participant;
import ru.avg.server.model.participant.ParticipantType;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {

    Participant findByNameAndCompanyIdAndType(String name, Integer companyId, ParticipantType type);

    List<Participant> findAllByCompanyId(Integer companyId);
}