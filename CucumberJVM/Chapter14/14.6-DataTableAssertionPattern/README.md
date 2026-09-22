# Pattern Differences: 14.6-DataTableAssertionPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [src/test/java/com/wimp/app/specs/stepdefinitions/PromotionStepDefinitions.java](#srctestjavacomwimpappspecsstepdefinitionspromotionstepdefinitionsjava)
- 📝 Modified [src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java](#srctestjavacomwimpappspecssupportcustomparametertypesjava)
- ➕ Added [src/test/java/com/wimp/app/specs/support/DataTableDiffHelper.java](#srctestjavacomwimpappspecssupportdatatablediffhelperjava)
- ➖ Deleted [src/test/java/com/wimp/app/specs/support/OfferedItemData.java](#srctestjavacomwimpappspecssupportoffereditemdatajava)

## Detailed Changes

### src/test/java/com/wimp/app/specs/stepdefinitions/PromotionStepDefinitions.java

[View file](After/src/test/java/com/wimp/app/specs/stepdefinitions/PromotionStepDefinitions.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/PromotionStepDefinitions.java#L3-L7)</sub>

```diff
@@ -1,14 +1,13 @@
 package com.wimp.app.specs.stepdefinitions;
 
+import com.wimp.app.models.OfferedItem;
 import com.wimp.app.models.Promotion;
 import com.wimp.app.services.PromotionService;
-import com.wimp.app.specs.support.OfferedItemData;
+import com.wimp.app.specs.support.DataTableDiffHelper;
+import io.cucumber.datatable.DataTable;
 import io.cucumber.java.en.*;
 
 import java.util.List;
-import java.util.Optional;
-
-import static org.junit.jupiter.api.Assertions.*;
 
 public class PromotionStepDefinitions {
 
```

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/stepdefinitions/PromotionStepDefinitions.java#L27-L31)</sub>

```diff
@@ -25,28 +24,10 @@ public class PromotionStepDefinitions {
     }
 
     @Then("the following items should be offered")
-    public void theFollowingItemsShouldBeOffered(List<OfferedItemData> expectedOfferedItems) {
-        var offeredItems = Optional.of(activePromotion.offeredItems()).orElseThrow(() -> new RuntimeException("No active promotion"));
-
-        int itemsToCompare = Math.min(offeredItems.size(), expectedOfferedItems.size());
-        for (int i = 0; i < itemsToCompare; i++)
-        {
-            var expectedItem = expectedOfferedItems.get(i);
-            var actualItem = offeredItems.get(i);
-
-            assertEquals(expectedItem.name(), actualItem.name());
-            if (expectedItem.price() != null)
-            {
-                assertTrue(expectedItem.price().compareTo(actualItem.price()) == 0);
-            }
-
-            if (expectedItem.originalPrice() != null)
-            {
-                assertTrue(expectedItem.originalPrice().compareTo(actualItem.originalPrice()) == 0);
-            }
-        }
-
-        assertEquals(expectedOfferedItems.size(), offeredItems.size(),
-            "The offered item count is different from the expected");
+    public void theFollowingItemsShouldBeOffered(DataTable expectedItemsDataTable) {
+        List<OfferedItem> promotionalItems = activePromotion.offeredItems().stream()
+            .filter(item -> !item.originalPrice().equals(item.price())).toList();
+        var actualItemsTable = DataTableDiffHelper.createDataTableWithHeader(promotionalItems, expectedItemsDataTable);
+        expectedItemsDataTable.unorderedDiff(actualItemsTable);
     }
 }
```

### src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java

[View file](After/src/test/java/com/wimp/app/specs/support/CustomParameterTypes.java#L18)

```diff
@@ -18,14 +18,6 @@ public class CustomParameterTypes {
             row.containsKey("vegetarian") ? Boolean.parseBoolean(row.get("vegetarian")) : DomainDefaults.PIZZA_VEGETARIAN);
     }
 
-    @DataTableType
-    public OfferedItemData orderedItemDataRow(Map<String, String> row) {
-        return new OfferedItemData(
-            row.get("name"),
-            row.containsKey("price") ? new BigDecimal(row.get("price")) : null,
-            row.containsKey("original price") ? new BigDecimal(row.get("original price")) : null);
-    }
-
     @ParameterType("\\$?(\\d+(?:\\.\\d{1,2})?)")
     public BigDecimal price(String value){
         return new BigDecimal(value);
```

### src/test/java/com/wimp/app/specs/support/DataTableDiffHelper.java

[View file](After/src/test/java/com/wimp/app/specs/support/DataTableDiffHelper.java#L1)

<sub>[Jump to change](After/src/test/java/com/wimp/app/specs/support/DataTableDiffHelper.java#L1-L99)</sub>

```diff
@@ -0,0 +1,99 @@
+package com.wimp.app.specs.support;
+
+import io.cucumber.datatable.DataTable;
+import org.jspecify.annotations.NonNull;
+
+import java.lang.reflect.Method;
+import java.util.ArrayList;
+import java.util.List;
+import java.util.regex.Pattern;
+import java.util.stream.Collectors;
+
+/**
+ * A helper class to generate data tables to be used for diffing. See Data Table
+ * Assertion pattern for details and the related step definition method
+ * ('PromotionsStepDefinitions.theFollowingItemsShouldBeOffered').
+ */
+public class DataTableDiffHelper {
+
+    /**
+     * Creates a data table from an object list, with the headers of the
+     * provided data table. It attempts getting the fields via usual getter
+     * method patterns for the header names. The header names might contain
+     * spaces, there are normalized (e.g., "original price" is changed to
+     * "originalPrice").
+     *
+     * @param objectList          The list of objects to generate the data table
+     *                            for.
+     * @param dataTableForHeaders The data table to take the headers for the
+     *                            created table.
+     * @param <T>                 The type of the objects in the list.
+     * @return A data table with the provided headers and retrieved values from
+     * the object list.
+     */
+    public static <T> @NonNull DataTable createDataTableWithHeader(List<T> objectList, DataTable dataTableForHeaders) {
+        List<String> headers = dataTableForHeaders.row(0);
+        return createDataTableWithHeader(objectList, headers);
+    }
+
+    /**
+     * Creates a data table from an object list, with the provided headers. It
+     * attempts getting the fields via usual getter method patterns for the
+     * header names. The header names might contain spaces, there are normalized
+     * (e.g., "original price" is changed to "originalPrice").
+     *
+     * @param objectList The list of objects to generate the data table for.
+     * @param headers    The list of header names to be used for the created
+     *                   table.
+     * @param <T>        The type of the objects in the list.
+     * @return A data table with the provided headers and retrieved values from
+     * the object list.
+     */
+    public static <T> @NonNull DataTable createDataTableWithHeader(List<T> objectList, List<String> headers) {
+        List<List<String>> actualTableRows = new ArrayList<>();
+        actualTableRows.add(headers);
+        for (T actualItem : objectList) {
+            List<String> row = headers.stream().map(h -> getObjectValueAsString(actualItem, h)).collect(Collectors.toList());
+            actualTableRows.add(row);
+        }
+
+        return DataTable.create(actualTableRows);
+    }
+
+    private static <T> String getObjectValueAsString(T actualItem, String header) {
+
+        Object propertyValue = getObjectValue(actualItem, header);
+        return propertyValue == null ? "" : propertyValue.toString();
+    }
+
+    private static Object getObjectValue(Object obj, String header) {
+        // allow using spaces in header, e.g. "original price" in the header is changed to "originalPrice"
+        var normalizedHeaderName = Pattern.compile("\\s+(.)").matcher(header.trim()).replaceAll(match -> match.group(1).toUpperCase());
+
+        // try invoking getXXX methods ('getOriginalPrice()')
+        var getterResult = invokeMethod(obj, "get" + normalizedHeaderName.substring(0, 1).toUpperCase() + normalizedHeaderName.substring(1));
+        if (getterResult.success)
+            return getterResult.result;
+
+        // try invoking methods used in records: 'originalPrice()'
+        var directResult = invokeMethod(obj, normalizedHeaderName.substring(0, 1).toLowerCase() + normalizedHeaderName.substring(1));
+        if (directResult.success)
+            return directResult.result;
+
+        return null;
+    }
+
+    private record InvokeMethodResult(boolean success, Object result) {
+    }
+
+    private static InvokeMethodResult invokeMethod(Object obj, String methodName) {
+        try {
+            Method method = obj.getClass().getMethod(methodName);
+            var returnValue = method.invoke(obj);
+            return new InvokeMethodResult(true, returnValue);
+        } catch (Exception e) {
+            // Do nothing, we'll return the default value
+            return new InvokeMethodResult(false, null);
+        }
+    }
+}
```

### src/test/java/com/wimp/app/specs/support/OfferedItemData.java

[View file](After/src/test/java/com/wimp/app/specs/support/OfferedItemData.java#L0)

```diff
@@ -1,6 +0,0 @@
-package com.wimp.app.specs.support;
-
-import java.math.BigDecimal;
-
-public record OfferedItemData(String name, BigDecimal price, BigDecimal originalPrice) {
-}
```
