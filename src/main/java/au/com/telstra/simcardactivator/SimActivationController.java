package au.com.telstra.simcardactivator;

import au.com.telstra.simcardactivator.model.SimCardRecord;
import au.com.telstra.simcardactivator.repository.SimCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/sim")
public class SimActivationController {

    @Autowired
    private SimCardRepository simCardRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/activate")
    public ResponseEntity<String> activateSim(@RequestBody Map<String, String> request) {
        String iccid = request.get("iccid");
        String customerEmail = request.get("customerEmail");

        Map<String, String> actuatorPayload = Map.of("iccid", iccid);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(actuatorPayload, headers);

        boolean success;
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "http://localhost:8444/actuate", entity, Map.class
            );
            success = (Boolean) response.getBody().get("success");
        } catch (Exception e) {
            success = false;
        }

        SimCardRecord record = new SimCardRecord(iccid, customerEmail, success);
        simCardRepository.save(record);

        return ResponseEntity.ok("Activation success: " + success);
    }

    @GetMapping("/lookup")
    public ResponseEntity<?> lookupSimCard(@RequestParam Long simCardId) {
        return simCardRepository.findById(simCardId)
                .map(record -> Map.of(
                        "iccid", record.getIccid(),
                        "customerEmail", record.getCustomerEmail(),
                        "active", record.isActive()
                ))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
