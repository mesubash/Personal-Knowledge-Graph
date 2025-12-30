package com.developer.pkg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphData {
    private List<NodeData> nodes;
    private List<LinkData> links;
    private Statistics stats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NodeData {
        private UUID id;
        private String label;
        private String type; // "note" or "tag"
        private String content;
        private List<String> tags;
        private Integer referenceCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LinkData {
        private UUID source;
        private UUID target;
        private String type; // "references" or "has-tag"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Statistics {
        private long noteCount;
        private long tagCount;
        private long referenceCount;
    }
}
