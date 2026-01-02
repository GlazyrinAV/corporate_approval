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
 * Data Transfer Object for creating a new meeting in the approval system.
 * This class is used as input when creating a new meeting, containing all the required information
 * for meeting initialization. It is typically used in POST requests to validate and transfer
 * meeting creation data between the controller and service layers.
 * <p>
 * The DTO includes validation constraints to ensure data integrity before processing,
 * with appropriate error messages for invalid inputs. It follows the standard pattern for
 * creation requests, excluding system-generated fields like ID that are assigned upon persistence.
 * </p>
 * <p>
 * The class is annotated with Lombok's {@link Data} annotation to automatically generate
 * getters, setters, equals, hashCode, and toString methods. The {@link Builder} annotation
 * provides a fluent API for object construction, while {@link NoArgsConstructor} and
 * {@link AllArgsConstructor} ensure compatibility with serialization frameworks and
 * dependency injection containers.
 * </p>
 *
 * @see MeetingDto for the full meeting representation including ID
 * @see ru.avg.server.model.meeting.Meeting for the corresponding entity
 * @author AVG
 * @since 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewMeetingDto {

    /**
     * The identifier of the company that will own the meeting.
     * This field establishes the relationship between the meeting and its parent company,
     * enforcing multi-tenancy by scoping the meeting to a valid company context.
     * The companyId must not be null, ensuring referential integrity at the DTO level.
     * <p>
     * This field is mandatory for creation operations and must correspond to an existing
     * company in the system. It's used by the service layer to verify access control
     * and establish proper ownership before persisting the meeting.
     * </p>
     *
     * @see jakarta.validation.constraints.NotNull
     * @see ru.avg.server.model.company.Company
     */
    @NotNull(message = "Company ID must not be null")
    private Integer companyId;

    /**
     * The type or category of the meeting being created.
     * This field represents the nature of the meeting, such as Board of Directors (BOD),
     * General Meeting of Participants (FMP), or General Meeting of Shareholders (FMS).
     * The type must not be null, empty, or contain only whitespace characters.
     * <p>
     * The value should correspond to one of the enumerated values in {@link ru.avg.server.model.meeting.MeetingType}.
     * The type determines participant eligibility rules, quorum requirements, and other
     * business logic specific to different meeting categories.
     * </p>
     *
     * @see jakarta.validation.constraints.NotBlank
     * @see ru.avg.server.model.meeting.MeetingType
     */
    @NotBlank(message = "Meeting type must not be blank")
    private String type;

    /**
     * The date when the meeting is scheduled to occur.
     * This field captures the temporal aspect of the meeting and is essential for
     * organizing and retrieving meetings chronologically.
     * The date must not be null and is expected to be in ISO-8601 format (yyyy-MM-dd)
     * when provided as a string in API requests.
     * <p>
     * The date is used for meeting scheduling, historical record keeping, and generating
     * proper documentation with correct timestamps. Past dates may be allowed for
     * recording completed meetings, while future dates are used for scheduled meetings.
     * </p>
     *
     * @see jakarta.validation.constraints.NotNull
     * @see org.springframework.format.annotation.DateTimeFormat
     * @see java.time.LocalDate
     */
    @NotNull(message = "Meeting date must not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    /**
     * The physical or virtual address where the meeting will be held.
     * This field provides location information for participants, including
     * the venue, room, building, city, or virtual meeting link (e.g., Zoom, Teams).
     * The address must not be null, empty, or contain only whitespace characters.
     * <p>
     * For physical meetings, this should include complete venue details.
     * For virtual meetings, this should contain the full URL or connection details
     * required for participants to join the meeting electronically.
     * </p>
     *
     * @see jakarta.validation.constraints.NotBlank
     */
    @NotBlank(message = "Meeting address must not be blank")
    private String address;

    /**
     * Optional identifier of the participant assigned as secretary for the meeting.
     * This field can be null if no secretary has been appointed at the time of creation.
     * <p>
     * The secretary is typically responsible for recording minutes, managing documentation,
     * and handling administrative aspects of the meeting. If specified, the ID must
     * correspond to an existing participant within the same company as the meeting.
     * </p>
     */
    private Integer secretaryId;

    /**
     * Optional identifier of the participant assigned as chairman for the meeting.
     * This field can be null if no chairman has been appointed at the time of creation.
     * <p>
     * The chairman is typically responsible for leading the meeting, ensuring proper
     * procedure is followed, recognizing speakers, and maintaining order. If specified,
     * the ID must correspond to an existing participant within the same company as the meeting.
     * </p>
     */
    private Integer chairmanId;
}