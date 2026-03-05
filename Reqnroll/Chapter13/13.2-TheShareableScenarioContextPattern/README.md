# Pattern Differences: 13.2-TheShareableScenarioContextPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.ShareableScenarioContextSample.Specs/Features/Ordering.feature](#wimpshareablescenariocontextsamplespecsfeaturesorderingfeature)
- 📝 Modified [WIMP.ShareableScenarioContextSample.Specs/StepDefinitions/AuthenticationStepDefinitions.cs](#wimpshareablescenariocontextsamplespecsstepdefinitionsauthenticationstepdefinitionscs)
- 📝 Modified [WIMP.ShareableScenarioContextSample.Specs/StepDefinitions/OrderingStepDefinitions.cs](#wimpshareablescenariocontextsamplespecsstepdefinitionsorderingstepdefinitionscs)
- ➕ Added [WIMP.ShareableScenarioContextSample.Specs/Support/AuthenticationContext.cs](#wimpshareablescenariocontextsamplespecssupportauthenticationcontextcs)

## Detailed Changes

### WIMP.ShareableScenarioContextSample.Specs/Features/Ordering.feature

[View file](After/WIMP.ShareableScenarioContextSample.Specs/Features/Ordering.feature#L4)

<sub>[Jump to change](After/WIMP.ShareableScenarioContextSample.Specs/Features/Ordering.feature#L7-L9)</sub>

```diff
@@ -4,6 +4,6 @@ Rule: A customer should receive a notification when their order is cancelled
 
 Scenario: The customer is notified about an order cancellation
   Given the customer Rebecca has logged in
-  And the customer Rebecca has placed the order #12342
-  When the customer Rebecca cancels the placed order
-  Then the customer Rebecca should receive a notification about the cancellation
+  And the logged in customer has placed the order #12342
+  When the logged in customer cancels the placed order
+  Then the logged in customer should receive a notification about the cancellation
```

### WIMP.ShareableScenarioContextSample.Specs/StepDefinitions/AuthenticationStepDefinitions.cs

[View file](After/WIMP.ShareableScenarioContextSample.Specs/StepDefinitions/AuthenticationStepDefinitions.cs#L1)

<sub>[Jump to change](After/WIMP.ShareableScenarioContextSample.Specs/StepDefinitions/AuthenticationStepDefinitions.cs#L4-L15)</sub>

```diff
@@ -1,15 +1,17 @@
 using Reqnroll;
 
 using WIMP.ShareableScenarioContextSample.App.Services;
+using WIMP.ShareableScenarioContextSample.Specs.Support;
 
 namespace WIMP.ShareableScenarioContextSample.Specs.StepDefinitions;
 
 [Binding]
-public class AuthenticationStepDefinitions
+public class AuthenticationStepDefinitions(AuthenticationContext authContext)
 {
     [Given("the customer {word} has logged in")]
     public void GivenTheCustomerHasLoggedIn(string customerName)
     {
         AuthenticationService.Login(customerName);
+        authContext.LoggedInCustomerName = customerName;
     }
 }
```

### WIMP.ShareableScenarioContextSample.Specs/StepDefinitions/OrderingStepDefinitions.cs

[View file](After/WIMP.ShareableScenarioContextSample.Specs/StepDefinitions/OrderingStepDefinitions.cs#L2)

<sub>[Jump to change](After/WIMP.ShareableScenarioContextSample.Specs/StepDefinitions/OrderingStepDefinitions.cs#L5-L30)</sub>

```diff
@@ -2,31 +2,32 @@ using Reqnroll;
 
 using WIMP.ShareableScenarioContextSample.App.Infrastructure;
 using WIMP.ShareableScenarioContextSample.App.Services;
+using WIMP.ShareableScenarioContextSample.Specs.Support;
 
 namespace WIMP.ShareableScenarioContextSample.Specs.StepDefinitions;
 
 [Binding]
-public class OrderingStepDefinitions
+public class OrderingStepDefinitions(AuthenticationContext authContext)
 {
     private int placedOrderNo;
 
-    [Given("the customer {word} has placed the order #{int}")]
-    public void GivenTheCustomerHasPlacedTheOrder(string customerName, int orderNo)
+    [Given("the logged in customer has placed the order #{int}")]
+    public void GivenTheLoggedInCustomerHasPlacedTheOrder(int orderNo)
     {
-        OrderService.PlaceOrder(customerName, orderNo, "Margherita");
+        OrderService.PlaceOrder(authContext.LoggedInCustomerName, orderNo, "Margherita");
         placedOrderNo = orderNo;
     }
 
-    [When("the customer {word} cancels the placed order")]
-    public void WhenTheCustomerCancelsThePlacedOrder(string customerName)
+    [When("the logged in customer cancels the placed order")]
+    public void WhenTheLoggedInCustomerCancelsThePlacedOrder()
     {
-        OrderService.CancelOrder(customerName, placedOrderNo);
+        OrderService.CancelOrder(authContext.LoggedInCustomerName, placedOrderNo);
     }
 
-    [Then("the customer {word} should receive a notification about the cancellation")]
-    public void ThenTheCustomerShouldReceiveANotification(string customerName)
+    [Then("the logged in customer should receive a notification about the cancellation")]
+    public void ThenTheLoggedInCustomerShouldReceiveANotification()
     {
-        Assert.IsTrue(NotificationService.WasNotificationSent(customerName));
+        Assert.IsTrue(NotificationService.WasNotificationSent(authContext.LoggedInCustomerName));
     }
 
     #region Reset database for every scenario execution
```

### WIMP.ShareableScenarioContextSample.Specs/Support/AuthenticationContext.cs

[View file](After/WIMP.ShareableScenarioContextSample.Specs/Support/AuthenticationContext.cs#L1)

<sub>[Jump to change](After/WIMP.ShareableScenarioContextSample.Specs/Support/AuthenticationContext.cs#L1-L11)</sub>

```diff
@@ -0,0 +1,11 @@
+namespace WIMP.ShareableScenarioContextSample.Specs.Support;
+
+/// <summary>
+/// Context class for sharing authentication-related data between step definition classes.
+/// This pattern allows step definitions in different classes to access the same data
+/// through dependency injection without relying on static fields.
+/// </summary>
+public class AuthenticationContext
+{
+    public string? LoggedInCustomerName { get; set; }
+}
```
