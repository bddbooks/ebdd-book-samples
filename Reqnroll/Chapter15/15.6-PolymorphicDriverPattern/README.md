# Pattern Differences: 15.6-PolymorphicDriverPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/Drivers/AuthenticationApiDriver.cs](#wimpspecsdriversauthenticationapidrivercs)
- ➕ Added [WIMP.Specs/Drivers/AuthenticationServiceDriver.cs](#wimpspecsdriversauthenticationservicedrivercs)
- ➕ Added [WIMP.Specs/Drivers/IAuthenticationDriver.cs](#wimpspecsdriversiauthenticationdrivercs)
- 📝 Modified [WIMP.Specs/StepDefinitions/AuthenticationStepDefinitions.cs](#wimpspecsstepdefinitionsauthenticationstepdefinitionscs)
- ➕ Added [WIMP.Specs/Support/DiConfiguration.cs](#wimpspecssupportdiconfigurationcs)

## Detailed Changes

### WIMP.Specs/Drivers/AuthenticationApiDriver.cs

[View file](After/WIMP.Specs/Drivers/AuthenticationApiDriver.cs#L5)

<sub>[Jump to change](After/WIMP.Specs/Drivers/AuthenticationApiDriver.cs#L8)</sub>

```diff
@@ -5,7 +5,7 @@ using WIMP.Specs.Support;
 
 namespace WIMP.Specs.Drivers;
 
-public class AuthenticationApiDriver(RestApiContext restApiContext)
+public class AuthenticationApiDriver(RestApiContext restApiContext) : IAuthenticationDriver
 {
     public TestAction<LoginResponse> Login(string customerName, string password) =>
         new LambdaAction<LoginResponse>("Login", async () =>
```

### WIMP.Specs/Drivers/AuthenticationServiceDriver.cs

[View file](After/WIMP.Specs/Drivers/AuthenticationServiceDriver.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Drivers/AuthenticationServiceDriver.cs#L1-L26)</sub>

```diff
@@ -0,0 +1,26 @@
+using WIMP.App.RestApi;
+using WIMP.App.Services;
+using WIMP.Specs.Support;
+
+namespace WIMP.Specs.Drivers;
+
+public class AuthenticationServiceDriver(AuthenticationService authService) : IAuthenticationDriver
+{
+    public TestAction<LoginResponse> Login(string customerName, string password) =>
+        new LambdaAction<LoginResponse>("Login", () =>
+        {
+            var result = authService.Login(customerName, password);
+            return result.Successful ? new LoginResponse(result.Value, customerName) :
+                    throw new TestActionFailedException(result.ErrorMessage);
+        });
+
+    public TestAction<VoidReturn> Register(string customerName, string email) =>
+        new LambdaAction("Register", () =>
+        {
+            var result = authService.Register(customerName, email);
+            if (!result.Successful)
+            {
+                throw new TestActionFailedException(result.ErrorMessage);
+            }
+        });
+}
```

### WIMP.Specs/Drivers/IAuthenticationDriver.cs

[View file](After/WIMP.Specs/Drivers/IAuthenticationDriver.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Drivers/IAuthenticationDriver.cs#L1-L11)</sub>

```diff
@@ -0,0 +1,11 @@
+using WIMP.App.RestApi;
+using WIMP.Specs.Support;
+
+namespace WIMP.Specs.Drivers;
+
+public interface IAuthenticationDriver
+{
+    public TestAction<LoginResponse> Login(string customerName, string password);
+
+    public TestAction<VoidReturn> Register(string customerName, string email);
+}
```

### WIMP.Specs/StepDefinitions/AuthenticationStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/AuthenticationStepDefinitions.cs#L7)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/AuthenticationStepDefinitions.cs#L10-L17)</sub>

```diff
@@ -7,14 +7,14 @@ using WIMP.Specs.Support;
 namespace WIMP.Specs.StepDefinitions;
 
 [Binding]
-public class AuthenticationStepDefinitions(AuthenticationApiDriver authenticationApiDriver)
+public class AuthenticationStepDefinitions(IAuthenticationDriver authenticationDriver)
 {
     private TestActionResult<LoginResponse> loginResult = TestActionResult<LoginResponse>.NotExecuted;
 
     [When("the customer attempts to log in with a wrong password")]
     public async Task WhenTheCustomerAttemptsToLogInWithAWrongPassword()
     {
-        loginResult = await authenticationApiDriver.Login(DomainDefaults.CustomerName, DomainDefaults.WrongPassword).AttemptExecute();
+        loginResult = await authenticationDriver.Login(DomainDefaults.CustomerName, DomainDefaults.WrongPassword).AttemptExecute();
     }
 
     [Then("the login should fail with {string}")]
```

### WIMP.Specs/Support/DiConfiguration.cs

[View file](After/WIMP.Specs/Support/DiConfiguration.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/DiConfiguration.cs#L1-L32)</sub>

```diff
@@ -0,0 +1,32 @@
+using Microsoft.Extensions.Logging;
+using Microsoft.Extensions.Logging.Abstractions;
+
+using Reqnroll;
+using Reqnroll.BoDi;
+
+using WIMP.App.Data;
+using WIMP.App.Services;
+using WIMP.Specs.Drivers;
+
+namespace WIMP.Specs.Support;
+
+[Binding]
+public class DiConfiguration
+{
+    [BeforeScenario(Order = 0)]
+    public void SetupDependencies(IObjectContainer scenarioContainer)
+    {
+        if (Environment.GetEnvironmentVariable("WIMP_TEST_TARGET") == "rest")
+        {
+            scenarioContainer.RegisterTypeAs<AuthenticationApiDriver, IAuthenticationDriver>();
+        }
+        else
+        {
+            scenarioContainer.RegisterTypeAs<AuthenticationServiceDriver, IAuthenticationDriver>();
+
+            // Additional DI registrations to allow injecting AuthenticationService to AuthenticationServiceDriver
+            scenarioContainer.RegisterTypeAs<StubDataRepository, IDataRepository>();
+            scenarioContainer.RegisterInstanceAs<ILogger<AuthenticationService>>(NullLogger<AuthenticationService>.Instance);
+        }
+    }
+}
```
