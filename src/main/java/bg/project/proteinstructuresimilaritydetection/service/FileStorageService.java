package bg.project.proteinstructuresimilaritydetection.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static bg.project.proteinstructuresimilaritydetection.constants.Constants.*;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(Environment env) {
        fileStorageLocation = Paths.get(env.getProperty("app.file.upload-dir",
                        "./src/main/resources/static"))
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            System.out.println(DIR_CREATION_ERROR);
        }
    }

    public Path storeFile(MultipartFile file,
                            String fileName) {
        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return targetLocation;
        } catch (IOException ex) {
            System.out.println(STORE_FILE_ERROR + fileName + TRY_AGAIN);
            return null;
        }
    }
}
