Feature: SIM card activation process

  Scenario: Successful SIM card activation
    Given the SIM card ICCID is "1255789453849037777"
    And the customer email is "user1@example.com"
    When I submit a POST request to activate the SIM card
    Then the activation should be successful
    And the activation record with ID 1 should have active status true

  Scenario: Failed SIM card activation
    Given the SIM card ICCID is "8944500102198304826"
    And the customer email is "user2@example.com"
    When I submit a POST request to activate the SIM card
    Then the activation should be unsuccessful
    And the activation record with ID 2 should have active status false
