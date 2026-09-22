package com.wimp.app.specs.support;

import io.cucumber.datatable.DataTable;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A helper class to generate data tables to be used for diffing. See Data Table
 * Assertion pattern for details and the related step definition method
 * ('PromotionsStepDefinitions.theFollowingItemsShouldBeOffered').
 */
public class DataTableDiffHelper {

    /**
     * Creates a data table from an object list, with the headers of the
     * provided data table. It attempts getting the fields via usual getter
     * method patterns for the header names. The header names might contain
     * spaces, there are normalized (e.g., "original price" is changed to
     * "originalPrice").
     *
     * @param objectList          The list of objects to generate the data table
     *                            for.
     * @param dataTableForHeaders The data table to take the headers for the
     *                            created table.
     * @param <T>                 The type of the objects in the list.
     * @return A data table with the provided headers and retrieved values from
     * the object list.
     */
    public static <T> @NonNull DataTable createDataTableWithHeader(List<T> objectList, DataTable dataTableForHeaders) {
        List<String> headers = dataTableForHeaders.row(0);
        return createDataTableWithHeader(objectList, headers);
    }

    /**
     * Creates a data table from an object list, with the provided headers. It
     * attempts getting the fields via usual getter method patterns for the
     * header names. The header names might contain spaces, there are normalized
     * (e.g., "original price" is changed to "originalPrice").
     *
     * @param objectList The list of objects to generate the data table for.
     * @param headers    The list of header names to be used for the created
     *                   table.
     * @param <T>        The type of the objects in the list.
     * @return A data table with the provided headers and retrieved values from
     * the object list.
     */
    public static <T> @NonNull DataTable createDataTableWithHeader(List<T> objectList, List<String> headers) {
        List<List<String>> actualTableRows = new ArrayList<>();
        actualTableRows.add(headers);
        for (T actualItem : objectList) {
            List<String> row = headers.stream().map(h -> getObjectValueAsString(actualItem, h)).collect(Collectors.toList());
            actualTableRows.add(row);
        }

        return DataTable.create(actualTableRows);
    }

    private static <T> String getObjectValueAsString(T actualItem, String header) {

        Object propertyValue = getObjectValue(actualItem, header);
        return propertyValue == null ? "" : propertyValue.toString();
    }

    private static Object getObjectValue(Object obj, String header) {
        // allow using spaces in header, e.g. "original price" in the header is changed to "originalPrice"
        var normalizedHeaderName = Pattern.compile("\\s+(.)").matcher(header.trim()).replaceAll(match -> match.group(1).toUpperCase());

        // try invoking getXXX methods ('getOriginalPrice()')
        var getterResult = invokeMethod(obj, "get" + normalizedHeaderName.substring(0, 1).toUpperCase() + normalizedHeaderName.substring(1));
        if (getterResult.success)
            return getterResult.result;

        // try invoking methods used in records: 'originalPrice()'
        var directResult = invokeMethod(obj, normalizedHeaderName.substring(0, 1).toLowerCase() + normalizedHeaderName.substring(1));
        if (directResult.success)
            return directResult.result;

        return null;
    }

    private record InvokeMethodResult(boolean success, Object result) {
    }

    private static InvokeMethodResult invokeMethod(Object obj, String methodName) {
        try {
            Method method = obj.getClass().getMethod(methodName);
            var returnValue = method.invoke(obj);
            return new InvokeMethodResult(true, returnValue);
        } catch (Exception e) {
            // Do nothing, we'll return the default value
            return new InvokeMethodResult(false, null);
        }
    }
}
