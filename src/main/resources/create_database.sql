CREATE DATABASE forum;

CREATE TABLE user
(
    user_id INT unsigned NOT NULL AUTO_INCREMENT,
    sub VARCHAR(255) CHARACTER SET ascii,
    given_name VARCHAR(255),
    family_name VARCHAR(255),
    email VARCHAR(255),
    PRIMARY KEY (user_id)
);