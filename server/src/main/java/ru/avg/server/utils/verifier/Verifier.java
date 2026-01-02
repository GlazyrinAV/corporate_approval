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
     * Verifies that a company exists and, if a meeting ID is provided, that the meeting belongs to that company.
     * <p>
     * This method performs two levels of validation:
     * <ol>
     *   <li>Company existence check: verifies that a company with the provided ID exists</li>
     *   <li>Ownership validation: if a meeting ID is provided, verifies that the meeting exists
     *       and is associated with the specified company</li>
     * </ol>
     * </p>
     * <p>
     * The method is designed to be used at the beginning of service operations that require
     * both company and meeting context, ensuring proper access control and data isolation
     * in a multi-tenant environment. It throws appropriate exceptions to provide clear
     * feedback about validation failures.
     * </p>
     *
     * @param companyId the ID of the company to verify; must not be null
     * @param meetingId the ID of the meeting to check ownership for; may be null to check only company existence
     * @throws CompanyNotFound             if no company exists with the given companyId
     * @throws MeetingNotFound             if a meetingId is provided but no meeting exists with that ID
     * @throws MeetingDoNotBelongToCompany if a meetingId is provided and the meeting exists but belongs to a different company
     * @throws IllegalArgumentException    if companyId is null
     * @see CompanyRepository#existsById(Object)
     * @see MeetingRepository#findById(Object)
     */
    public void verifyCompanyAndMeeting(Integer companyId, Integer meetingId) {
        if (Objects.isNull(companyId)) {
            throw new IllegalArgumentException("Company ID must not be null");
        }

        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFound(companyId);
        }

        if (Objects.nonNull(meetingId)) {
            Meeting meeting = meetingRepository.findById(meetingId)
                    .orElseThrow(() -> new MeetingNotFound(meetingId));

            Integer meetingCompanyId = meeting.getCompany() != null ? meeting.getCompany().getId() : null;
            if (!Objects.equals(meetingCompanyId, companyId)) {
                throw new MeetingDoNotBelongToCompany(companyId, meetingId);
            }
        }
    }
}