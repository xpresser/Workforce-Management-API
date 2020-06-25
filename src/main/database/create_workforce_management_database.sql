CREATE TABLE USERS (
                       ID NUMBER 	GENERATED ALWAYS
                           AS IDENTITY
                           (START WITH 1
                           INCREMENT BY 1
                           MINVALUE 1
                           MAXVALUE 9223372036854775807
                           NOCYCLE
                           NOCACHE
                           ORDER) PRIMARY KEY NOT NULL,
                       EMAIL 				VARCHAR(100)NOT NULL UNIQUE,
                       USERNAME 			VARCHAR(50) NOT NULL UNIQUE,
                       PASSWORD 			VARCHAR(100) NOT NULL,
                       FIRSTNAME 			VARCHAR(50) NOT NULL,
                       LASTNAME 			VARCHAR(50) NOT NULL,
                       IS_ADMIN 			CHAR(1),
                       ON_LEAVE 			CHAR(1),
                       CREATED_AT 			TIMESTAMP 	NOT NULL,
                       CREATED_BY 			NUMBER			        ,
                       UPDATED_AT 			TIMESTAMP 	NOT NULL,
                       UPDATED_BY 			NUMBER
);


CREATE TABLE DAYS_OFF_ALLOWANCES (
                                     USER_ID 		NUMBER NOT NULL,
                                     LEAVE_DAYS_LEFT NUMBER NOT NULL,
                                     REMAINING_DAYS_OFF_KEY		VARCHAR(30) NOT NULL,


                                     CONSTRAINT PK_hashmaps PRIMARY KEY (USER_ID, REMAINING_DAYS_OFF_KEY)
);



CREATE TABLE TEAMS (
                       ID	NUMBER GENERATED ALWAYS
                           AS IDENTITY
                           (START WITH 100
                           INCREMENT BY 1
                           MINVALUE 1
                           MAXVALUE 9223372036854775807
                           NOCYCLE
                           NOCACHE
                           ORDER) PRIMARY KEY NOT NULL,
                       CREATED_BY 				NUMBER 			   NOT NULL,
                       UPDATED_BY 				NUMBER 			   NOT NULL,
                       TEAM_LEADER				NUMBER,
                       TITLE 					VARCHAR(50),
                       DESCRIPTION 				VARCHAR(50),
                       CREATED_AT 				TIMESTAMP 		   NOT NULL,
                       UPDATED_AT 				TIMESTAMP     	   NOT NULL,

                       CONSTRAINT FK_team_creator FOREIGN KEY (CREATED_BY)
                           REFERENCES Users(ID),

                       CONSTRAINT FK_team_leader FOREIGN KEY (TEAM_LEADER)
                           REFERENCES Users(ID) ON DELETE SET NULL

);

CREATE TABLE TEAM_MEMBERS (
                              TEAM_ID  	NUMBER NOT NULL,
                              USER_ID		NUMBER NOT NULL,

                              CONSTRAINT PK_TeamMembers PRIMARY KEY (TEAM_ID, USER_ID),

                              CONSTRAINT FK_teamkey FOREIGN KEY (TEAM_ID)
                                  REFERENCES TEAMS(ID)
                                      ON DELETE CASCADE,

                              CONSTRAINT FK_userkey FOREIGN KEY (USER_ID)
                                  REFERENCES USERS(ID)
                                      ON DELETE CASCADE
);


CREATE TABLE TIME_OFF_REQUESTS (
                                   ID NUMBER GENERATED ALWAYS
                                       AS IDENTITY
                                       (START WITH 100
                                       INCREMENT BY 1
                                       MINVALUE 1
                                       MAXVALUE 9223372036854775807
                                       NOCYCLE
                                       NOCACHE
                                       ORDER) PRIMARY KEY NOT NULL,

                                   REQUESTER_ID 			NUMBER NOT NULL,
                                   LEAVE_WORK_DAYS			NUMBER,
                                   LEAVE_TYPE 				VARCHAR(255),
                                   REASON 			    	VARCHAR(500),
                                   STATUS 					VARCHAR(255),
                                   START_DATE 				TIMESTAMP NOT NULL,
                                   END_DATE 				TIMESTAMP NOT NULL,
                                   CREATED_BY 				NUMBER NOT NULL,
                                   UPDATED_BY 				NUMBER NOT NULL,
                                   CREATED_AT 				TIMESTAMP NOT NULL,
                                   UPDATED_AT 				TIMESTAMP NOT NULL,

                                   CONSTRAINT FK_requester FOREIGN KEY (REQUESTER_ID)
                                       REFERENCES USERS(ID),

                                   CONSTRAINT FK_request_creator FOREIGN KEY (CREATED_BY)
                                       REFERENCES USERS(ID)

);

