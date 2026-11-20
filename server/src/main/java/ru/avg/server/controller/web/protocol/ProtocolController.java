package ru.avg.server.controller.web.protocol;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing meeting protocols.
 * Provides endpoints for generating, retrieving, and managing protocol documents
 * associated with corporate meetings.
 * <p>
 * This controller is mapped to the base path {@code /approval/{companyInn}/meeting/protocol}
 * and operates within the context of a specific company identified by its INN (tax identification number).
 * </p>
 *
 * @author AVG
 * @see ru.avg.server.controller.web.meeting.MeetingController
 * @since 1.0
 */
@RestController
@RequestMapping("/approval/{companyInn}/meeting/protocol")
@RequiredArgsConstructor
public class ProtocolController {
}
