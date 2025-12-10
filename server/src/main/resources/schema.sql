drop schema public cascade;
create schema public;

create table public.company
(
    id                     int generated always as identity
        constraint company_pk
            primary key,
    title                  varchar               not null,
    inn                    bigint                not null unique,
    company_type           varchar               not null
        constraint check_name
            check (company.company_type in ('LLC', 'JSC')),
    has_board_of_directors boolean default false not null
);

create table public.participant
(
    id         integer generated always as identity
        constraint participant_pk
            primary key,
    name       varchar not null,
    share      double precision,
    company_id integer not null
        constraint participant_company_id_fk
            references public.company (id) on delete cascade,
    type       varchar not null
        constraint check_type
            check (type in ('OWNER', 'MEMBER_OF_BOARD')),
    is_active boolean default true
);

create table public.meeting
(
    id           integer generated always as identity
        constraint meeting_pk
            primary key,
    company_id   integer not null
        constraint meeting_company_id_fk
            references public.company (id) on delete cascade,
    type         varchar not null
        constraint check_type
            check (type in ('BOD', 'FMP', 'FMS')),
    date         date    not null,
    address      varchar not null,
    secretary_id integer
        constraint meeting_participant_id_fk_2
            references public.participant (id) on DELETE cascade,
    chairman_id  integer
        constraint meeting_participant_id_fk_3
            references public.participant (id) on DELETE cascade
);

create table meeting_participant
(
    id             integer generated always as identity
        constraint meeting_participant_pk
            primary key,
    meeting_id     integer               not null
        constraint meeting_meeting_meeting_id_fk
            references meeting on delete cascade,
    participant_id integer               not null unique
        constraint meeting_participant_participant_id_fk
            references participant on delete cascade,
    is_present     boolean default false not null
);

create table topic
(
    id         integer generated always as identity
        constraint topic_pk
            primary key,
    title      varchar not null,
    meeting_id integer not null
        constraint topic_meeting_id_fk
            references meeting on delete cascade
);

create table voting
(
    id          integer generated always as identity
        constraint voting_pk
            primary key,
    topic_id    integer               not null
        constraint voting_topic_id_fk
            references topic on delete cascade,
    is_accepted boolean default false not null
);

create table voter
(
    id                     integer generated always as identity
        constraint voter_pk
            primary key,
    voting_id              integer               not null,
    meeting_participant_id integer               not null
        constraint voter_meeting_participant_id_fk
            references meeting_participant on delete cascade,
    topic_id               integer
        constraint voter_topic_id_fk
            references topic on delete cascade,
    vote                   varchar default 'NOT_VOTED'
        constraint check_type
            check (vote in ('YES', 'NO', 'ABSTAINED', 'NOT_VOTED')),
    is_related_party_deal  boolean default false not null
);

insert into company
values (DEFAULT, 'Company 1', 7810101010, 'LLC', false);
insert into participant
values (DEFAULT, 'Alex', 20, 1, 'OWNER', true);
insert into participant
values (DEFAULT, 'Jack', 40, 1, 'OWNER', true);
insert into meeting
values (DEFAULT, 1, 'FMP', '11.11.11', 'SPb');
insert into meeting_participant
values (DEFAULT, 1, 1, true);