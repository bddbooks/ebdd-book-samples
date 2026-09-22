# Pattern Differences: 15.6-PolymorphicDriverPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/drivers/AuthenticationApiDriver.java](#srctestjavacomwimpappspecsdriversauthenticationapidriverjava)
- ➕ Added [src/test/java/com/wimp/app/specs/drivers/AuthenticationDriver.java](#srctestjavacomwimpappspecsdriversauthenticationdriverjava)
- ➕ Added [src/test/java/com/wimp/app/specs/drivers/AuthenticationServiceDriver.java](#srctestjavacomwimpappspecsdriversauthenticationservicedriverjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/AuthenticationStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsauthenticationstepdefinitionsjava)
- 📝 Modified [src/test/resources/application-test.properties](#srctestresourcesapplicationtestproperties)

## Detailed Changes

### src/test/java/com/wimp/app/specs/drivers/AuthenticationApiDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/AuthenticationApiDriver.java#L5)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/AuthenticationApiDriver.java#L8-L16)</sub>

```diff
@@ -5,13 +5,15 @@ import com.wimp.app.restapi.LoginResponse;
 import com.wimp.app.specs.support.LambdaAction;
 import com.wimp.app.specs.support.RestApiContext;
 import com.wimp.app.specs.support.TestAction;
+import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
 import org.springframework.stereotype.Component;
 import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.service.annotation.HttpExchange;
 import org.springframework.web.service.annotation.PostExchange;
 
 @Component
-public class AuthenticationApiDriver {
+@ConditionalOnProperty(name = "test.execution.test-target", havingValue = "rest-api", matchIfMissing = true)
+public class AuthenticationApiDriver implements AuthenticationDriver {
     private final RestApiContext restApiContext;
     private final AuthenticationApiClient authenticationApiClient;
 
```

### src/test/java/com/wimp/app/specs/drivers/AuthenticationDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/AuthenticationDriver.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/AuthenticationDriver.java#L1-L8)</sub>

```diff
@@ -0,0 +1,8 @@
+package com.wimp.app.specs.drivers;
+
+import com.wimp.app.restapi.LoginResponse;
+import com.wimp.app.specs.support.TestAction;
+
+public interface AuthenticationDriver {
+    TestAction<LoginResponse> login(String customerName, String password);
+}
```

### src/test/java/com/wimp/app/specs/drivers/AuthenticationServiceDriver.java

[View file](After/src/test/java/com/wimp/app/specs/drivers/AuthenticationServiceDriver.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/drivers/AuthenticationServiceDriver.java#L1-L32)</sub>

```diff
@@ -0,0 +1,32 @@
+package com.wimp.app.specs.drivers;
+
+import com.wimp.app.models.ServiceResult;
+import com.wimp.app.restapi.LoginResponse;
+import com.wimp.app.services.AuthenticationService;
+import com.wimp.app.specs.support.LambdaAction;
+import com.wimp.app.specs.support.TestAction;
+import com.wimp.app.specs.support.TestActionFailedException;
+import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
+import org.springframework.stereotype.Component;
+
+@Component
+@ConditionalOnProperty(name = "test.execution.test-target", havingValue = "service-api")
+public class AuthenticationServiceDriver implements AuthenticationDriver {
+    private final AuthenticationService authenticationService;
+
+    public AuthenticationServiceDriver(AuthenticationService authenticationService) {
+        this.authenticationService = authenticationService;
+    }
+
+    @Override
+    public TestAction<LoginResponse> login(String customerName, String password) {
+        return new LambdaAction<>("Login", "%s/%s".formatted(customerName, password), () -> {
+            ServiceResult<String> result = authenticationService.login(customerName, password);
+            if (!result.successful()) {
+                throw new TestActionFailedException(result.errorMessage());
+            }
+            return new LoginResponse(result.value(), customerName);
+        });
+    }
+}
+
```

### src/test/java/com/wimp/app/specs/stepdefinitions/AuthenticationStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/AuthenticationStepDefinitions.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/AuthenticationStepDefinitions.java#L4-L19)</sub>

```diff
@@ -1,22 +1,22 @@
 package com.wimp.app.specs.stepdefinitions;
 
 import com.wimp.app.restapi.LoginResponse;
-import com.wimp.app.specs.drivers.AuthenticationApiDriver;
+import com.wimp.app.specs.drivers.AuthenticationDriver;
 import com.wimp.app.specs.support.DomainDefaults;
 import com.wimp.app.specs.support.TestActionResult;
 import io.cucumber.java.en.*;
 
 public class AuthenticationStepDefinitions {
-    private final AuthenticationApiDriver authenticationApiDriver;
+    private final AuthenticationDriver authenticationDriver;
     private TestActionResult<LoginResponse> loginResult;
 
-    public AuthenticationStepDefinitions(AuthenticationApiDriver authenticationApiDriver) {
-        this.authenticationApiDriver = authenticationApiDriver;
+    public AuthenticationStepDefinitions(AuthenticationDriver authenticationDriver) {
+        this.authenticationDriver = authenticationDriver;
     }
 
     @When("the customer attempts to log in with a wrong password")
     public void theCustomerAttemptsToLogInWithAWrongPassword() throws Exception {
-        loginResult = authenticationApiDriver.login(DomainDefaults.CUSTOMER_NAME, DomainDefaults.WRONG_PASSWORD).attemptExecute();
+        loginResult = authenticationDriver.login(DomainDefaults.CUSTOMER_NAME, DomainDefaults.WRONG_PASSWORD).attemptExecute();
     }
 
     @Then("the login should fail with {string}")
```

### src/test/resources/application-test.properties

[View file](After/src/test/resources/application-test.properties#L1)

<sub>[Jump to change](After/src/test/resources/application-test.properties#L2-L7)</sub>

```diff
@@ -1 +1,7 @@
 # Test-specific configuration settings.
+
+# Specify the test.execution.test-target to either 'rest-api' or 'service-api'
+# to switch between the automation interfaces.
+# From command line you can override this setting with
+#   mvn test "-Dtest.execution.test-target=service-api"
+test.execution.test-target=rest-api
```
