package accessmanagement

import future.keywords.contains
import future.keywords.if
import future.keywords.in
import data.datacatalog.user_assignments
import data.datacatalog.role_authorizations
# By default, deny requests.

# Allow the action if the user is granted permission to perform the action.

# Del 1
# 1. Hent brukers roller og scopes
# 2. Sjekk om bruker rolle har en rolle
# 3. Sjekk om brukerens rolle har tilgang
# 4. Sjekk om rolle har tilgang til operasjon (get, put osv)

# Del 2
# 5. Sjekk om bruker har orgunit
# 6. Sjekk om bruker har tilgang til path/uri med objecttype og orgunit

# 1
allow {
	#1
	role := user_assignments[input.user][_].rolesandscopes[_].role
	#2
	has_role(input.user, role)
    #3
	user_has_role_authorization(input.user)
    #4
    role_has_operation(role, input.operation)
}

# Return scopes for user
scopes = user_scopes {
    allow # Check if user is authorized
    some i, j
    username := input.user
    user_scopes := user_assignments[username][i].rolesandscopes[j].scopes
}

# Check if user has role
has_role(username, role) {
    some i, j
    user_data := user_assignments[username][i].rolesandscopes[j]
    user_data.role == role
}

user_has_role_authorization(username) {
    some i, j, k
    role := user_assignments[username][i].rolesandscopes[j].role
    # Check if this role exists in role_authorizations
    role_authorizations[role][k]
}

# Rule to check if a role has a particular operation
#role_has_operation(role, operation, uri) { TODO: Add uri
role_has_operation(role, operation) {
    some i
    role_data := role_authorizations[role][i]
    role_data.operation == operation
#    role_data.uri == uri TODO: Add uri
}
