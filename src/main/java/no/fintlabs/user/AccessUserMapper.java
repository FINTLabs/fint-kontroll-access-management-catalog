package no.fintlabs.user;

public class AccessUserMapper {

    public static AccessUserDto toDto(AccessUser accessUser) {
        return new AccessUserDto(accessUser.getResourceId(), accessUser.getUserId(), accessUser.getUserName(), accessUser.getFirstName(), accessUser.getLastName());
    }
}
