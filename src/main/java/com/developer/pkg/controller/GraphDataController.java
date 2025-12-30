package com.developer.pkg.controller;


import com.developer.pkg.dto.GraphData;
import com.developer.pkg.service.GraphService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/graph")
@RequiredArgsConstructor
public class GraphDataController {

    private final GraphService graphService;

    @GetMapping("/data")
    public GraphData getGraphData() {
        return graphService.getGraphData();
    }
}
