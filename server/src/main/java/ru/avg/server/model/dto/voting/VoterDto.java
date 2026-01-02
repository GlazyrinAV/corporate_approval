package ru.avg.server.model.dto.voting;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.avg.server.model.dto.participant.MeetingParticipantDto;

/**
 * Data Transfer Object representing a vote cast by a participant in a voting session.
 * This class is used to transfer voting data between different layers of the application,
 * particularly between the controller and service layers when submitting or retrieving
 * individual votes in a voting process.
 * <p>
 * The VoterDto serves as an input DTO for creating or updating votes, containing all
 * necessary information about a participant's vote on a specific topic. It establishes
 * relationships between the vote, the voting session, the participant, and the topic,
 * while also capturing the vote type and related-party transaction status.
 * </p>
 * <p>
 * The class is annotated with Lombok's {@link Data} annotation to automatically generate
 * getters, setters, equals, hashCode, and toString methods. The {@link Builder} annotation
 * provides a fluent API for object construction, facilitating easy creation of instances
 * especially in test scenarios and service implementations.
 * </p>
 *
 * @see ru.avg.server.model.voting.Voter for the corresponding entity
 * @see ru.avg.server.model.voting.VoteType for valid vote types
 * @author AVG
 * @since 1.0
 */
@Data
@Builder
public class VoterDto {

    /**
     * Unique identifier for the vote record.
     * This field is optional and is typically null when creating a new vote,
     * as the ID is assigned by the system upon persistence. After creation,
     * this field contains the generated ID that can be used for subsequent
     * operations such as updates or deletions.
     * <p>
     * For update operations, the ID must match an existing vote record
     * to ensure proper targeting of the update operation.
     * </p>
     */
    private Integer id;

    /**
     * The identifier of the parent voting session to which this vote belongs.
     * This field establishes the relationship between the individual vote and
     * the overall voting process, ensuring proper scoping and context.
     * The votingId must not be null, ensuring referential integrity at the DTO level.
     * <p>
     * This field is mandatory for all operations involving votes, as it provides
     * the context for which voting session the vote is being cast. The ID must
     * correspond to an existing voting session record in the system.
     * </p>
     *
     * @see jakarta.validation.constraints.NotNull
     * @see ru.avg.server.model.voting.Voting
     */
    @NotNull(message = "Voting ID must not be null")
    private Integer votingId;

    /**
     * The meeting participant who is casting this vote.
     * This field contains the complete participant information including
     * their identifier, name, and other attributes, establishing who is voting.
     * The participant must not be null, ensuring that only valid participants
     * can cast votes.
     * <p>
     * The {@link Valid} annotation ensures that the nested MeetingParticipantDto
     * is also validated according to its own constraints when this DTO
     * is validated. This creates a cascading validation effect,
     * ensuring data integrity throughout the object graph.
     * </p>
     *
     * @see MeetingParticipantDto
     * @see jakarta.validation.Valid
     * @see jakarta.validation.constraints.NotNull
     */
    @Valid
    @NotNull(message = "Participant must not be null")
    private MeetingParticipantDto participant;

    /**
     * The identifier of the topic being voted on in this session.
     * This field establishes the relationship between the vote and the
     * agenda item under discussion, ensuring the vote is properly contextualized.
     * The topicId must not be null, ensuring referential integrity at the DTO level.
     * <p>
     * This field is mandatory as every vote must be associated with a specific
     * topic being discussed in the meeting. The ID must correspond to an existing
     * topic record within the same meeting context.
     * </p>
     *
     * @see jakarta.validation.constraints.NotNull
     * @see ru.avg.server.model.topic.Topic
     */
    @NotNull(message = "Topic ID must not be null")
    private Integer topicId;

    /**
     * The type of vote cast by the participant.
     * This field represents the participant's position on the topic and must
     * match one of the valid values defined in {@link ru.avg.server.model.voting.VoteType}.
     * The vote must not be null, ensuring every vote has a clear disposition.
     * <p>
     * Valid values include: "YES" (in favor), "NO" (against), "ABSTAINED" (chose not to vote),
     * and "NOT_VOTED" (did not cast a vote). The string representation corresponds
     * to the enum constants in VoteType.
     * </p>
     *
     * @see jakarta.validation.constraints.NotNull
     * @see ru.avg.server.model.voting.VoteType
     */
    @NotNull(message = "Vote type must not be null")
    private String vote;

    /**
     * Indicates whether this vote involves a related-party transaction.
     * Represents a potential conflict of interest where the participant has
     * a personal or financial interest in the outcome of the vote.
     * Defaults to false if not explicitly provided.
     * <p>
     * This field is used for regulatory compliance and corporate governance,
     * helping to identify votes that may require additional disclosure or
     * special handling according to corporate rules. It's important for
     * generating proper reporting and ensuring transparency.
     * </p>
     */
    private boolean isRelatedPartyDeal = false;
}