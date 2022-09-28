create table book(
    id bigint primary key generated always as identity,
    name varchar not null,
    publication_year smallint not null,
    status varchar not null default 'Active',
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
);

create table author(
    id bigint primary key generated always as identity,
    last_name varchar not null,
    first_name varchar not null,
    middle_name varchar,
    country varchar not null,
    date_of_birth date not null,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
);

create table book_author(
    id bigint primary key generated always as identity,
    book_id bigint references book(id) not null,
    author_id bigint references author(id) not null,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
);

create table book_loan(
    id bigint primary key generated always as identity,
    book_id bigint references book(id) not null,
    user_id bigint not null,
    status varchar not null default 'Active',
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
);

create table user_replica(
    id bigint primary key,
    status varchar not null
);

create table inbox(
    id uuid primary key,
    source varchar not null,
    type varchar not null,
    payload jsonb not null,
    status varchar not null default 'New',
    error varchar,
    processed_by varchar,
    version smallint not null default 0,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
);

create table outbox(
    id uuid primary key default gen_random_uuid(),
    aggregate_type varchar not null,
    aggregate_id varchar not null,
    type varchar not null,
    payload jsonb not null,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
);
