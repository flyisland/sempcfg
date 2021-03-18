package com.solace.tools.sempcfg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {
    public static ObjectMapper objectMapper = new ObjectMapper();

    // TODO: move to SempSpec class
    public static String getCollectionNameFromUri(String uri){
        String[] items = uri.split("/");
        return items[items.length-1].split("\\?")[0];
    }

    public static  <T> Optional<T> jsonSafeGetValue(JsonNode jsonNode, Class<T> valueType, String ... names) {
        Optional<JsonNode> node = jsonSafeGetNode(jsonNode, names);

        return node.map(n -> {
            try {
                return objectMapper.treeToValue(n, valueType);
            } catch (JsonProcessingException e) {
                return null;
            }
        });
    }

    private static Optional<JsonNode> jsonSafeGetNode(JsonNode jsonNode, String[] names) {
        var node = Optional.of(jsonNode);
        for (String name : names) {
            node = node.map(n -> n.get(name));
        }
        return node;
    }

    public static Optional<String> getFirstMatch(String input, Pattern re) {
        var m = re.matcher(input);
        if (m.find()) {
            return Optional.of(m.group(1));
        }else {
            return Optional.empty();
        }
    }

    public static void log(String text) {
        System.err.println(text);
    }

    public static void err(String format, Object... args) {
        System.err.printf(format, args);
    }

    public static void errPrintlnAndExit(String format, Object... args) {
        errPrintlnAndExit(null, format, args);
    }

    public static void errPrintlnAndExit(Exception e, String format, Object... args) {
        err(format, args);
        err("%n");
        if (Objects.nonNull(e)) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    public static Set<Map.Entry<String, Object>> symmetricDiff(Set<Map.Entry<String, Object>> s1, Set<Map.Entry<String, Object>> s2) {
        var symmetricDiff = new HashSet<>(s1);
        symmetricDiff.addAll(s2);
        var tmp = new HashSet<>(s1);
        tmp.retainAll(s2);
        symmetricDiff.removeAll(tmp);

        return symmetricDiff;
    }
}