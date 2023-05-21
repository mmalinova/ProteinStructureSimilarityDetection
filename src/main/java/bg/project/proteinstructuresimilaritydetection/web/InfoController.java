package bg.project.proteinstructuresimilaritydetection.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InfoController {

    @GetMapping("/info")
    public String info() {
        return "info";
    }
}
