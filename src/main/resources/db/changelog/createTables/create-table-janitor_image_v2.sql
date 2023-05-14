CREATE TABLE IF NOT EXISTS janitor_image (
                                ID BIGSERIAL NOT NULL PRIMARY KEY ,
                                path varchar(512) NOT NULL,
                                janitor_id bigint DEFAULT NULL,
                                CONSTRAINT FK_JANITOR_IMAGE_JANITOR FOREIGN KEY (janitor_id) REFERENCES janitor (id)
);
