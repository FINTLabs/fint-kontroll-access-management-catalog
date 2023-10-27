package no.fintlabs.opa;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class OpaBundleService {

    public Resource getOpaBundleFile() throws MalformedURLException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        String bundleDir = "opabundle";
        String bundleFileName = "opabundle.tar.gz";

        Path file = Paths.get(tmpDir, bundleDir, bundleFileName);

        return new UrlResource(file.toUri());
    }
}
