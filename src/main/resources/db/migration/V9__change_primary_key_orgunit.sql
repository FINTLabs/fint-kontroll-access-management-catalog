-- ALTER TABLE scope_orgunit DROP CONSTRAINT scope_orgunit_org_unit_id_fkey;
-- ALTER TABLE org_unit DROP CONSTRAINT org_unit_pkey;
-- ALTER TABLE org_unit ADD PRIMARY KEY (id);
-- ALTER TABLE scope_orgunit ALTER COLUMN org_unit_id TYPE bigint USING org_unit_id::bigint;
-- ALTER TABLE scope_orgunit ADD CONSTRAINT scope_orgunit_org_unit_id_fkey FOREIGN KEY (org_unit_id) REFERENCES org_unit (id);
