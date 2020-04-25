/*Account*/
CREATE TABLE account (
   id bigserial NOT NULL UNIQUE,
   email varchar(32),
   pw varchar(64) NOT NULL,
   nametemp varchar(30) NOT NULL,
   verified boolean NOT NULL DEFAULT false,
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
   id smallserial,
   name VARCHAR(30),
   PRIMARY key(id)
);

/*post*/
CREATE TABLE post(
   id serial,
   board_id bigserial,
   writer VARCHAR(30) NOT NULL,
   writetime bigint NOT NULL,
   content VARCHAR(200) NOT NULL,
   FOREIGN KEY (board_id) REFERENCES board(id),
   FOREIGN KEY (writer) REFERENCES account(id),
   PRIMARY key(id)
);

/*comment*/
CREATE TABLE comment (
   post_id bigserial,
   writer VARCHAR(30) NOT NULL,
   timewrite bigint NOT NULL,
   content_c VARCHAR(200) NOT NULL,
   FOREIGN KEY (post_id) REFERENCES post(id),
   FOREIGN KEY (writer) REFERENCES account(id)
);

/*calendar*/
CREATE TABLE memo(
   animal_id SERIAL,
   date_time bigint,
   id bigserial,
   is_word_memo boolean NOT NULL DEFAULT false,
   FOREIGN KEY (animal_id) REFERENCES animal(id),
   FOREIGN KEY (id) REFERENCES word_memo(wmemo_id),
   FOREIGN KEY (id) REFERENCES photo_memo(pmemo_id),
   PRIMARY KEY(id)
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
   images mediumblob NOT NULL,
);

/*Home*/
CREATE TABLE grouptemp (
   group_id SERIAL UNIQUE NOT NULL,
   group_name varchar(32) NOT NULL,
   owner_email varchar(32) NOT NULL,
   PRIMARY KEY (group_id)
);

/*animal specices*/
CREATE TABLE animal_species (species_id serial PRIMARY KEY);

/*animal info*/
CREATE TABLE animal (
   id SERIAL,
   species int NOT NULL,
   names varchar(30) NOT NULL,
   birth bigint NOT NULL,
   gender smallint NOT NULL,
   attributes text DEFAULT NULL,
   PRIMARY KEY (id),
   FOREIGN KEY (species) REFERENCES animal_species(species_id)
);

/* ////////////H E L P //////////*/
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
   weights_alert boolean,
   birth_alert boolean,
   event_alert boolean,
   com_comment boolean,
);