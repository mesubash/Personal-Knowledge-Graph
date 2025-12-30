package com.developer.pkg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GraphVisualizationController {

    @GetMapping("/graph")
    public String showGraph() {
        return "graph";
    }
}
