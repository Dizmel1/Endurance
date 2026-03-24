package com.example.Endurance.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("title", "Главная страница");
        return "home";
    }
    @GetMapping("/signIn")
    public String signIn(Model model){
        model.addAttribute("title");
        return "signIn";
    }
    @GetMapping("/signUp")
    public String signUp(Model model){
        model.addAttribute("title");
        return "signUp";
    }
}
