package bg.project.proteinstructuresimilaritydetection.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PDBFileService {

    private final String pdbUrl = "https://files.rcsb.org/view/1G2T.pdb";
    private final RestTemplate restTemplate = new RestTemplate();

    public String fetchPDBFileContent() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(pdbUrl, String.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        return null; // Handle error cases
    }
}

