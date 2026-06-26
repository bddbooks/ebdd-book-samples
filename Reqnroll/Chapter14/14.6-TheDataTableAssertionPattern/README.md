# Pattern Differences: 14.6-TheDataTableAssertionPattern

This document shows the differences between the Before and After implementations of this pattern.

## Summary of Changes

- 📝 Modified [WIMP.Specs/StepDefinitions/PromotionStepDefinitions.cs](#wimpspecsstepdefinitionspromotionstepdefinitionscs)
- ➖ Deleted [WIMP.Specs/Support/OfferedItemData.cs](#wimpspecssupportoffereditemdatacs)

## Detailed Changes

### WIMP.Specs/StepDefinitions/PromotionStepDefinitions.cs

[View file](After/WIMP.Specs/StepDefinitions/PromotionStepDefinitions.cs#L2)

```diff
@@ -2,7 +2,6 @@ using Reqnroll;
 
 using WIMP.App.Models;
 using WIMP.App.Services;
-using WIMP.Specs.Support;
 
 namespace WIMP.Specs.StepDefinitions;
 
```

<sub>[Jump to change](After/WIMP.Specs/StepDefinitions/PromotionStepDefinitions.cs#L25)</sub>

```diff
@@ -23,29 +22,6 @@ public class PromotionStepDefinitions(PromotionService promotionService)
         var offeredItems = activePromotion?.OfferedItems
                 ?? throw new InvalidOperationException("No active promotion");
 
-        var expectedOfferedItems = expectedOfferedItemsTable.CreateSet<OfferedItemData>().ToList();
-
-        int itemsToCompare = Math.Min(offeredItems.Count, expectedOfferedItems.Count);
-        for (int i = 0; i < itemsToCompare; i++)
-        {
-            var expectedItem = expectedOfferedItems[i];
-            var actualItem = offeredItems[i];
-
-            Assert.AreEqual(expectedItem.Name, actualItem.Name);
-            if (expectedItem.Price != null)
-            {
-                Assert.AreEqual(expectedItem.Price, actualItem.Price);
-            }
-
-            if (expectedItem.OriginalPrice != null)
-            {
-                Assert.AreEqual(expectedItem.OriginalPrice, actualItem.OriginalPrice);
-            }
-        }
-
-        Assert.HasCount(
-            expectedOfferedItems.Count,
-            offeredItems,
-            "The offered item count is different from the expected");
+        expectedOfferedItemsTable.CompareToSet(offeredItems);
     }
 }
```

### WIMP.Specs/Support/OfferedItemData.cs

[View file](After/WIMP.Specs/Support/OfferedItemData.cs#L0)

```diff
@@ -1,8 +0,0 @@
-namespace WIMP.Specs.Support;
-
-public class OfferedItemData
-{
-    public required string Name { get; set; }
-    public decimal? Price { get; set; }
-    public decimal? OriginalPrice { get; set; }
-}
```
