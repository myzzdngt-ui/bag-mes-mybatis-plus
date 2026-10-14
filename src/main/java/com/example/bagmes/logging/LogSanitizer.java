package com.example.bagmes.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Serializes request data while removing credentials from known sensitive fields.
 */
public final class LogSanitizer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Set<String> SENSITIVE_KEYS = Set.of("password", "token", "secret", "authorization");
    private static final String MASK = "***";

    private LogSanitizer() {
    }

    public static String sanitize(Object value) {
        try {
            JsonNode root = OBJECT_MAPPER.valueToTree(value);
            maskSensitiveValues(root);
            return OBJECT_MAPPER.writeValueAsString(root);
        } catch (IllegalArgumentException | JsonProcessingException exception) {
            try {
                return OBJECT_MAPPER.writeValueAsString(String.valueOf(value));
            } catch (JsonProcessingException ignored) {
                return "\"[unavailable]\"";
            }
        }
    }

    private static void maskSensitiveValues(JsonNode node) {
        if (node instanceof ObjectNode objectNode) {
            Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (SENSITIVE_KEYS.contains(field.getKey().toLowerCase(Locale.ROOT))) {
                    objectNode.put(field.getKey(), MASK);
                } else {
                    maskSensitiveValues(field.getValue());
                }
            }
        } else if (node instanceof ArrayNode arrayNode) {
            for (JsonNode item : arrayNode) {
                maskSensitiveValues(item);
            }
        }
    }
}
