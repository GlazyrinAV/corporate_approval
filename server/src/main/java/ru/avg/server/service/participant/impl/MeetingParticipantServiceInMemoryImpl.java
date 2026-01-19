package ru.avg.server.service.participant.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.avg.server.exception.company.CompanyNotFound;
import ru.avg.server.exception.meeting.MeetingNotFound;
import ru.avg.server.exception.meeting.MeetingTypeNotFound;
import ru.avg.server.exception.participant.MeetingParticipantNotFound;
import ru.avg.server.model.dto.meeting.MeetingDto;
import ru.avg.server.model.dto.participant.MeetingParticipantDto;
import ru.avg.server.model.dto.participant.ParticipantDto;
import ru.avg.server.model.dto.participant.mapper.MeetingParticipantMapper;
import ru.avg.server.model.dto.topic.TopicDto;
import ru.avg.server.model.dto.topic.mapper.TopicMapper;
import ru.avg.server.model.meeting.MeetingType;
import ru.avg.server.model.participant.MeetingParticipant;
import ru.avg.server.repository.participant.MeetingParticipantRepository;
import ru.avg.server.repository.topic.TopicRepository;
import ru.avg.server.service.meeting.MeetingService;
import ru.avg.server.service.participant.MeetingParticipantService;
import ru.avg.server.service.participant.ParticipantService;
import ru.avg.server.service.voting.VotingService;
import ru.avg.server.utils.verifier.Verifier;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the {@link MeetingParticipantService} interface.
 * This service manages participants within meetings, including adding participants,
 * retrieving existing and potential participants, and removing participants from meetings.
 * It also triggers voting creation for each topic when a participant is added.
 *
 * <p>This implementation ensures data isolation by scoping all operations to a specific
 * company and meeting, enforcing access control and multi-tenancy.</p>
 * </p>
 *
 * @author AVG
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class MeetingParticipantServiceInMemoryImpl implements MeetingParticipantService {

    /**
     * Mapper responsible for converting between {@link MeetingParticipant} entities and {@link MeetingParticipantDto} objects.
     */
    private final MeetingParticipantMapper meetingParticipantMapper;

    /**
     * Repository for managing persistence of {@link MeetingParticipant} entities.
     */
    private final MeetingParticipantRepository meetingParticipantRepository;

    /**
     * Service for managing topics within a meeting, used to retrieve topics when creating votings.
     */
    private final TopicRepository topicRepository;

    /**
     * Mapper for converting {@link TopicDto} to entity when creating votings.
     */
    private final TopicMapper topicMapper;

    /**
     * Service for managing voting creation when a participant is added to a meeting.
     */
    private final VotingService votingService;

    /**
     * Service for retrieving participants based on meeting type, used to find potential participants.
     */
    private final ParticipantService participantService;

    /**
     * Service for retrieving meeting details such as type and company ID, used in participant management.
     */
    private final MeetingService meetingService;

    /**
     * Utility component for verifying that the company and meeting exist and are linked correctly.
     */
    private final Verifier verifier;

    /**
     * Adds multiple participants to a specified meeting.
     * For each participant added, this method triggers the creation of votings for all topics
     * associated with the meeting to ensure voting structures are initialized.
     *
     * @param companyId    the ID of the company that owns the meeting; must not be null and must exist
     * @param meetingId    the ID of the meeting to which participants will be added; must not be null and must exist
     * @param participants the list of {@link MeetingParticipantDto} objects representing participants to add
     * @return a list of saved {@link MeetingParticipantDto} objects with generated IDs; never null
     * @throws CompanyNotFound if the specified company does not exist
     * @throws MeetingNotFound if the specified meeting does not exist
     */
    @Override
    public List<MeetingParticipantDto> save(Integer companyId, Integer meetingId, List<MeetingParticipantDto> participants) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);

        for (MeetingParticipantDto participant : participants) {
            MeetingParticipant meetingParticipant = meetingParticipantRepository.save(meetingParticipantMapper.fromDto(participant));
            List<TopicDto> topics = topicRepository.findAllByMeetingId(meetingParticipant.getMeeting().getId()).stream()
                    .map(topicMapper::toDto)
                    .toList();
            for (TopicDto topic : topics) {
                votingService.save(topicMapper.fromDto(topic));
            }
        }
        return participants;
    }

    /**
     * Retrieves all participants associated with a specific meeting.
     * The result is filtered to ensure only participants belonging to the given meeting are returned.
     *
     * @param companyId the ID of the company; used for validation
     * @param meetingId the ID of the meeting for which to retrieve participants
     * @return a list of {@link MeetingParticipantDto} objects representing all participants in the meeting;
     * returns an empty list if no participants are found
     * @throws CompanyNotFound if the specified company does not exist
     * @throws MeetingNotFound if the specified meeting does not exist
     */
    @Override
    public List<MeetingParticipantDto> findAll(Integer companyId, Integer meetingId) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);

        return meetingParticipantRepository.findAllByMeetingId(meetingId)
                .stream()
                .filter(x -> x.getMeeting().getId().equals(meetingId))
                .map(meetingParticipantMapper::toDto)
                .toList();
    }

    /**
     * Finds potential participants who can be added to a meeting based on the meeting type.
     * Excludes participants who are already part of the meeting.
     *
     * @param companyId the ID of the company; used for validation and participant lookup
     * @param meetingId the ID of the meeting; used to determine current participants and meeting type
     * @return a list of eligible {@link MeetingParticipantDto} objects who are not yet in the meeting;
     * returns an empty list if no potential participants are available
     * @throws CompanyNotFound     if the specified company does not exist
     * @throws MeetingNotFound     if the specified meeting does not exist
     * @throws MeetingTypeNotFound if the meeting type is invalid or not supported
     */
    @Override
    public List<MeetingParticipantDto> findPotential(Integer companyId, Integer meetingId) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);

        MeetingDto meetingDto = meetingService.findById(companyId, meetingId);

        // Get current participant IDs as a Set for O(1) lookup
        Set<Integer> currentParticipantIds = meetingParticipantRepository.findAllByMeetingId(meetingId)
                .stream()
                .map(mp -> mp.getParticipant().getId())
                .collect(Collectors.toSet());

        // Find meeting type
        MeetingType meetingType = Arrays.stream(MeetingType.values())
                .filter(mt -> mt.getTitle().equals(meetingDto.getType()))
                .findFirst()
                .orElseThrow(() -> new MeetingTypeNotFound(meetingDto.getType()));

        // Get potential participants and filter out those already in the meeting
        return participantService.findAllByMeetingType(meetingDto.getCompanyId(), meetingType)
                .stream()
                .filter(ParticipantDto::getIsActive)
                .map(meetingParticipantMapper::fromParticipantDto)
                .filter(dto -> !currentParticipantIds.contains(dto.getParticipant().getId()))
                .peek(dto -> dto.setMeetingId(meetingId))
                .toList();
    }

    /**
     * Retrieves a specific participant within a meeting using the participant ID.
     *
     * @param companyId     the ID of the company; used for validation
     * @param meetingId     the ID of the meeting; used for scoping
     * @param participantId the ID of the participant to retrieve
     * @return the corresponding {@link MeetingParticipantDto} if found
     * @throws CompanyNotFound            if the specified company does not exist
     * @throws MeetingNotFound            if the specified meeting does not exist
     * @throws MeetingParticipantNotFound if no participant is found with the given ID in the meeting
     */
    @Override
    public MeetingParticipantDto findByParticipantId(Integer companyId, Integer meetingId, Integer participantId) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);

        return meetingParticipantRepository.findByMeetingIdAndParticipantId(meetingId, participantId)
                .map(meetingParticipantMapper::toDto)
                .orElseThrow(() -> new MeetingParticipantNotFound(participantId));
    }

    /**
     * Removes a participant from a meeting using the meeting-participant link ID.
     *
     * @param companyId            the ID of the company; used for validation
     * @param meetingId            the ID of the meeting; used for validation
     * @param meetingParticipantId the ID of the meeting-participant association to delete
     * @throws CompanyNotFound            if the specified company does not exist
     * @throws MeetingNotFound            if the specified meeting does not exist
     * @throws MeetingParticipantNotFound if no meeting participant is found with the given ID
     */
    @Override
    public void delete(Integer companyId, Integer meetingId, Integer meetingParticipantId) {
        verifier.verifyCompanyAndMeeting(companyId, meetingId);

        if (!meetingParticipantRepository.existsById(meetingParticipantId)) {
            throw new MeetingParticipantNotFound(meetingParticipantId);
        }
        meetingParticipantRepository.deleteById(meetingParticipantId);
    }
}