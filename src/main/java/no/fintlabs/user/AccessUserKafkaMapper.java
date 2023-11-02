package no.fintlabs.user;

public class AccessUserKafkaMapper {

    public static AccessUser toAccessUser(AccessUserKafka accessUserKafka) {
        return AccessUser.builder().resourceId(accessUserKafka.getResourceId())
                .userId(accessUserKafka.getUserId())
                .userName(accessUserKafka.getUserName())
                .userType(accessUserKafka.getUserType())
                .firstName(accessUserKafka.getFirstName())
                .lastName(accessUserKafka.getLastName())
                .mainOrganisationUnitName(accessUserKafka.getMainOrganisationUnitName())
                .mainOrganisationUnitId(accessUserKafka.getMainOrganisationUnitId())
                .mobilePhone(accessUserKafka.getMobilePhone())
                .email(accessUserKafka.getEmail())
                .managerRef(accessUserKafka.getManagerRef())
                .build();
    }
}
