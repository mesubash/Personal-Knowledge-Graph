package com.developer.pkg.controller;

import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller
public class Controller {

    @GetMapping
    public String index() {
        return "index";
    }
}
