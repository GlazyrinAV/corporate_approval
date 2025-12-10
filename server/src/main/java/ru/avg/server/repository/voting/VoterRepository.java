package ru.avg.server.repository.voting;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.avg.server.model.voting.Voter;

import java.util.List;

public interface VoterRepository extends JpaRepository<Voter, Integer> {

    List<Voter> findAllByTopic_Id(Integer topicId);
}