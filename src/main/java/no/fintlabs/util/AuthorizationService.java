package no.fintlabs.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthorizationService {
    /*@Autowired
    private AuthorizationClient authorizationClient;

    public List<String> getAllAutorizedOrgUnitIds() {

        List<Scope> scope = authorizationClient.getUserScopes();
        List<String> authorizedOrgIDs = scope.stream()
                .filter(s -> s.getObjectType().equals("user"))
                .map(Scope::getOrgUnits)
                .flatMap(Collection::stream)
                .toList();

        log.info("UserScopes from OPA: " + scope);
        log.info("Authorized orgUnitIDs" + authorizedOrgIDs);

        return authorizedOrgIDs;
    }*/
}
