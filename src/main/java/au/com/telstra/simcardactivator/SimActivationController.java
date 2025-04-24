package au.com.telstra.simcardactivator;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

    @RestController
    @RequestMapping("/sim")
    public class SimActivationController {

        private final RestTemplate restTemplate = new RestTemplate();

        @PostMapping("/activate")
        public ResponseEntity<String> activateSim(@RequestBody Map<String, String> request) {
            String iccid = request.get("iccid");
            String customerEmail = request.get("customerEmail");

            System.out.println("Received ICCID: " + iccid);
            System.out.println("Received Customer Email: " + customerEmail);

            Map<String, String> actuatorPayload = Map.of("iccid", iccid);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(actuatorPayload, headers);

            try {
                ResponseEntity<Map> response = restTemplate.postForEntity(
                        "http://localhost:8444/actuate",
                        entity,
                        Map.class
                );

                boolean success = (Boolean) response.getBody().get("success");
                System.out.println("Activation result: " + success);

                return ResponseEntity.ok("Activation success: " + success);

            } catch (Exception e) {
                System.out.println("Error during activation: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Activation failed due to internal error.");
            }
        }
    }