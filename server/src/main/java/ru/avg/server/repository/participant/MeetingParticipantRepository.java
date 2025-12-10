package ru.avg.server.repository.participant;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.avg.server.model.participant.MeetingParticipant;

import java.util.List;

public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Integer> {

    List<MeetingParticipant> save(List<MeetingParticipant> meetingParticipants);

    List<MeetingParticipant> findAllByMeetingId(Integer meetingId);

    MeetingParticipant findByMeetingIdAndParticipantId(Integer meetingId, Integer participantId);

    List<MeetingParticipant> findByParticipantId(Integer participantId);
}