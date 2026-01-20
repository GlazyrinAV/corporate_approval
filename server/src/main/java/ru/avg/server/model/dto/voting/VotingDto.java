package ru.avg.server.model.dto.voting;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object representing a voting session in the approval system.
 * This class is used to transfer voting data between different layers of the application,
 * particularly between the controller and service layers during API responses.
 * <p>
 * The VotingDto serves as an output DTO for returning voting information to clients,
 * containing key details about a voting session including its outcome and participants.
 * It is used primarily in read operations to present voting results without exposing
 * sensitive or unnecessary entity relationships.
 * </p>
 * <p>
 * The class is annotated with Lombok's {@link Data} annotation to automatically generate
 * getters, setters, equals, hashCode, and toString methods. The {@link Builder} annotation
 * provides a fluent API for object construction, facilitating easy creation of instances
 * especially in service implementations and test scenarios.
 * </p>
 *
 * @see ru.avg.server.model.voting.Voting for the corresponding entity
 * @see NewVotingDto for the creation-specific variant
 * @author AVG
 * @since 1.0
 */
@Data
@Builder
public class VotingDto {

    /**
     * The unique identifier of the voting session.
     * This field corresponds to the primary key of the voting entity in the database.
     * It is used to uniquely identify a specific voting session and is typically
     * included in API responses to support subsequent operations that reference
     * this particular voting instance.
     * <p>
     * The ID is assigned by the system upon persistence and should never be modified
     * by clients. It may be null when the DTO is used in creation contexts, but is
     * always populated in retrieval contexts.
     * </p>
     */
    private Integer id;

    /**
     * The identifier of the topic being voted on in this session.
     * This field establishes the relationship between the voting session and its
     * associated agenda item, allowing clients to understand what proposal was voted on.
     * The topicId must correspond to an existing topic in the system.
     * <p>
     * This reference is essential for maintaining context in voting results,
     * linking the abstract voting process to the specific discussion point it
     * was conducted for. The ID is used to retrieve additional information about
     * the topic when needed.
     * </p>
     */
    private Integer topicId;

    /**
     * The final outcome of the voting session.
     * This boolean field indicates whether the proposal was accepted (true) or rejected (false)
     * based on the voting results and applicable quorum rules.
     * <p>
     * The acceptance status is determined by business logic after all votes have been
     * collected and counted, considering factors such as required majority, quorum
     * requirements, and special voting rules. It represents the official decision
     * of the meeting on the matter being voted on.
     * </p>
     */
    private boolean isAccepted;

    /**
     * List of participant identifiers who took part in this voting session.
     * This field contains the IDs of all participants who cast votes during the session,
     * providing information about participation without exposing detailed vote data.
     * <p>
     * The list may be empty if no participants voted, though typically voting sessions
     * should have at least some participants. Each ID in the list corresponds to a
     * valid participant in the system and can be used to retrieve additional information
     * about the voters through participant service endpoints.
     * </p>
     */
    private List<Integer> votersId;
}