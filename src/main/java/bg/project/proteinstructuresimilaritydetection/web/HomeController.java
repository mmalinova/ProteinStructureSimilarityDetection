package bg.project.proteinstructuresimilaritydetection.web;

import bg.project.proteinstructuresimilaritydetection.model.Files;
import bg.project.proteinstructuresimilaritydetection.service.FileStorageService;
import bg.project.proteinstructuresimilaritydetection.service.SimilarityDetectionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Path;

import static bg.project.proteinstructuresimilaritydetection.constants.Constants.*;

@Controller
public class HomeController {

    private final SimilarityDetectionService similarityDetectionService;
    private final FileStorageService fileStorageService;
    private String first = "";
    private String second = "";

    public HomeController(
            SimilarityDetectionService similarityDetectionService,
            FileStorageService fileStorageService) {
        this.similarityDetectionService = similarityDetectionService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/")
    public String home(Model model) {
        Files file = new Files();
        if (!model.containsAttribute("addFilesModel")) {
            model.addAttribute("addFilesModel", file);
        } else {
            Files files = (Files) model.getAttribute("addFilesModel");
//            if (files != null &&
//                    files.getFirstFilePath() != null &&
//                    files.getSecondFilePath() != null)
//                similarityDetectionService.openFile(files.getFirstFilePath(), files.getSecondFilePath());
        }
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFirstFile(@RequestParam("firstFile") MultipartFile firstFile,
                                  @RequestParam("secondFile") MultipartFile secondFile,
                                  @Valid Files files,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) throws InterruptedException, IOException {
        // check scoring function factor
        if (files.getPositiveFactor().isEmpty() || files.getNegativeFactor().isEmpty()) {
            files.setScoringMessage(SCORING_FUNCTION_FACTOR_REQUIRED);
            redirectAfterError(redirectAttributes, files, bindingResult);

            return "redirect:/";
        }
        // check if files are empty
        if (firstFile.isEmpty() && secondFile.isEmpty()) {
            files.setFirstMessage(FILE_REQUIRED);
            files.setSecondMessage(FILE_REQUIRED);
            redirectAfterError(redirectAttributes, files, bindingResult);

            return "redirect:/";
        }
        if (firstFile.isEmpty()) {
            files.setFirstMessage(FILE_REQUIRED);
            redirectAfterError(redirectAttributes, files, bindingResult);

            return "redirect:/";
        }
        if (secondFile.isEmpty()) {
            files.setSecondMessage(FILE_REQUIRED);
            redirectAfterError(redirectAttributes, files, bindingResult);

            return "redirect:/";
        }

        String firstOriginalFileName = getOriginalFileName(firstFile);
        if (firstOriginalFileName.contains("Sorry")) {
            files.setFirstMessage(firstOriginalFileName);
            redirectAfterError(redirectAttributes, files, bindingResult);

            return "redirect:/";
        } else {
            files.setFirstFileName(firstOriginalFileName);
        }
        String secondOriginalFileName = getOriginalFileName(secondFile);
        if (secondOriginalFileName.contains("Sorry")) {
            files.setSecondMessage(secondOriginalFileName);
            redirectAfterError(redirectAttributes, files, bindingResult);

            return "redirect:/";
        } else {
            files.setSecondFileName(secondOriginalFileName);
        }

        String firstFileExtension = checkFileExtension(firstOriginalFileName);
        if (firstFileExtension != null) {
            files.setFirstMessage(firstFileExtension);
            redirectAfterError(redirectAttributes, files, bindingResult);

            return "redirect:/";
        }
        String secondFileExtension = checkFileExtension(secondOriginalFileName);
        if (secondFileExtension != null) {
            files.setSecondMessage(secondFileExtension);
            redirectAfterError(redirectAttributes, files, bindingResult);

            return "redirect:/";
        }

        // normalize the file path
        Path firstFilePath = fileStorageService.storeFile(firstFile, firstOriginalFileName);
        Path secondFilePath = fileStorageService.storeFile(secondFile, secondOriginalFileName);

        firstFile.getInputStream().close();
        secondFile.getInputStream().close();

        files.setFirstFilePath(firstFilePath.toString());
        files.setSecondFilePath(secondFilePath.toString());

        first = files.getFirstFileName();
        second = files.getSecondFileName();

//        similarityDetectionService.openFile(firstFilePath.toString(), secondFilePath.toString());
        files.setDegreeOfSimilarity(similarityDetectionService.getTheDegreeOfSimilarity());

        redirectAttributes.addFlashAttribute("addFilesModel", files);

        return "redirect:/";
    }

    @PostMapping("/visualization")
    public String upload(RedirectAttributes redirectAttributes) throws InterruptedException {
        Files file = new Files();

        file.setFirstFileName(first);
        file.setSecondFileName(second);

        redirectAttributes.addFlashAttribute("addFilesModel", file);

        return "redirect:/visualization";
    }

    private String getOriginalFileName(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        if (fileName == null)
            return ORIGINAL_FILE_NAME_ERROR;
        return fileName;
    }

    private String checkFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            String extension = fileName.substring(index + 1);
            if (!extension.equals("pdb"))
                return PDB_EXTENSION_REQUIRED;
            else
                return null;
        }
        return EXTENSION_REQUIRED;
    }

    private void redirectAfterError(
            RedirectAttributes redirectAttributes,
            @Valid Files files,
            BindingResult bindingResult) {
        redirectAttributes.addFlashAttribute("addFilesModel", files);
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addFilesModel", bindingResult);
    }
}
