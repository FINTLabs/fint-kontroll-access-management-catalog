package no.fintlabs.scope;

import java.util.Arrays;
import java.util.List;

public enum ScopeObjectTypes {
    ORGUNIT("orgunit"),
    USER("user"),
    RESOURCE("resource"),
    ROLE("role"),
    DEVICE("device"),
    LICENSE("license"),
    ALLORGUNITS("allorgunits"),
    ALLUSERS("allusers"),
    ALLRESOURCES("allresources"),
    ALLROLES("allroles"),
    ALLDEVICES("alldevices"),
    ALLLICENSES("alllicenses");

    private final String objectTypeName;

    ScopeObjectTypes(String objectTypeName) {
        this.objectTypeName = objectTypeName;
    }

    public String getObjectTypeName() {
        return objectTypeName;
    }

    public static List<ScopeObjectTypes> getObjectTypesExcludingAll() {
        return Arrays.stream(values()).filter(scopeObjectTypes -> !scopeObjectTypes.objectTypeName.startsWith("all")).toList();
    }
}
