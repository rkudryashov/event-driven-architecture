create table library_user(
    id bigint primary key generated always as identity,
    last_name varchar not null,
    first_name varchar not null,
    middle_name varchar,
    email varchar not null,
    status varchar not null default 'Active',
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
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

create table inbox_unprocessed(
    id bigint primary key generated always as identity,
    message varchar not null,
    error varchar not null,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
);

create table outbox(
    id uuid primary key default gen_random_uuid(),
    aggregate_type varchar not null,
    aggregate_id varchar,
    type varchar not null,
    topic varchar not null,
    payload jsonb not null,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
);
