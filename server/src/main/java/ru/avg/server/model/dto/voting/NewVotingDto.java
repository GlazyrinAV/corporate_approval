package ru.avg.server.model.dto.voting;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for creating a voting session in the approval system.
 * This class is used as input when initiating a new voting process on a meeting topic,
 * typically in POST requests to validate and transfer voter data between the controller
 * and service layers.
 * <p>
 * The DTO contains a list of voter data transfer objects that will be processed to establish
 * voting relationships for a specific topic. It uses Jakarta Validation annotations to ensure
 * data integrity:
 * <ul>
 *   <li>{@link NotNull} ensures the list itself is not null</li>
 *   <li>{@link Valid} ensures each contained {@link VoterDto} is validated</li>
 * </ul>
 * </p>
 * <p>
 * The class is annotated with Lombok's {@link Data} annotation to automatically generate
 * getters, setters, equals, hashCode, and toString methods. The {@link Builder} annotation
 * provides a fluent API for object construction, facilitating easy creation of instances
 * especially in test scenarios and service implementations.
 * </p>
 *
 * @author AVG
 * @see VoterDto
 * @see VotingDto
 * @since 1.0
 */
@Data
@Builder
public class NewVotingDto {

    /**
     * List of voters participating in the voting session.
     * This field must not be null and should contain valid {@link VoterDto} objects.
     * Each voter in the list will be validated individually due to the {@link Valid} annotation.
     * Initialized with an empty ArrayList by default to prevent null pointer exceptions.
     * <p>
     * The voters list represents all participants who will cast votes in this session,
     * including their voting choices and related-party transaction status. This data
     * is used by the service layer to create the complete voting process with all
     * individual votes properly associated.
     * </p>
     *
     * @see Valid
     * @see NotNull
     * @see VoterDto
     */
    @Valid
    @NotNull(message = "List of voters must not be null")
    private List<@Valid VoterDto> voters = new ArrayList<>();
}