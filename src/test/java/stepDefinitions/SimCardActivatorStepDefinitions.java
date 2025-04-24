package stepDefinitions;

import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

public class SimCardActivatorStepDefinitions {

    private String iccid;
    private String email;
    private Long simCardId = 1L;
    private boolean activationResult;

    @Autowired
    private RestTemplate restTemplate;

    @Given("the SIM card ICCID is {string}")
    public void the_iccid_is(String iccid) {
        this.iccid = iccid;
    }

    @And("the customer email is {string}")
    public void the_email_is(String email) {
        this.email = email;
    }

    @When("I submit a POST request to activate the SIM card")
    public void submit_activation() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> request = Map.of("iccid", iccid, "customerEmail", email);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:8080/sim/activate", entity, String.class
        );

        activationResult = response.getBody().contains("true");
    }

    @Then("the activation should be successful")
    public void should_be_successful() {
        assertTrue(activationResult);
    }

    @Then("the activation should be unsuccessful")
    public void should_be_unsuccessful() {
        assertFalse(activationResult);
    }

    @And("the activation record with ID {int} should have active status {word}")
    public void check_activation_status(int id, String status) {
        simCardId = (long) id;

        ResponseEntity<Map> response = restTemplate.getForEntity(
                "http://localhost:8080/sim/lookup?simCardId=" + simCardId, Map.class
        );

        boolean isActive = (boolean) response.getBody().get("active");
        boolean expected = Boolean.parseBoolean(status);
        assertEquals(expected, isActive);
    }
}