CREATE TABLE TIME_OFF_RESPONSES (
                                    ID NUMBER GENERATED ALWAYS
                                        AS IDENTITY
                                        (START WITH 100
                                        INCREMENT BY 1
                                        MINVALUE 1
                                        MAXVALUE 9223372036854775807
                                        NOCYCLE
                                        NOCACHE
                                        ORDER) PRIMARY KEY NOT NULL,

                                    REQUEST_ID  NUMBER   NOT NULL,
                                    APPROVER_ID NUMBER   NOT NULL,
                                    IS_APPROVED CHAR(1),

                                    CONSTRAINT FK_request FOREIGN KEY (REQUEST_ID)
                                        REFERENCES TIME_OFF_REQUESTS(ID)
                                            ON DELETE CASCADE,

                                    CONSTRAINT FK_approver FOREIGN KEY (APPROVER_ID)
                                        REFERENCES USERS(ID)
);


COMMIT;

INSERT INTO USERS (EMAIL, USERNAME, PASSWORD, FIRSTNAME, LASTNAME, IS_ADMIN, ON_LEAVE, CREATED_AT, UPDATED_AT)
VALUES ('admin@abv.bg','admin','$2a$10$XInpkvoFAxjpa/IWVf1hvOLjQpN.Y/iyYJS3986Nr0cwSaomww5xW','Admin','Admin',1,0,sysdate,sysdate);
COMMIT;
UPDATE USERS
SET CREATED_BY=1,
    UPDATED_BY=1
WHERE ID=1;
COMMIT;
ALTER TABLE DAYS_OFF_ALLOWANCES ADD CONSTRAINT FK_DAYS_OFF_ALLOWANCES_USERS
    FOREIGN KEY (USER_ID) REFERENCES USERS(ID);
ALTER TABLE USERS
    MODIFY CREATED_BY NUMBER NOT NULL;
ALTER TABLE USERS
    MODIFY UPDATED_BY NUMBER NOT NULL;
COMMIT;
CREATE TABLE GDPR_ARCHIVE
(
    ID NUMBER GENERATED ALWAYS AS IDENTITY INCREMENT BY 1 MAXVALUE 9223372036854775807 MINVALUE 1 NOCACHE ORDER NOT NULL
    , USER_ID NUMBER NOT NULL
    , USERNAME VARCHAR2(50 BYTE) NOT NULL
    , DELETION_DATE TIMESTAMP(6) NOT NULL
)
    LOGGING
    TABLESPACE USERS
    PCTFREE 10
    INITRANS 1
    STORAGE
(
    INITIAL 65536
    NEXT 1048576
    MINEXTENTS 1
    MAXEXTENTS UNLIMITED
    BUFFER_POOL DEFAULT
)
    NOCOMPRESS
    NO INMEMORY
    NOPARALLEL;

ALTER TABLE GDPR_ARCHIVE
    ADD CONSTRAINT SYS_C0010919 UNIQUE
        (
         ID
            )
        USING INDEX
            (
            CREATE UNIQUE INDEX SYS_C0010919 ON GDPR_ARCHIVE (ID ASC)
                LOGGING
                TABLESPACE USERS
                PCTFREE 10
                INITRANS 2
                STORAGE
                (
                INITIAL 65536
                NEXT 1048576
                MINEXTENTS 1
                MAXEXTENTS UNLIMITED
                BUFFER_POOL DEFAULT
                )
                NOPARALLEL
            )
        ENABLE;

ALTER TABLE GDPR_ARCHIVE
    ADD CONSTRAINT SYS_C0010920 UNIQUE
        (
         USER_ID
            )
        USING INDEX
            (
            CREATE UNIQUE INDEX SYS_C0010920 ON GDPR_ARCHIVE (USER_ID ASC)
                LOGGING
                TABLESPACE USERS
                PCTFREE 10
                INITRANS 2
                STORAGE
                (
                INITIAL 65536
                NEXT 1048576
                MINEXTENTS 1
                MAXEXTENTS UNLIMITED
                BUFFER_POOL DEFAULT
                )
                NOPARALLEL
            )
        ENABLE;
COMMIT;