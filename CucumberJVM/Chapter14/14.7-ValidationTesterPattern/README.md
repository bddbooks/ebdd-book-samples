# Pattern Differences: 14.7-ValidationTesterPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/CustomerCollectionStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionscustomercollectionstepdefinitionsjava)
- 📝 Modified [src/test/resources/com/wimp/app/specs/CustomerCollection.feature](#srctestresourcescomwimpappspecscustomercollectionfeature)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/CustomerCollectionStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/CustomerCollectionStepDefinitions.java#L25)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/CustomerCollectionStepDefinitions.java#L28-L35)</sub>

```diff
@@ -25,6 +25,14 @@ public class CustomerCollectionStepDefinitions {
         order = new OrderObjectMother().withCustomerCollection().build();
     }
 
+    @When("the customer provides valid contact details, but:")
+    public void theCustomerProvidesValidContactDetailsBut(DataTable customizationTable) throws Exception {
+        if (customizationTable.cell(0, 0).equals("field"))
+            customizationTable = customizationTable.transpose();
+        var details = customizationTable.asList(ContactDetails.class).getFirst();
+        provideContactDetails(details);
+    }
+
     @When("the customer provides the contact details as:")
     public void theCustomerProvidesTheContactDetailsAs(DataTable contactDetailsTable) {
         var contactDetails = contactDetailsTable.asList(ContactDetails.class).getFirst();
```

### src/test/resources/com/wimp/app/specs/CustomerCollection.feature

[View file](After/src/test/resources/com/wimp/app/specs/CustomerCollection.feature#L3)

<sub>[Jump to change](After/src/test/resources/com/wimp/app/specs/CustomerCollection.feature#L6-L28)</sub>

```diff
@@ -3,19 +3,26 @@ Feature: Customer collection
 Rule: Customers must supply acceptable contact details when placing an order for customer-collection
   Scenario Outline: Contact details supplied
     Given a customer has chosen to collect their order
-    When the customer provides the contact details as:
-      | Name   | Email   | Phone   |
-      | <name> | <email> | <phone> |
+    When the customer provides valid contact details, but:
+      | field   | value   |
+      | <field> | <value> |
     Then the contact details are <result>
+
   Examples:
-    | description                  | name    | email            | phone         | result       |
-    | Everything provided          | Rebecca | becca@galaxy.uni | 12334456      | accepted     |
-    | No Name                      |         |                  |               | not accepted |
-    | No Name, but email and phone |         | becca@galaxy.uni | 12334456      | not accepted |
-    | No Name, but phone           |         |                  | 12334456      | not accepted |
-    | Only phone                   | Rebecca |                  | 12334456      | accepted     |
-    | Only email                   | Rebecca | becca@galaxy.uni |               | accepted     |
-    | Only name                    | Rebecca |                  |               | not accepted |
-    | Invalid email                | Rebecca | invalid-email    | 12334456      | not accepted |
-    | Invalid phone                | Rebecca | becca@galaxy.uni | invalid-phone | not accepted |
+    | description        | field | value         | result       |
+    | name missing       | Name  |               | not accepted |
+    | wrong email format | Email | invalid-email | not accepted |
+    | email is optional  | Email |               | accepted     |
 
+  Scenario Outline: State is mandatory for US customers
+    Given a customer has chosen to collect their order
+    When the customer provides valid contact details, but:
+      | Country   | State   |
+      | <country> | <state> |
+    Then the contact details are <result>
+
+  Examples:
+    | description            | country | state | result       |
+    | state missing for US   | US      | none  | not accepted |
+    | state provided for US  | US      | CA    | accepted     |
+    | state optional, non-US | France  | none  | accepted     |
```
