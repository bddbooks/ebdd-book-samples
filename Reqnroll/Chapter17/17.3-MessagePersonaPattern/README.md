# Pattern Differences: 17.3-MessagePersonaPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/Features/Ordering.feature](#wimpspecsfeaturesorderingfeature)
- 📝 Modified [WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs](#wimpspecsstepdefinitionsorderingstepdefinitionscs)
- 📝 Modified [WIMP.Specs/Support/CustomParameterTypes.cs](#wimpspecssupportcustomparametertypescs)

## Detailed Changes

### WIMP.Specs/Features/Ordering.feature

[View file](After/WIMP.Specs/Features/Ordering.feature#L3)

<sub>[Jump to change](After/WIMP.Specs/Features/Ordering.feature#L6)</sub>

```diff
@@ -3,4 +3,4 @@ Feature: Ordering
 Rule: More than 4 large pizzas cannot be delivered as a single order
   Scenario: Five large pizzas are ordered
     When the customer places an order for 5 pizzas of size large
-    Then the order should be rejected with message "We cannot deliver 5 large pizzas in a single order, the maximum is 4"
+    Then the order should be rejected with message [cannot-deliver-too-many-large-pizzas,5]
```

### WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L24)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/OrderingStepDefinitions.cs#L27)</sub>

```diff
@@ -24,7 +24,7 @@ public class OrderingStepDefinitions(OrderingApiDriver orderingApiDriver, Authen
             .AttemptExecute();
     }
 
-    [Then("the order should be rejected with message {string}")]
+    [Then("the order should be rejected with message {user-message}")]
     public void ThenTheOrderShouldBeRejectedWith(string expectedMessage)
     {
         placeOrderResult.AssertFailedWithErrorMessageContains(expectedMessage);
```

### WIMP.Specs/Support/CustomParameterTypes.cs

[View file](After/WIMP.Specs/Support/CustomParameterTypes.cs#L1)

<sub>[Jump to change](After/WIMP.Specs/Support/CustomParameterTypes.cs#L3-L19)</sub>

```diff
@@ -1,8 +1,20 @@
 using Reqnroll;
 
+using WIMP.App.Services;
+using WIMP.Specs.Drivers;
+
 namespace WIMP.Specs.Support;
 
 [Binding]
-public class CustomParameterTypes
+public class CustomParameterTypes(CustomerDriver customerDriver, MessageService messageService)
 {
+    [StepArgumentTransformation(@"\[([\w\-]+(?:,.+)?)\]", Name = "user-message")]
+    public string ConvertUserMessage(string messageNameSpecification)
+    {
+        string language = customerDriver.GetInterfaceLanguage();
+        string[] specParts = messageNameSpecification.Split(',');
+        string messageName = specParts[0];
+        string[] parameters = specParts.Skip(1).ToArray();
+        return messageService.GetMessage(language, messageName, parameters);
+    }
 }
```
