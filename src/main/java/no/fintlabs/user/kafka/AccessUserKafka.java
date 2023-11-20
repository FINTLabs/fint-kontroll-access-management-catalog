package no.fintlabs.user.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class AccessUserKafka {
    private String resourceId;
    private String userId;
    private String userName;
    private String userType;
    private String firstName;
    private String lastName;
    private String mainOrganisationUnitName;
    private String mainOrganisationUnitId;
    private String mobilePhone;
    private String email;
    private String managerRef;
    private UUID identityProviderUserObjectId;
    private List<String> organisationUnitIds;
}
