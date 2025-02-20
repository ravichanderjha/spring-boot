package com.example.FuzzySearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class FuzzySearchServiceTest {

    private FuzzySearchService fuzzySearchService;
    private JsonNodeFactory factory = JsonNodeFactory.instance;

    @BeforeEach
    void setUp() {
        fuzzySearchService = new FuzzySearchService();
    }

    @Test
    void exactMatchInName() {
        JsonNode node = createNode("John Doe", "GroupA", "HR");
        List<JsonNode> nodes = Collections.singletonList(node);

        List<JsonNode> result = fuzzySearchService.fuzzySearch(nodes, "John Doe", 70);
        System.out.println(result.toString());
        assertEquals(1, result.size());
    }

    @Test
    void typoInSearchTerm() {
        JsonNode node = createNode("John Doe", "GroupA", "HR");
        List<JsonNode> nodes = Collections.singletonList(node);

        List<JsonNode> result = fuzzySearchService.fuzzySearch(nodes, "Jhon Doe", 70);
        assertEquals(1, result.size()); // Similarity 75%
    }

    @Test
    void noMatchWhenBelowThreshold() {
        JsonNode node = createNode("Alice", "GroupA", "HR");
        List<JsonNode> nodes = Collections.singletonList(node);

        List<JsonNode> result = fuzzySearchService.fuzzySearch(nodes, "Alicia", 70);
        assertEquals(0, result.size()); // Similarity ~66.67%
    }

    @Test
    void matchInGroupField() {
        JsonNode node = createNode("John Doe", "GroupA", "HR");
        List<JsonNode> nodes = Collections.singletonList(node);

        List<JsonNode> result = fuzzySearchService.fuzzySearch(nodes, "groupa", 70);
        assertEquals(1, result.size());
    }

    private JsonNode createNode(String name, String group, String department) {
        ObjectNode node = factory.objectNode();
        node.put("name", name);
        node.put("group", group);
        node.put("department", department);
        return node;
    }
}