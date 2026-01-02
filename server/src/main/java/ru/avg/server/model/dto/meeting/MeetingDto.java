package ru.avg.server.model.dto.meeting;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Data Transfer Object representing a meeting in the approval system.
 * This class is used to transfer meeting data between different layers of the application,
 * particularly between the controller and service layers during API requests and responses.
 * <p>
 * The MeetingDto serves as both an input DTO for creating/updating meetings and an output DTO
 * for returning meeting information to clients. It contains all the essential fields needed
 * to represent a meeting, including its identifier, type, date, location, and key participants.
 * </p>
 * <p>
 * The class is annotated with Lombok's {@link Data} annotation to automatically generate
 * getters, setters, equals, hashCode, and toString methods. The {@link Builder} annotation
 * provides a fluent API for object construction, while {@link NoArgsConstructor} and
 * {@link AllArgsConstructor} ensure compatibility with serialization frameworks and
 * dependency injection containers.
 * </p>
 * <p>
 * All required fields are annotated with Jakarta Validation constraints to ensure data
 * integrity before processing. The {@link DateTimeFormat} annotation specifies the expected
 * date format for parsing when the DTO is used in web requests.
 * </p>
 *
 * @see ru.avg.server.model.meeting.Meeting
 * @see ru.avg.server.model.meeting.MeetingType
 * @author AVG
 * @since 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingDto {

    /**
     * The unique identifier of the meeting.
     * This field is optional and is typically null when creating a new meeting,
     * as the ID is assigned by the system upon persistence. After creation,
     * this field contains the generated ID that can be used for subsequent
     * operations such as updates or deletions.
     * <p>
     * For update operations, the ID must match an existing meeting record
     * and be associated with the specified company to maintain multi-tenancy security.
     * </p>
     */
    private Integer id;

    /**
     * The identifier of the company that owns this meeting.
     * This field establishes the relationship between the meeting and its parent company,
     * enforcing multi-tenancy by scoping all operations to a valid company context.
     * The companyId must not be null, ensuring referential integrity at the DTO level.
     *
     * @see jakarta.validation.constraints.NotNull
     */
    @NotNull(message = "Company ID must not be null")
    private Integer companyId;

    /**
     * The type or category of the meeting.
     * This field represents the nature of the meeting, such as Board of Directors (BOD),
     * General Meeting of Participants (FMP), or General Meeting of Shareholders (FMS).
     * The type must not be null, empty, or contain only whitespace characters.
     * It should correspond to one of the values defined in {@link ru.avg.server.model.meeting.MeetingType}.
     *
     * @see jakarta.validation.constraints.NotBlank
     * @see ru.avg.server.model.meeting.MeetingType
     */
    @NotBlank(message = "Meeting type must not be blank")
    private String type;

    /**
     * The date when the meeting is scheduled or was held.
     * This field captures the temporal aspect of the meeting and is essential for
     * organizing and retrieving meetings chronologically.
     * The date must not be null and is expected to be in ISO-8601 format (yyyy-MM-dd)
     * when passed as a string in API requests.
     *
     * @see jakarta.validation.constraints.NotNull
     * @see org.springframework.format.annotation.DateTimeFormat
     */
    @NotNull(message = "Meeting date must not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    /**
     * The physical or virtual address where the meeting will be or was held.
     * This field provides the location information for participants, including
     * the venue, room, or virtual meeting link.
     * The address must not be null, empty, or contain only whitespace characters.
     *
     * @see jakarta.validation.constraints.NotBlank
     */
    @NotBlank(message = "Meeting address must not be blank")
    private String address;

    /**
     * The identifier of the participant assigned as secretary for the meeting.
     * This field is optional and can be null if no secretary has been appointed
     * at the time of creation or update.
     * The secretary is typically responsible for recording minutes and managing
     * administrative aspects of the meeting.
     */
    private Integer secretaryId;

    /**
     * The identifier of the participant assigned as chairman for the meeting.
     * This field is optional and can be null if no chairman has been appointed
     * at the time of creation or update.
     * The chairman is typically responsible for leading the meeting and ensuring
     * proper procedure is followed.
     */
    private Integer chairmanId;
}