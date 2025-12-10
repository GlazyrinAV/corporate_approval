package ru.avg.server.repository.meeting;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.avg.server.model.meeting.Meeting;
import ru.avg.server.model.meeting.MeetingType;

import java.time.LocalDate;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Integer> {

    Meeting findByCompanyIdAndTypeAndDate(Integer companyId, MeetingType type, LocalDate date);

    List<Meeting> findAllByCompanyId(Integer companyId);
}