package com.smartstudy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping({"/", "/theme"})
    public String theme() {
        return "theme";
    }

    @GetMapping("/ui")
    public String ui() {
        return "ui";
    }
}
