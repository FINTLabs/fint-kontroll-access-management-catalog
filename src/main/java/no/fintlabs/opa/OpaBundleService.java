package no.fintlabs.opa;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPOutputStream;

@Slf4j
@Service
public class OpaBundleService {

    public Resource getOpaBundleFile() throws MalformedURLException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        String bundleDir = "opabundle";
        String bundleFileName = "opabundle.tar.gz";

        Path file = Paths.get(tmpDir, bundleDir, bundleFileName);

        return new UrlResource(file.toUri());
    }

    public void createOpaBundleGzip(String opaDatafilePath) throws IOException {
        String tarFile = System.getProperty("java.io.tmpdir") + "/opabundle/opabundle.tar.gz";

        File opaBundlePath = new File(tarFile);

        if (!opaBundlePath.getParentFile().exists()) {
            opaBundlePath.getParentFile().mkdirs();
        }

        log.info("Creating OPA bundle: {}", tarFile);

        try (FileOutputStream fOut = new FileOutputStream(tarFile);
             GZIPOutputStream gzOut = new GZIPOutputStream(fOut);
             TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut)) {

            addFileToTar(tOut, new FileInputStream(opaDatafilePath), "datacatalog/data.json");

            Resource resource = new ClassPathResource("opa/policy/auth.rego");

            try (InputStream in = resource.getInputStream()) {
                addFileToTar(tOut, in, "auth.rego");
            }
        }

        log.info("Done creating OPA bundle: {}", tarFile);
    }

    private void addFileToTar(TarArchiveOutputStream tOut, InputStream in, String base) throws IOException {
        TarArchiveEntry tarEntry = new TarArchiveEntry(base);
        tarEntry.setSize(in.available()); // Set the size, important for the tar to know how much to read
        tOut.putArchiveEntry(tarEntry);
        IOUtils.copy(in, tOut);
        tOut.closeArchiveEntry();
    }
}
