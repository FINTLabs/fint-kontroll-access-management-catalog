package no.fintlabs.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class FileHelperUtil {

    public static void writeToFile(byte[] fileContent, String filePath) throws IOException {
        log.info("Writing data to file: {}", filePath);

        File opaFileInTmp = new File(filePath);

        if(!opaFileInTmp.getParentFile().exists()) {
            opaFileInTmp.getParentFile().mkdirs();
        }

        try(FileWriter fileWriter = new FileWriter(opaFileInTmp)) {
            fileWriter.write(new String(fileContent));
            log.info("Wrote data to file: {}", opaFileInTmp.getAbsolutePath());
        }
    }
}
