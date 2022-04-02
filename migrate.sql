DROP TABLE IF EXISTS f_user, community, community_membership, comment, post, reaction;
DROP TYPE IF EXISTS membership_type, reaction_type;

CREATE TABLE f_user
(
    id uuid,
    sub varchar(255) NOT NULL UNIQUE,
    given_name varchar(255),
    family_name varchar(255),
    email varchar(255) NOT NULL UNIQUE,
    display_name varchar(64) NOT NULL UNIQUE,
    join_date timestamp DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE community
(
    id uuid,
    name varchar(255) NOT NULL,
    token varchar(255) NOT NULL UNIQUE,
    description varchar(1024),
    creation_date timestamp DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TYPE membership_type AS ENUM('MODERATOR', 'OWNER', 'USER');
CREATE TABLE community_membership
(
    user_id uuid,
    community_id uuid,
    type membership_type NOT NULL,
    join_date timestamp DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES f_user(id),
    FOREIGN KEY (community_id) REFERENCES community(id),
    PRIMARY KEY (user_id, community_id)
);

CREATE TABLE comment
(
    id uuid,
    parent_id uuid DEFAULT NULL,
    user_id uuid NOT NULL,
    content varchar(4096) NOT NULL,
    creation_date timestamp DEFAULT CURRENT_TIMESTAMP,
    updated_date timestamp DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES comment(id),
    FOREIGN KEY (user_id) REFERENCES f_user(id),
    PRIMARY KEY (id)
);

CREATE OR REPLACE FUNCTION update_comment() RETURNS trigger AS
$update_comment$
BEGIN
    NEW.updated_date := current_timestamp;
    RETURN NEW;
END
$update_comment$
LANGUAGE plpgsql;

CREATE TRIGGER update_comment BEFORE UPDATE ON comment
    FOR EACH ROW EXECUTE FUNCTION update_comment();

CREATE TABLE post
(
    id uuid,
    comment_id uuid NOT NULL,
    community_id uuid NOT NULL,
    user_id uuid NOT NULL,
    parent_id uuid DEFAULT NULL,
    title varchar(256) NOT NULL,
    FOREIGN KEY (comment_id) REFERENCES comment(id),
    FOREIGN KEY (community_id) REFERENCES community(id),
    FOREIGN KEY (user_id) REFERENCES f_user(id),
    FOREIGN KEY (parent_id) REFERENCES post(id),
    PRIMARY KEY (id)
);

CREATE TYPE reaction_type AS ENUM('DOWN', 'UP');
CREATE TABLE reaction
(
    user_id uuid,
    comment_id uuid,
    type reaction_type NOT NULL,
    FOREIGN KEY (user_id) REFERENCES f_user(id),
    FOREIGN KEY (comment_id) REFERENCES comment(id),
    PRIMARY KEY (comment_id, user_id)
);