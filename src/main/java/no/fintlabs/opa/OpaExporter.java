package no.fintlabs.opa;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.accesspermission.repository.AccessPermission;
import no.fintlabs.accesspermission.repository.AccessPermissionRepository;
import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.accessrole.AccessRoleRepository;
import no.fintlabs.opa.dto.OpaDto;
import no.fintlabs.opa.mapper.OpaDataMapper;
import no.fintlabs.user.repository.AccessUser;
import no.fintlabs.user.repository.AccessUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static no.fintlabs.util.FileHelperUtil.writeToFile;

@Component
@Slf4j
public class OpaExporter {
    private final AccessUserRepository accessUserRepository;
    private final AccessPermissionRepository accessPermissionRepository;
    private final AccessRoleRepository accessRoleRepository;

    private final OpaBundleService opaBundleService;

    @Value("${opa.jsonexport.filename}")
    private String opaFile;

    public OpaExporter(AccessUserRepository accessUserRepository, AccessPermissionRepository accessPermissionRepository, AccessRoleRepository accessRoleRepository, OpaBundleService opaBundleService) {
        this.accessUserRepository = accessUserRepository;
        this.accessPermissionRepository = accessPermissionRepository;
        this.accessRoleRepository = accessRoleRepository;
        this.opaBundleService = opaBundleService;
    }

    @Scheduled(cron = "0 */5 * * * ?") //Every 5 minutes
    public void exportOpaDataToJson() throws Exception {
        log.info("Exporting datafile for OPA");
        try {
            String opaFilePath = System.getProperty("java.io.tmpdir") + "/opa/datacatalog/" + opaFile;

            List<AccessUser> users = accessUserRepository.findAll();
            List<AccessPermission> permissions = accessPermissionRepository.findAll();
            List<AccessRole> roles = accessRoleRepository.findAll();

            OpaDto opaDto = OpaDataMapper.mapDataToOpaDto(users, permissions, roles);
            writeToFile(opaDto.getJsonAsBytes(), opaFilePath);

            opaBundleService.createOpaBundleGzip(opaFilePath);
        } catch (Exception e) {
            log.error("Failed to export datafile for OPA", e);
            throw e;
        }
    }


    public void setOpaFile(String opaFile) {
        this.opaFile = opaFile;
    }
}
