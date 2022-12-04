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
