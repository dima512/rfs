DROP TABLE IF EXISTS language;

CREATE TABLE language(
id IDENTITY NOT NULL PRIMARY KEY,
name VARCHAR(100) NOT NULL,
author VARCHAR NOT NULL,
year SMALLINT NOT NULL,
feature VARCHAR NOT NULL,
current_version VARCHAR(25) NOT NULL
);

INSERT INTO language(name, author, year, feature, current_version) VALUES('r', 'ihaka', 1993, 'statistics', '3.6.1');