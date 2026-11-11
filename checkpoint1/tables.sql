CREATE TABLE user (
 username char(20) not null,
 password char(20) not null,
 PRIMARY KEY (username)
);

CREATE TABLE artifact (
 artifact_id decimal(9, 0) not null,
 views integer not null,
 username char(20) not null, -- CreatedArtifact
 PRIMARY KEY (artifact_id)
);

CREATE TABLE message (
 artifact_id decimal(9, 0) not null,
 topic_id decimal(9, 0) not null, -- MessageIn
 -- char and varchar have very limited length
 content text not null
);

CREATE TABLE externalrecord (
 artifact_id decimal(9, 0) not null,
 external_database_id decimal(9, 0) not null,
 external_identifier varchar(200) not null
);

CREATE TABLE topic (
 topic_id decimal(9, 0) not null,
 username char(20) not null, -- CreatedTopic
 PRIMARY KEY (topic_id)
);

CREATE TABLE forumthread (
 topic_id decimal(9, 0) not null,
 artifact_id decimal(9, 0) not null -- RootMessage
);

CREATE TABLE externaldatabase (
 external_database_id decimal(9, 0) not null,
 PRIMARY KEY(external_database_id)
);

CREATE TABLE follows (
 follower char(20) not null,
 following char(20) not null
);

CREATE TABLE likesartifact (
 username char(20) not null,
 artifact_id decimal(9, 0) not null
);

CREATE TABLE internallink (
 artifact_1 decimal(9, 0) not null,
 artifact_2 decimal(9, 0) not null,
 link_type char(20) not null
);
