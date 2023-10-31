ALTER TABLE accessuser ADD COLUMN email varchar(255);
ALTER TABLE accessuser ADD COLUMN main_organisation_unit_id varchar(255);
ALTER TABLE accessuser ADD COLUMN main_organisation_unit_name varchar(255);
ALTER TABLE accessuser ADD COLUMN manager_ref varchar(255);
ALTER TABLE accessuser ADD COLUMN mobile_phone varchar(255);
ALTER TABLE accessuser ADD COLUMN user_type varchar(255);

CREATE TABLE accessuser_orgunit_ids
(
    user_id varchar(255),
    organisation_unit_ids varchar(255)
);

ALTER TABLE accessuser_orgunit_ids ADD CONSTRAINT user_fk FOREIGN KEY (user_id) REFERENCES accessuser (resource_id);
