ALTER TABLE org_unit ADD COLUMN name varchar(255);
ALTER TABLE org_unit ADD COLUMN short_name varchar(255);

ALTER TABLE accessuser_orgunit_ids
    ADD CONSTRAINT orgunit_fk
        FOREIGN KEY (organisation_unit_ids) REFERENCES org_unit(org_unit_id);
