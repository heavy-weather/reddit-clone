package com.anthonyguidotti.forum;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApplicationController {
    @GetMapping(value = "/")
    public String home() {
        return "index";
    }
}
