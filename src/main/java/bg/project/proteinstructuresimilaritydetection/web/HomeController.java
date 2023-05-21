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

@Controller
public class HomeController {

    private final SimilarityDetectionService sds;
    private final FileStorageService fileStorageService;

    public HomeController(SimilarityDetectionService sds, FileStorageService fileStorageService) {
        this.sds = sds;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/")
    public String home(Model model) {
        if (!model.containsAttribute("addFilesModel")) {
            model.addAttribute("addFilesModel", new Files());
        }
//        sds.openFile("src/main/resources/static/1bt0.pdb", "src/main/resources/static/1eh4.pdb");
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFirstFile(@RequestParam("firstFile") MultipartFile firstFile,
                                  @RequestParam("secondFile") MultipartFile secondFile,
                                  @Valid Files files,
                                  BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        // check if file is empty
        if (firstFile.isEmpty() && secondFile.isEmpty()) {
            files.setFirstMessage("Please select a file to upload.");
            files.setSecondMessage("Please select a file to upload.");
            redirectAttributes.addFlashAttribute("addFilesModel", files);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addFilesModel", bindingResult);

            return "redirect:/";
        }
        if (firstFile.isEmpty()) {
            files.setFirstMessage("Please select a file to upload.");
            redirectAttributes.addFlashAttribute("addFilesModel", files);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addFilesModel", bindingResult);

            return "redirect:/";
        }
        if (secondFile.isEmpty()) {
            files.setSecondMessage("Please select a file to upload.");
            redirectAttributes.addFlashAttribute("addFilesModel", files);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addFilesModel", bindingResult);

            return "redirect:/";
        }
        // normalize the file path
        String firstFileName = fileStorageService.storeFile(firstFile);
        String secondFileName = fileStorageService.storeFile(secondFile);

        // return success response
        files.setFirstFileName(firstFileName);
        files.setSecondFileName(secondFileName);
        files.setFirstMessage("You successfully uploaded " + firstFileName + '!');
        files.setSecondMessage("You successfully uploaded " + secondFileName + '!');
        redirectAttributes.addFlashAttribute("addFilesModel", files);

        return "redirect:/";
    }

    @PostMapping("/second_upload")
    public String uploadSecondFile(@RequestParam("file") MultipartFile file,
                            RedirectAttributes redirectAttributes) {
        // check if file is empty
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("second_message", "Please select a file to upload.");
            return "redirect:/";
        }
        // normalize the file path
        String fileName = fileStorageService.storeFile(file);

        // return success response
        redirectAttributes.addFlashAttribute("second_message", "You successfully uploaded " + fileName + '!');
        redirectAttributes.addFlashAttribute("secondUploadedFile", fileName);

        return "redirect:/";
    }
}
