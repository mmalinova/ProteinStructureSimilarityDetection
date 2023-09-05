package bg.project.proteinstructuresimilaritydetection.web;

import bg.project.proteinstructuresimilaritydetection.model.Files;
import bg.project.proteinstructuresimilaritydetection.service.FileStorageService;
import bg.project.proteinstructuresimilaritydetection.service.PDBFileService;
import bg.project.proteinstructuresimilaritydetection.service.SimilarityDetectionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@Controller
public class VisualizationController {

    private final SimilarityDetectionService similarityDetectionService;
    private final FileStorageService fileStorageService;
    private PDBFileService pdbFileService;

    public VisualizationController(SimilarityDetectionService similarityDetectionService, FileStorageService fileStorageService, PDBFileService pdbFileService) {
        this.similarityDetectionService = similarityDetectionService;
        this.fileStorageService = fileStorageService;
        this.pdbFileService = pdbFileService;
    }

    @GetMapping("/visualization")
    public String visualization(Model model) throws IOException {
        Files file = new Files();
        Files files = (Files) model.getAttribute("addFilesModel");
        assert files != null;
//        Path firstContent = fileStorageService.downloadFromFileSystem(files.getFirstFileName());
//        Path secondContent = fileStorageService.downloadFromFileSystem(files.getSecondFileName());
//        files.setFirstFileName(firstContent.toString());
//        files.setSecondFileName(secondContent.toString());

//        InputStream is = new ClassPathResource(firstFilePath).getInputStream();
//        InputStream s = new ClassPathResource(secondFilePath).getInputStream();
//        try {
//            String contents = new String(FileCopyUtils.copyToByteArray(is), StandardCharsets.UTF_8);
//            String content = new String(FileCopyUtils.copyToByteArray(s), StandardCharsets.UTF_8);
//            similarityDetectionService.openFile(contents, content);
//            is.close();
//            s.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        if (!model.containsAttribute("addFilesModel")) {
            model.addAttribute("addFilesModel", file);
//            String pdbContent = pdbFileService.fetchPDBFileContent();
//            if (pdbContent != null) {
//                return pdbContent;
//            } else {
//                return "Failed to fetch PDB file content.";
//            }
        } else {
//            Files files = (Files) model.getAttribute("addFilesModel");
//            if (files != null &&
//                    files.getFirstFilePath() != null &&
//                    files.getSecondFilePath() != null)
//            {
//                InputStream is = new ClassPathResource(files.getFirstFilePath()).getInputStream();
//                InputStream s = new ClassPathResource(files.getSecondFilePath()).getInputStream();
//                try {
//                    String contents = new String(FileCopyUtils.copyToByteArray(is), StandardCharsets.UTF_8);
//                    String content = new String(FileCopyUtils.copyToByteArray(s), StandardCharsets.UTF_8);
////                    similarityDetectionService.openFile(contents, content);
//                    is.close();
//                    s.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }
        return "visualization";
    }
}
