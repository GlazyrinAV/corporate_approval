package ru.avg.server.model.voting;

import lombok.Getter;

/**
 * Enumeration of possible vote options in a voting process.
 * Each vote type has a corresponding Russian title for display purposes.
 *
 * Supported types:
 * - NOT_VOTED: Participant did not vote (НЕ ГОЛОСОВАЛ)
 * - YES: Voted in favor (ЗА)
 * - NO: Voted against (ПРОТИВ)
 * - ABSTAINED: Abstained from voting (ВОЗДЕРЖАЛСЯ)
 *
 * This enum is used in {@link ru.avg.server.model.voting.Voter} to record voting decisions.
 */
@Getter
public enum VoteType {

    NOT_VOTED("НЕ ГОЛОСОВАЛ"),
    YES("ЗА"),
    NO("ПРОТИВ"),
    ABSTAINED("ВОЗДЕРЖАЛСЯ");

    private final String title;

    /**
     * Constructs a new VoteType with the specified display title.
     *
     * @param title the Russian title of the vote type
     */
    VoteType(String title) {
        this.title = title;
    }
}