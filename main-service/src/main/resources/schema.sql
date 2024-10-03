DROP TABLE IF EXISTS users,
                     categories,
                     events,
                     locations,
                     requests,
                     compilations,
                     events_compilations,
                     ratings CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    member_name VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS categories (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS locations (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    lat FLOAT,
    lon FLOAT
);

CREATE TABLE IF NOT EXISTS events (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    created_date TIMESTAMP,
    published_on TIMESTAMP,
    event_date TIMESTAMP,
    title VARCHAR(120),
    annotation VARCHAR(2000),
    description VARCHAR(7000),
    state VARCHAR,
    category_id INTEGER NOT NULL REFERENCES categories(id),
    user_id INTEGER NOT NULL REFERENCES users(id),
    confirmed_requests INTEGER,
    participant_limit INTEGER,
    paid BOOLEAN,
    request_moderation BOOLEAN,
    location_id INTEGER NOT NULL REFERENCES locations(id)
);

CREATE TABLE IF NOT EXISTS requests (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    created_date TIMESTAMP,
    event_id INTEGER NOT NULL REFERENCES events(id),
    user_id INTEGER NOT NULL REFERENCES users(id),
    status VARCHAR(50),
    CONSTRAINT unique_event_user UNIQUE (event_id, user_id)
);

CREATE TABLE IF NOT EXISTS compilations (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    pinned BOOLEAN,
    title VARCHAR
);

CREATE TABLE IF NOT EXISTS events_compilations (
    compilation_id INTEGER NOT NULL REFERENCES compilations(id) ON DELETE CASCADE,
    event_id INTEGER NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    PRIMARY KEY (compilation_id, event_id)
);

CREATE TABLE IF NOT EXISTS ratings (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    event_id INTEGER NOT NULL REFERENCES events(id),
    user_id INTEGER NOT NULL REFERENCES users(id),
    binary_evaluation BOOLEAN,
    CONSTRAINT unique_rating UNIQUE (event_id, user_id)
);