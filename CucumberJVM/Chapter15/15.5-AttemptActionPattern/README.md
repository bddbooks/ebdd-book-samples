# Pattern Differences: 15.5-AttemptActionPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/AuthenticationStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionsauthenticationstepdefinitionsjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/TestAction.java](#srctestjavacomwimpappspecssupporttestactionjava)
- ➕ Added [src/test/java/com/wimp/app/specs/support/TestActionResult.java](#srctestjavacomwimpappspecssupporttestactionresultjava)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/AuthenticationStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/AuthenticationStepDefinitions.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/AuthenticationStepDefinitions.java#L3-L11)</sub>

```diff
@@ -1,16 +1,14 @@
 package com.wimp.app.specs.stepdefinitions;
 
+import com.wimp.app.restapi.LoginResponse;
 import com.wimp.app.specs.drivers.AuthenticationApiDriver;
 import com.wimp.app.specs.support.DomainDefaults;
-import com.wimp.app.specs.support.TestActionFailedException;
+import com.wimp.app.specs.support.TestActionResult;
 import io.cucumber.java.en.*;
 
-import static org.junit.jupiter.api.Assertions.assertNotNull;
-import static org.junit.jupiter.api.Assertions.assertTrue;
-
 public class AuthenticationStepDefinitions {
     private final AuthenticationApiDriver authenticationApiDriver;
-    private TestActionFailedException loginError;
+    private TestActionResult<LoginResponse> loginResult;
 
     public AuthenticationStepDefinitions(AuthenticationApiDriver authenticationApiDriver) {
         this.authenticationApiDriver = authenticationApiDriver;
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/AuthenticationStepDefinitions.java#L19-L24)</sub>

```diff
@@ -18,17 +16,11 @@ public class AuthenticationStepDefinitions {
 
     @When("the customer attempts to log in with a wrong password")
     public void theCustomerAttemptsToLogInWithAWrongPassword() throws Exception {
-        try {
-            authenticationApiDriver.login(DomainDefaults.CUSTOMER_NAME, DomainDefaults.WRONG_PASSWORD).execute();
-            loginError = null;
-        } catch (TestActionFailedException ex) {
-            loginError = ex;
-        }
+        loginResult = authenticationApiDriver.login(DomainDefaults.CUSTOMER_NAME, DomainDefaults.WRONG_PASSWORD).attemptExecute();
     }
 
     @Then("the login should fail with {string}")
     public void theLoginShouldFailWith(String expectedMessage) {
-        assertNotNull(loginError);
-        assertTrue(loginError.getMessage().contains(expectedMessage), "Login should fail with the right error message (`%s`), but failed with `%s`".formatted(expectedMessage, loginError.getMessage()));
+        loginResult.assertFailedWithErrorMessageContains(expectedMessage);
     }
 }
```

### src/test/java/com/wimp/app/specs/support/TestAction.java

[View file](After/src/test/java/com/wimp/app/specs/support/TestAction.java#L3)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/TestAction.java#L6-L8)</sub>

```diff
@@ -3,6 +3,9 @@ package com.wimp.app.specs.support;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
+import static com.wimp.app.specs.support.TestActionResult.createFailed;
+import static com.wimp.app.specs.support.TestActionResult.createSucceeded;
+
 public abstract class TestAction<TResult> {
 
     protected static final Logger log = LoggerFactory.getLogger(TestAction.class);
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/TestAction.java#L49-L57)</sub>

```diff
@@ -43,4 +46,13 @@ public abstract class TestAction<TResult> {
             throw new TestActionFailedException("%s failed: %s".formatted(getTestActionName(), ex.getMessage()));
         }
     }
+
+    public final TestActionResult<TResult> attemptExecute() throws Exception {
+        try {
+            var result = execute();
+            return createSucceeded(result);
+        } catch (TestActionFailedException error) {
+            return createFailed(error);
+        }
+    }
 }
```

### src/test/java/com/wimp/app/specs/support/TestActionResult.java

[View file](After/src/test/java/com/wimp/app/specs/support/TestActionResult.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/TestActionResult.java#L1-L70)</sub>

```diff
@@ -0,0 +1,70 @@
+package com.wimp.app.specs.support;
+
+import static org.junit.jupiter.api.Assertions.*;
+
+public class TestActionResult<TResult> {
+
+    public static <TResult> TestActionResult<TResult> notExecuted() {
+        return new TestActionResult<>(false, false, null, null);
+    }
+
+    public static <TResult> TestActionResult<TResult> createSucceeded(TResult result) {
+        return new TestActionResult<>(true, true, null, result);
+    }
+
+    public static <TResult> TestActionResult<TResult> createSucceeded() {
+        return createSucceeded(null);
+    }
+
+    public static <TResult> TestActionResult<TResult> createFailed(TestActionFailedException error) {
+        return new TestActionResult<>(true, false, error, null);
+    }
+
+    private final boolean executed;
+    private final boolean success;
+    private final TestActionFailedException error;
+    private final TResult result;
+
+    public boolean wasExecuted() {
+        return executed;
+    }
+
+    public boolean isSuccess() {
+        return success;
+    }
+
+    public TestActionFailedException getError() {
+        return error;
+    }
+
+    public TResult getResult() {
+        return result;
+    }
+
+    private TestActionResult(boolean executed, boolean success, TestActionFailedException error, TResult result) {
+        this.executed = executed;
+        this.success = success;
+        this.error = error;
+        this.result = result;
+    }
+
+    public void assertExecuted() {
+        assertTrue(wasExecuted(), "The action was not executed");
+    }
+
+    public void assertFailed() {
+        assertExecuted();
+        assertFalse(isSuccess(), "The result (%s) expected to be failure".formatted(this));
+        assertNotNull(getError(), "The result (%s) expected to contain an error".formatted(this));
+    }
+
+    public void assertFailedWithErrorMessageContains(String expectedErrorMessage) {
+        assertFailed();
+        assertTrue(getError().getMessage().contains(expectedErrorMessage), "The error `%s` is expected to contain message `%s`".formatted(getError().getMessage(), expectedErrorMessage));
+    }
+
+    public void assertSucceeded() {
+        assertExecuted();
+        assertTrue(isSuccess(), "The result (%s) expected to succeed".formatted(this));
+    }
+}
```
