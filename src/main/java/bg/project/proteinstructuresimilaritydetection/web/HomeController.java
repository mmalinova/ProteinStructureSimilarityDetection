package bg.project.proteinstructuresimilaritydetection.web;

import bg.project.proteinstructuresimilaritydetection.model.URLs;
import bg.project.proteinstructuresimilaritydetection.service.PDBFileService;
import bg.project.proteinstructuresimilaritydetection.service.SimilarityDetectionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static bg.project.proteinstructuresimilaritydetection.constants.Constants.*;

@Controller
public class HomeController {

    private final SimilarityDetectionService similarityDetectionService;
    private final PDBFileService pdbFileService;

    public HomeController(
            SimilarityDetectionService similarityDetectionService,
            PDBFileService pdbFileService
    ) {
        this.similarityDetectionService = similarityDetectionService;
        this.pdbFileService = pdbFileService;
    }

    @GetMapping("/")
    public String home(Model model) {
        URLs URLs = new URLs();
        if (!model.containsAttribute("addModel"))
            model.addAttribute("addModel", URLs);
        return "index";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("firstFilePath") String firstFilePath,
                         @RequestParam("secondFilePath") String secondFilePath,
                         @Valid URLs URLs,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) throws InterruptedException, IOException {
        // check scoring function factor
        if (URLs.getPositiveFactor() == null || URLs.getNegativeFactor() == null) {
            URLs.setScoringMessage(SCORING_FUNCTION_FACTOR_REQUIRED);
            redirectAfterError(redirectAttributes, URLs, bindingResult);

            return "redirect:/";
        }
        // check if URLs are empty
        if (firstFilePath.isEmpty() && secondFilePath.isEmpty()) {
            URLs.setFirstMessage(FILE_REQUIRED);
            URLs.setSecondMessage(FILE_REQUIRED);
            redirectAfterError(redirectAttributes, URLs, bindingResult);

            return "redirect:/";
        }

        if (firstFilePath.isEmpty()) {
            URLs.setFirstMessage(FILE_REQUIRED);
            redirectAfterError(redirectAttributes, URLs, bindingResult);

            return "redirect:/";
        }

        if (secondFilePath.isEmpty()) {
            URLs.setSecondMessage(FILE_REQUIRED);
            redirectAfterError(redirectAttributes, URLs, bindingResult);

            return "redirect:/";
        }

        String firstFileExtension = checkFileExtension(firstFilePath);
        String secondFileExtension = checkFileExtension(secondFilePath);
        if (firstFileExtension != null && secondFileExtension != null) {
            URLs.setFirstMessage(firstFileExtension);
            URLs.setSecondMessage(secondFileExtension);
            redirectAfterError(redirectAttributes, URLs, bindingResult);

            return "redirect:/";
        }

        if (firstFileExtension != null) {
            URLs.setFirstMessage(firstFileExtension);
            redirectAfterError(redirectAttributes, URLs, bindingResult);

            return "redirect:/";
        }

        if (secondFileExtension != null) {
            URLs.setSecondMessage(secondFileExtension);
            redirectAfterError(redirectAttributes, URLs, bindingResult);

            return "redirect:/";
        }

        String firstFileContent = pdbFileService.fetchPDBFileContent(firstFilePath);
        String secondFileContent = pdbFileService.fetchPDBFileContent(secondFilePath);

        if (Objects.equals(firstFileContent, PDB_NOT_LOADED) && Objects.equals(secondFileContent, PDB_NOT_LOADED)) {
            URLs.setFirstMessage(PDB_NOT_LOADED);
            URLs.setSecondMessage(PDB_NOT_LOADED);
            redirectAfterError(redirectAttributes, URLs, bindingResult);

            return "redirect:/";
        }

        if (Objects.equals(firstFileContent, PDB_NOT_LOADED)) {
            URLs.setFirstMessage(PDB_NOT_LOADED);
            redirectAfterError(redirectAttributes, URLs, bindingResult);

            return "redirect:/";
        }

        if (Objects.equals(secondFileContent, PDB_NOT_LOADED)) {
            URLs.setSecondMessage(PDB_NOT_LOADED);
            redirectAfterError(redirectAttributes, URLs, bindingResult);

            return "redirect:/";
        }

        URLs.setFirstFilePath(firstFilePath);
        URLs.setSecondFilePath(secondFilePath);

        similarityDetectionService.openFile(firstFileContent, secondFileContent);
        URLs.setDegreeOfSimilarity(similarityDetectionService.getTheDegreeOfSimilarity());

        redirectAttributes.addFlashAttribute("addModel", URLs);

        return "redirect:/";
    }

    private String checkFileExtension(String url) {
        try {
            // Parse the URL into a URI
            URI uri = new URI(url);
            // Extract the path part from the URI
            String path = uri.getPath();
            // Create a Path object from the path
            Path filePath = Paths.get(path);
            // Get the file extension
            return getFileExtension(filePath);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return URL_ERROR;
        }
    }

    private static String getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf(".");

        if (dotIndex >= 0 && dotIndex < fileName.length() - 1) {
            String extension = fileName.substring(dotIndex + 1);
            if (!extension.equals("pdb"))
                return PDB_EXTENSION_REQUIRED;
            else
                return null;
        } else {
            return PDB_EXTENSION_REQUIRED;
        }
    }

    private void redirectAfterError(
            RedirectAttributes redirectAttributes,
            @Valid URLs URLs,
            BindingResult bindingResult) {
        URLs.setFirstFilePath(null);
        URLs.setSecondFilePath(null);
        redirectAttributes.addFlashAttribute("addModel", URLs);
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addModel", bindingResult);
    }
}
