package bg.project.proteinstructuresimilaritydetection.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static bg.project.proteinstructuresimilaritydetection.constants.Constants.PDB_NOT_LOADED;

@Service
public class PDBFileService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String fetchPDBFileContent(String pdbUrl) {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(pdbUrl, String.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        return PDB_NOT_LOADED;
    }
}

