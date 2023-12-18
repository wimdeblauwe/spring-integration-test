# Taken from https://raw.githubusercontent.com/spring-projects/spring-integration/main/spring-integration-jdbc/src/main/resources/org/springframework/integration/jdbc/schema-mysql.sql

CREATE TABLE _spring_integration_MESSAGE
(
    MESSAGE_ID    CHAR(36)     NOT NULL,
    REGION        VARCHAR(100) NOT NULL,
    CREATED_DATE  DATETIME(6)  NOT NULL,
    MESSAGE_BYTES BLOB,
    constraint _spring_integration_MESSAGE_PK primary key (MESSAGE_ID, REGION)
);

CREATE INDEX _spring_integration_MESSAGE_IX1 ON _spring_integration_MESSAGE (CREATED_DATE);

CREATE TABLE _spring_integration_GROUP_TO_MESSAGE
(
    GROUP_KEY  CHAR(36)     NOT NULL,
    MESSAGE_ID CHAR(36)     NOT NULL,
    REGION     VARCHAR(100) NOT NULL,
    constraint _spring_integration_GROUP_TO_MESSAGE_PK primary key (GROUP_KEY, MESSAGE_ID, REGION)
);

CREATE TABLE _spring_integration_MESSAGE_GROUP
(
    GROUP_KEY              CHAR(36)     NOT NULL,
    REGION                 VARCHAR(100) NOT NULL,
    GROUP_CONDITION        VARCHAR(255),
    COMPLETE               BIGINT,
    LAST_RELEASED_SEQUENCE BIGINT,
    CREATED_DATE           DATETIME(6)  NOT NULL,
    UPDATED_DATE           DATETIME(6) DEFAULT NULL,
    constraint _spring_integration_MESSAGE_GROUP_PK primary key (GROUP_KEY, REGION)
);

CREATE TABLE _spring_integration_LOCK
(
    LOCK_KEY     CHAR(36)     NOT NULL,
    REGION       VARCHAR(100) NOT NULL,
    CLIENT_ID    CHAR(36),
    CREATED_DATE DATETIME(6)  NOT NULL,
    constraint _spring_integration_LOCK_PK primary key (LOCK_KEY, REGION)
);



CREATE TABLE _spring_integration_CHANNEL_MESSAGE
(
    MESSAGE_ID       CHAR(36)     NOT NULL,
    GROUP_KEY        CHAR(36)     NOT NULL,
    CREATED_DATE     BIGINT       NOT NULL,
    MESSAGE_PRIORITY BIGINT,
    MESSAGE_SEQUENCE BIGINT       NOT NULL AUTO_INCREMENT UNIQUE,
    MESSAGE_BYTES    BLOB,
    REGION           VARCHAR(100) NOT NULL,
    constraint _spring_integration_CHANNEL_MESSAGE_PK primary key (REGION, GROUP_KEY, CREATED_DATE, MESSAGE_SEQUENCE)
);

CREATE INDEX _spring_integration_CHANNEL_MSG_DELETE_IDX ON _spring_integration_CHANNEL_MESSAGE (REGION, GROUP_KEY, MESSAGE_ID);
-- This is only needed if the message group store property 'priorityEnabled' is true
-- CREATE UNIQUE INDEX _spring_integration_CHANNEL_MSG_PRIORITY_IDX ON _spring_integration_CHANNEL_MESSAGE (REGION, GROUP_KEY, MESSAGE_PRIORITY DESC, CREATED_DATE, MESSAGE_SEQUENCE);


CREATE TABLE _spring_integration_METADATA_STORE
(
    METADATA_KEY   VARCHAR(255) NOT NULL,
    METADATA_VALUE VARCHAR(4000),
    REGION         VARCHAR(100) NOT NULL,
    constraint _spring_integration_METADATA_STORE_PK primary key (METADATA_KEY, REGION)
);
