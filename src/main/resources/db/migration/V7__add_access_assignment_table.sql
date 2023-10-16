CREATE TABLE accessassignment
(
    user_id        varchar(255),
    access_role_id varchar(255),
    scope_id       bigint,
    PRIMARY KEY (user_id, access_role_id, scope_id)
);

ALTER TABLE accessassignment ADD CONSTRAINT user_fk FOREIGN KEY (user_id) REFERENCES accessuser (resource_id);
ALTER TABLE accessassignment ADD CONSTRAINT access_role_fk FOREIGN KEY (access_role_id) REFERENCES accessrole (access_role_id);
ALTER TABLE accessassignment ADD CONSTRAINT scope_fk FOREIGN KEY (scope_id) REFERENCES scope (id);
