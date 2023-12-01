WITH user_id_query AS (
    SELECT resource_id FROM accessuser WHERE user_name = 'morten.solberg@vigoiks.no'
)

   , new_assignment AS (
INSERT INTO accessassignment (user_id, access_role_id)
SELECT resource_id, 'al' FROM user_id_query
                                  RETURNING id
)

, new_scopes AS (
INSERT INTO scope (objecttype)
VALUES
    ('orgunit'),
    ('user'),
    ('role'),
    ('resource'),
    ('device'),
    ('license'),
    ('allorgunits'),
    ('allusers'),
    ('allresources'),
    ('allroles'),
    ('alldevices'),
    ('alllicenses')
    RETURNING id
    )

        , insert_assignment_scope AS (
INSERT INTO assignment_scope (scope_id, assignment_id)
SELECT new_scopes.id, new_assignment.id
FROM new_scopes,
    new_assignment)

INSERT INTO scope_orgunit (scope_id, org_unit_id)
SELECT new_scopes.id, org_unit.org_unit_id
FROM new_scopes, org_unit;
