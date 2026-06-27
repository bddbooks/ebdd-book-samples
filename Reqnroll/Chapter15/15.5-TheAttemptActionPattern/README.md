# Pattern Differences: 15.5-TheAttemptActionPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/StepDefinitions/AuthenticationStepDefinitions.cs](#wimpspecsstepdefinitionsauthenticationstepdefinitionscs)
- 📝 Modified [WIMP.Specs/Support/TestAction.cs](#wimpspecssupporttestactioncs)
- ➕ Added [WIMP.Specs/Support/TestActionResult.cs](#wimpspecssupporttestactionresultcs)

## Detailed Changes

### WIMP.Specs/StepDefinitions/AuthenticationStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/AuthenticationStepDefinitions.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/AuthenticationStepDefinitions.cs#L3)</sub>

```diff
@@ -1,5 +1,6 @@
 using Reqnroll;
 
+using WIMP.App.RestApi;
 using WIMP.Specs.Drivers;
 using WIMP.Specs.Support;
 
```

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/AuthenticationStepDefinitions.cs#L12-L23)</sub>

```diff
@@ -8,27 +9,17 @@ namespace WIMP.Specs.StepDefinitions;
 [Binding]
 public class AuthenticationStepDefinitions(AuthenticationApiDriver authApiDriver)
 {
-    private WimpActionFailedException? loginError;
+    private TestActionResult<LoginResponse> loginResult = TestActionResult<LoginResponse>.NotExecuted;
 
     [When("the customer attempts to log in with a wrong password")]
     public async Task WhenTheCustomerAttemptsToLogInWithAWrongPassword()
     {
-        try
-        {
-            await authApiDriver.Login(DomainDefaults.CustomerName, DomainDefaults.WrongPassword).Execute();
-            loginError = null;
-        }
-        catch (WimpActionFailedException ex)
-        {
-            loginError = ex;
-        }
+        loginResult = await authApiDriver.Login(DomainDefaults.CustomerName, DomainDefaults.WrongPassword).AttemptExecute();
     }
 
     [Then("the login should fail with {string}")]
     public void ThenTheLoginShouldFailWith(string expectedMessage)
     {
-        Assert.IsNotNull(loginError, "Login should fail");
-        StringAssert.Contains(loginError.Message, expectedMessage,
-            "Login should fail with the right error message");
+        loginResult.AssertFailedWithErrorMessageContains(expectedMessage);
     }
 }
```

### WIMP.Specs/Support/TestAction.cs

[View file](After/WIMP.Specs/Support/TestAction.cs#L24)

<sub>[Jump to change](After/WIMP.Specs/Support/TestAction.cs#L27-L39)</sub>

```diff
@@ -24,4 +24,17 @@ public abstract class TestAction<TResult>(string actionName)
             throw;
         }
     }
+
+    public async Task<TestActionResult<TResult>> AttemptExecute()
+    {
+        try
+        {
+            var result = await Execute();
+            return TestActionResult<TResult>.CreateSucceeded(result);
+        }
+        catch (WimpActionFailedException error)
+        {
+            return TestActionResult<TResult>.CreateFailed(error);
+        }
+    }
 }
```

### WIMP.Specs/Support/TestActionResult.cs

[View file](After/WIMP.Specs/Support/TestActionResult.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/TestActionResult.cs#L1-L52)</sub>

```diff
@@ -0,0 +1,52 @@
+namespace WIMP.Specs.Support;
+
+public record TestActionResult<TResult>
+{
+    public static readonly TestActionResult<TResult> NotExecuted =
+        new(false, null, default!);
+
+    public static TestActionResult<TResult> CreateSucceeded(
+        TResult result = default!) => new(true, null, result);
+
+    public static TestActionResult<TResult> CreateFailed(
+        WimpActionFailedException error) => new(false, error, default!);
+
+    public bool WasExecuted => !Equals(NotExecuted);
+
+    public bool Success { get; }
+
+    public WimpActionFailedException? Error { get; }
+
+    public TResult Result { get; }
+
+    private TestActionResult(bool success, WimpActionFailedException? error, TResult result)
+    {
+        Success = success;
+        Error = error;
+        Result = result;
+    }
+
+    public void AssertExecuted()
+    {
+        Assert.IsTrue(WasExecuted, "The action was not executed");
+    }
+
+    public void AssertFailed()
+    {
+        AssertExecuted();
+        Assert.IsFalse(Success, $"The result ({this}) expected to be failure");
+        Assert.IsNotNull(Error, $"The result ({this}) expected to contain an error");
+    }
+
+    public void AssertFailedWithErrorMessageContains(string expectedErrorMessage)
+    {
+        AssertFailed();
+        StringAssert.Contains(Error!.Message, expectedErrorMessage);
+    }
+
+    public void AssertSucceeded()
+    {
+        AssertExecuted();
+        Assert.IsTrue(Success, $"The result ({this}) expected to succeed");
+    }
+}
```
