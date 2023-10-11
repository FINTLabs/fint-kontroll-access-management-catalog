ALTER TABLE accesspermission ADD CONSTRAINT feature_fk FOREIGN KEY (feature_id) REFERENCES feature (id);
ALTER TABLE accesspermission ADD CONSTRAINT accessrole_fk FOREIGN KEY (access_role_id) REFERENCES accessrole (access_role_id);
ALTER TABLE accesspermission ADD CONSTRAINT accesspermunique UNIQUE (access_role_id, feature_id, operation);
