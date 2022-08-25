package bg.project.proteinstructuresimilaritydetection.web;

import bg.project.proteinstructuresimilaritydetection.service.SimilarityDetectionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final SimilarityDetectionService sds;

    public HomeController(SimilarityDetectionService sds) {
        this.sds = sds;
    }

    @GetMapping("/")
    public String home() {
        sds.openFile();
        return "index";
    }
}
