CREATE TABLE access_user_org_unit
(
    user_id varchar(255),
    org_unit_id varchar(255),
    primary key (user_id, org_unit_id),
    foreign key (user_id) references accessuser (resource_id),
    foreign key (org_unit_id) references org_unit (org_unit_id)
);
