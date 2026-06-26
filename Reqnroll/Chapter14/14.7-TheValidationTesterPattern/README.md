# Pattern Differences: 14.7-TheValidationTesterPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/Features/CustomerCollection.feature](#wimpspecsfeaturescustomercollectionfeature)
- 📝 Modified [WIMP.Specs/StepDefinitions/CustomerCollectionStepDefinitions.cs](#wimpspecsstepdefinitionscustomercollectionstepdefinitionscs)
- 📝 Modified [WIMP.Specs/Support/DomainDefaults.cs](#wimpspecssupportdomaindefaultscs)

## Detailed Changes

### WIMP.Specs/Features/CustomerCollection.feature

[View file](After/WIMP.Specs/Features/CustomerCollection.feature#L3)

<sub>[Jump to change](After/WIMP.Specs/Features/CustomerCollection.feature#L6-L28)</sub>

```diff
@@ -3,19 +3,26 @@ Feature: Customer collection
 Rule: Customers must supply acceptable contact details when placing an order for customer-collection
   Scenario Outline: Contact details supplied
     Given a customer has chosen to collect their order
-    When the customer provides the contact details as:
-      | name   | email   | phone   |
-      | <name> | <email> | <phone> |
+    When the customer provides valid contact details, but:
+      | field   | value   |
+      | <field> | <value> |
     Then the contact details are <result>
+
+  Examples:
+    | description        | field | value         | result       |
+    | name missing       | Name  |               | not accepted |
+    | wrong email format | Email | invalid-email | not accepted |
+    | email is optional  | Email |               | accepted     |
+
+  Scenario Outline: State is mandatory for US customers
+    Given a customer has chosen to collect their order
+    When the customer provides valid contact details, but:
+      | Country   | State   |
+      | <country> | <state> |
+    Then the contact details are <result>
+
   Examples:
-    | description                  | name    | email            | phone         | result       |
-    | Everything provided          | Rebecca | becca@galaxy.uni |      12334456 | accepted     |
-    | No Name                      |         |                  |               | not accepted |
-    | No Name, but email and phone |         | becca@galaxy.uni |      12334456 | not accepted |
-    | No Name, but phone           |         |                  |      12334456 | not accepted |
-    | Only phone                   | Rebecca |                  |      12334456 | accepted     |
-    | Only email                   | Rebecca | becca@galaxy.uni |               | accepted     |
-    | Only name                    | Rebecca |                  |               | not accepted |
-    | Invalid email                | Rebecca | invalid-email    |      12334456 | not accepted |
-    | Invalid phone                | Rebecca | becca@galaxy.uni | invalid-phone | not accepted |
-    
+    | description            | country | state | result       |
+    | state missing for US   | US      | -     | not accepted |
+    | state provided for US  | US      | CA    | accepted     |
+    | state optional, non-US | France  | -     | accepted     |
```

### WIMP.Specs/StepDefinitions/CustomerCollectionStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/CustomerCollectionStepDefinitions.cs#L18)

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/CustomerCollectionStepDefinitions.cs#L21-L29)</sub>

```diff
@@ -18,6 +18,15 @@ public class CustomerCollectionStepDefinitions(OrderService orderService)
         order = new OrderObjectMother().WithCustomerCollection().Build();
     }
 
+    [When("the customer provides valid contact details, but:")]
+    public void WhenTheCustomerProvidesValidContactDetailsBut(DataTable customizationTable)
+    {
+        var contactDetails = DomainDefaults.ContactDetailsDefaultInstance();
+        customizationTable.FillInstance(contactDetails);
+
+        ProvideCustomerDetails(contactDetails);
+    }
+
     [When("the customer provides the contact details as:")]
     public void WhenTheCustomerProvidesTheContactDetailsAs(DataTable contactDetailsTable)
     {
```

### WIMP.Specs/Support/DomainDefaults.cs

[View file](After/WIMP.Specs/Support/DomainDefaults.cs#L18)

<sub>[Jump to change](After/WIMP.Specs/Support/DomainDefaults.cs#L21-L37)</sub>

```diff
@@ -18,4 +18,21 @@ public static class DomainDefaults
     }
 
     public const OrderCollection OrderCollection = App.Models.OrderCollection.Delivery;
+
+    public const string CustomerName = "Rebecca";
+    public const string CustomerEmail = "becca@galaxy.uni";
+    public const string CustomerPhone = "12334456";
+    public const string Country = "France";
+
+    public static ContactDetails ContactDetailsDefaultInstance()
+    {
+        return new ContactDetails
+        {
+            Name = CustomerName,
+            Email = CustomerEmail,
+            Phone = CustomerPhone,
+            Country = Country,
+            State = null
+        };
+    }
 }
```
