package com.example.FuzzySearch;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class FuzzySearchService {

    private static final LevenshteinDistance LEVENSHTEIN_DISTANCE = LevenshteinDistance.getDefaultInstance();

    public List<JsonNode> fuzzySearch(List<JsonNode> nodes, String searchTerm, double threshold) {
        String lowerSearchTerm = searchTerm.trim().toLowerCase();
        List<ScoredJsonNode> scoredNodes = new ArrayList<>();

        for (JsonNode node : nodes) {
            String name = node.get("name").asText("").toLowerCase();
            String group = node.get("group").asText("").toLowerCase();
            String department = node.get("department").asText("").toLowerCase();

            double maxScore = calculateMaxSimilarity(name, group, department, lowerSearchTerm);

            if (maxScore >= threshold) {
                scoredNodes.add(new ScoredJsonNode(node, maxScore));
            }
        }

        Collections.sort(scoredNodes);
        return scoredNodes.stream()
                .map(ScoredJsonNode::getNode)
                .collect(Collectors.toList());
    }

    private double calculateMaxSimilarity(String name, String group, String department, String searchTerm) {
        double nameScore = calculateSimilarity(name, searchTerm);
        double groupScore = calculateSimilarity(group, searchTerm);
        double departmentScore = calculateSimilarity(department, searchTerm);

        return Math.max(nameScore, Math.max(groupScore, departmentScore));
    }

    private double calculateSimilarity(String field, String searchTerm) {
        if (field.isEmpty() && searchTerm.isEmpty()) return 100.0;
        if (field.isEmpty() || searchTerm.isEmpty()) return 0.0;

        int distance = LEVENSHTEIN_DISTANCE.apply(field, searchTerm);
        int maxLength = Math.max(field.length(), searchTerm.length());
        return (1.0 - (double) distance / maxLength) * 100;
    }

    private static class ScoredJsonNode implements Comparable<ScoredJsonNode> {
        private final JsonNode node;
        private final double score;

        ScoredJsonNode(JsonNode node, double score) {
            this.node = node;
            this.score = score;
        }

        public JsonNode getNode() { return node; }
        public double getScore() { return score; }

        @Override
        public int compareTo(ScoredJsonNode other) {
            return Double.compare(other.score, this.score); // Descending order
        }
    }
}