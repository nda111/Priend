/*Account*/
CREATE TABLE account (
   id bigserial NOT NULL UNIQUE,
   email varchar(32),
   pw varchar(64) NOT NULL,
   name varchar(30) NOT NULL,
   verified boolean NOT NULL DEFAULT false,
   auth_token varchar(64) UNIQUE DEFAULT null,
   weights_alert boolean DEFAULT TRUE,
   birth_alert boolean DEFAULT TRUE,
   event_alert boolean DEFAULT TRUE,
   com_comment boolean DEFAULT TRUE,
   PRIMARY KEY (email)
);

/*Email Confirm*/
CREATE TABLE verifying_hash (
   email varchar(32),
   hash varchar(64) NOT NULL UNIQUE,
   PRIMARY KEY (email),
   FOREIGN KEY (email) REFERENCES account(email) ON DELETE CASCADE
);

/*Reset Password*/
CREATE TABLE reset_password (
   email varchar(32),
   expire_due bigint NOT NULL,
   hash varchar(64) NOT NULL UNIQUE,
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

/*animal specices*/
CREATE TABLE animal_species (species_id serial PRIMARY KEY);

/*animal info*/
CREATE TABLE animal (
   id SERIAL,
   species int NOT NULL,
   name varchar(30) NOT NULL,
   birth bigint NOT NULL,
   gender smallint NOT NULL,
   attributes text DEFAULT NULL,
   PRIMARY KEY (id),
   FOREIGN KEY (species) REFERENCES animal_species(species_id)
);

/*group*/
CREATE TABLE group (
   id SERIAL,
   owner_id bigint,
   name VARCHAR(20) NOT NULL,
   passwd VARCHAR(64),
   PRIMARY KEY (id),
   FOREIGN KEY (owner_id) REFERENCES account(id)
);

/*participates*/
CREATE TABLE participates (
   group_id int NOT NULL,
   account_id bigint NOT NULL,
   PRIMARY KEY (group_id, account_id),
   FOREIGN KEY (group_id) REFERENCES group(id),
   FOREIGN KEY (account_id) REFERENCES account(id) 
);

/*managed*/
CREATE TABLE managed (
   group_id int NOT NULL,
   pet_id int NOT NULL,
   PRIMARY KEY (group_id, pet_id),
   FOREIGN KEY (group_id) REFERENCES group(id),
   FOREIGN KEY (pet_id) REFERENCES animal(id)
);

/*weight*/
CREATE TABLE weights (
   pet_id int,
   measured bigint,
   weights real NOT NULL,
   PRIMARY KEY (pet_id, measured),
   FOREIGN KEY (pet_id) REFERENCES animal(id) ON DELETE CASCADE
);