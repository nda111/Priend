/*Account*/
CREATE TABLE account (
    email varchar(32),
    pw varchar(64) NOT NULL,
    nametemp varchar(30) NOT NULL,
    verified boolean NOT NULL DEFAULT false,
    accnt_id bigserial NOT NULL,
    auth_token varchar(64) UNIQUE DEFAULT null,
    PRIMARY KEY (email)
);

 /*Email Confirm*/   
CREATE TABLE verifying_hash (
   email varchar(32),
   hashtemp varchar(64) NOT NULL UNIQUE,
   PRIMARY KEY (email),
   FOREIGN KEY (email) REFERENCES account(email) ON DELETE CASCADE
);

/*Reset Password*/
CREATE TABLE reset_password (
    email varchar(32),
    expire_due bigint NOT NULL,
    hashtemp varchar(64) NOT NULL UNIQUE,
    used boolean NOT NULL DEFAULT false,
    PRIMARY KEY (email),
    FOREIGN KEY (email) REFERENCES account(email) ON DELETE CASCADE
);

/*board*/
CREATE TABLE board(
id varchar(32),
boardname VARCHAR(30)
);

/*post*/
CREATE TABLE post(
id VARCHAR(32),
post_id bigserial, 
writer VARCHAR(30),
writetime bigint NOT NULL,
content_p ntext, 
FOREIGN KEY (post_id) REFERENCES board(id),
FOREIGN KEY (writer) REFERENCES account(accnt_id)
);

/*comment*/
CREATE TABLE comment
(
post_id bigserial,
writer VARCHAR(30), 
timewirte bigint NOT NULL,
content_c VARCHAR(64),
FOREIGN KEY (ID) REFERENCES post(ID),
FOREIGN KEY (writer) REFERENCES account(accnt_id)
);


/*calendar*/
CREATE TABLE memo(
animal_id  SERIAL,
date_time bigint,
verified boolean NOT NULL DEFAULT false,
memo_id bigserial,
FOREIGN KEY (animal_id) REFERENCES animal(id)
);

/*word memo*/
CREATE TABLE word_memo(
wmemo_id bigserial NOT NULL,
text_content VARCHAR(64),
FOREIGN KEY (wmemo_id) REFERENCES memo(memo_id)
);

/*photo memo*/
CREATE TABLE photo_memo(
pmemo_id bigserial,
keyword text,
/*images mediumblob NOT NULL, : 이거 blob이 근데 자료형으로 인식이 안되넹..*/
);


/*Home*/
CREATE TABLE grouptemp (
group_id SERIAL UNIQUE NOT NULL,
group_name varchar(32) NOT NULL,    
owner_email varchar(32) NOT NULL,
PRIMARY KEY (group_id)
);

/*animal specicec*/
CREATE TABLE animal_species (
   species_id int PRIMARY KEY
);

/*animal info*/
CREATE TABLE animal (
   id SERIAL UNIQUE,
species int NOT NULL,  
 names varchar(30) NOT NULL,
   birth bigint NOT NULL,
   gender smallint NOT NULL,
   attributes text DEFAULT NULL,
   PRIMARY KEY (id),
   FOREIGN KEY (species) REFERENCES animal_species(species_id)
);

/*person*/
CREATE TABLE person_group (
   group_id int,
    account_email varchar(32),
    PRIMARY KEY (group_id, account_email),
    FOREIGN KEY (group_id) REFERENCES grouptemp(group_id) ON DELETE CASCADE,
    FOREIGN KEY (account_email) REFERENCES account(email) ON DELETE CASCADE
);

 /*animal*/   
CREATE TABLE animal_group (
   group_id int,
    animal_id int,
    PRIMARY KEY (animal_id),
    FOREIGN KEY (group_id) REFERENCES grouptemp(group_id) ON DELETE CASCADE,
    FOREIGN KEY (animal_id) REFERENCES animal(id) ON DELETE CASCADE
);

/*weight*/
CREATE TABLE weights (
    pet_id int,
    measured bigint,
    weights real NOT NULL,
    PRIMARY KEY (pet_id, measured),
    FOREIGN KEY (pet_id) REFERENCES animal(id) ON DELETE CASCADE
);

/*<setting>*/
CREATE TABLE setting(
weights_alert VARCHAR,
birth_alert VARCHAR,
event_alert VARCHAR,
com_comment VARCHAR,
FOREIGN KEY (weights_alert) REFERENCES weights(weights) ON DELETE CASCADE,
FOREIGN KEY (birth_alert) REFERENCES animal(birth) ON DELETE CASCADE,
FOREIGN KEY (event_alert) REFERENCES calendar(memo_id) ON DELETE CASCADE,
FOREIGN KEY (con_comment) REFERENCES comment(content_c) ON DELETE CASCADE
);