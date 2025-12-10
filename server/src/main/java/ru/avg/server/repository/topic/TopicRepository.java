package ru.avg.server.repository.topic;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.avg.server.model.topic.Topic;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Integer> {

    List<Topic> findAllByMeeting_Id(Integer meetingId);
}