package ru.avg.server.repository.voting;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.avg.server.model.voting.Voting;

public interface VotingRepository extends JpaRepository<Voting, Integer> {

    Voting findByTopicId(Integer topicId);

    void deleteByTopicId(Integer topicId);

}