package com.example.Endurance.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login.html";
    }
}
