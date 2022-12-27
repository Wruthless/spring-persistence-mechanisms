CREATE TABLE IF NOT EXISTS officers
(
    id          INT             NOT NULL AUTO_INCREMENT,
    rank        VARCHAR(20)     NOT NULL,
    first_name  VARCHAR(50)     NOT NULL,
    last_name   VARCHAR(50)     NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS players
(
    id          INT             NOT NULL AUTO_INCREMENT,
    band        VARCHAR(50)     NOT NULL,
    name        VARCHAR(50)     NOT NULL,
    instrument  VARCHAR(50)     NOT NULL,
    PRIMARY KEY (id)
);