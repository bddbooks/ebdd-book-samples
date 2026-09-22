/*
 * NOTE: This application ("WIMP - Where Is My Pizza") is provided solely to
 * demonstrate the Behavior-Driven Development scenario automation patterns
 * described in the book "Effective Behavior-Driven Development" by
 * Gaspar Nagy and Seb Rose.
 *
 * It is NOT a complete or production-ready implementation. It deliberately
 * uses shortcuts and simplifications (e.g. authentication, data storage,
 * error handling, security) that are NOT suitable for a real application.
 * Do not use this code as a basis for production software.
 */

package com.wimp.app.services;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Pattern;

@Service
public class MessageService {
    private static final String DEFAULT_LANGUAGE = "en-US";
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%(?<no>\\d+)");

    private static final Map<String, Map<String, String>> MESSAGE_TEMPLATES = Map.of(
        "cannot-deliver-too-many-large-pizzas", Map.of(DEFAULT_LANGUAGE, "We cannot deliver %1 large pizzas in a single order, the maximum is 4"),
        "invalid-password", Map.of(DEFAULT_LANGUAGE, "Invalid password")
    );

    public String getMessage(String language, String messageName, Object... parameters) {
        String template = getMessageTemplate(language, messageName);
        if (parameters == null || parameters.length == 0) {
            return template;
        }

        return PLACEHOLDER_PATTERN.matcher(template).replaceAll(match -> {
            int index = Integer.parseInt(match.group("no")) - 1;
            if (index < 0 || index >= parameters.length) {
                return match.group();
            }
            Object value = parameters[index];
            return value == null ? "" : value.toString();
        });
    }

    private String getMessageTemplate(String language, String messageName) {
        Map<String, String> messages = MESSAGE_TEMPLATES.get(messageName);
        if (messages == null) {
            return "!" + messageName + " (unspecified message for " + language + ")";
        }
        return messages.getOrDefault(language, messages.getOrDefault(DEFAULT_LANGUAGE, "!" + messageName + " (unspecified message for " + language + ")"));
    }
}
