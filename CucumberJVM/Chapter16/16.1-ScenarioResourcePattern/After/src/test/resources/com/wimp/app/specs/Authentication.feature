Feature: Authentication

  Rule: Customers need valid password for login

    #NOTE: This represents an automated scenario that does not depend on payment gateway
    Scenario: Login is rejected for a wrong password
      When the customer attempts to log in with a wrong password
      Then the login should fail with "Invalid password"
