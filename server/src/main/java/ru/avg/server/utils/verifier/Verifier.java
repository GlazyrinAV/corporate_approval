package ru.avg.server.utils.verifier;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.avg.server.exception.company.CompanyNotFound;
import ru.avg.server.exception.meeting.MeetingDoNotBelongToCompany;
import ru.avg.server.exception.meeting.MeetingNotFound;
import ru.avg.server.model.meeting.Meeting;
import ru.avg.server.repository.company.CompanyRepository;
import ru.avg.server.repository.meeting.MeetingRepository;

import java.util.Objects;

/**
 * Utility component responsible for verifying data consistency and access control between companies and meetings.
 * Ensures that operations are performed only when:
 * - The specified company exists
 * - The meeting (if provided) belongs to the specified company
 * <p>
 * This class supports multi-tenancy by enforcing ownership checks and preventing cross-company data access.
 * It is typically injected into service classes to validate business logic constraints before performing
 * data manipulation operations.
 * </p>
 *
 * @author AVG
 * @see CompanyRepository
 * @see MeetingRepository
 * @see ru.avg.server.service.company.CompanyService
 * @see ru.avg.server.service.meeting.MeetingService
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class Verifier {

    /**
     * Repository used to access and verify company entities in the persistence layer.
     * This dependency is injected by Spring via constructor injection and is used
     * to check the existence of companies by their identifier.
     * <p>
     * The repository is used in the {@link #verifyCompanyAndMeeting(Integer, Integer)} method
     * to validate that a company with the given ID exists before allowing operations
     * that involve that company.
     * </p>
     *
     * @see CompanyRepository#existsById(Object)
     * @see #verifyCompanyAndMeeting(Integer, Integer)
     */
    private final CompanyRepository companyRepository;

    /**
     * Repository used to access and verify meeting entities in the persistence layer.
     * This dependency is injected by Spring via constructor injection and is used
     * to check the existence of meetings and their association with companies.
     * <p>
     * The repository is used in the {@link #verifyCompanyAndMeeting(Integer, Integer)} method
     * to validate that a meeting with the given ID exists and belongs to the specified company.
     * </p>
     *
     * @see MeetingRepository#findById(Object)
     * @see #verifyCompanyAndMeeting(Integer, Integer)
     */
    private final MeetingRepository meetingRepository;

    /**
     * Verifies that a company with the given ID exists and, if specified, that the meeting with the given ID exists and belongs to the company.
     * <p>
     * This method first checks whether the provided company ID is not null and corresponds to an existing company
     * by calling {@link #verifyCompany(Integer)}. If the company is invalid, an exception is thrown.
     * </p>
     * <p>
     * If a meeting ID is provided (not null), the method then checks whether:
     * <ul>
     *   <li>The meeting exists in the database.</li>
     *   <li>The meeting is associated with the specified company.</li>
     * </ul>
     * If either check fails, an appropriate exception is thrown.
     * </p>
     *
     * @param companyId the unique identifier of the company to verify; must not be {@code null}
     * @param meetingId the unique identifier of the meeting to verify; may be {@code null} to skip meeting checks
     * @throws IllegalArgumentException    if the provided {@code companyId} is {@code null}
     * @throws CompanyNotFound             if no company exists with the given {@code companyId}
     * @throws MeetingNotFound             if a {@code meetingId} is provided but no meeting exists with that ID
     * @throws MeetingDoNotBelongToCompany if a {@code meetingId} is provided and the meeting exists but does not belong to the specified company
     */
    public void verifyCompanyAndMeeting(Integer companyId, Integer meetingId) {
        verifyCompany(companyId);

        if (Objects.nonNull(meetingId)) {
            Meeting meeting = meetingRepository.findById(meetingId)
                    .orElseThrow(() -> new MeetingNotFound(meetingId));

            Integer meetingCompanyId = meeting.getCompany() != null ? meeting.getCompany().getId() : null;
            if (!Objects.equals(meetingCompanyId, companyId)) {
                throw new MeetingDoNotBelongToCompany(companyId, meetingId);
            }
        }
    }

    /**
     * Verifies that a company with the given ID exists in the system.
     * <p>
     * This method checks whether the specified company ID is not null and corresponds
     * to an existing company in the database. If the ID is null or no company is found
     * with the given ID, an appropriate exception is thrown.
     * </p>
     *
     * @param companyId the unique identifier of the company to verify; must not be {@code null}
     * @throws IllegalArgumentException if the provided {@code companyId} is {@code null}
     * @throws CompanyNotFound          if no company exists with the given ID
     */
    public void verifyCompany(Integer companyId) {
        if (Objects.isNull(companyId)) {
            throw new IllegalArgumentException("Company ID must not be null");
        }

        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFound(companyId);
        }
    }
}