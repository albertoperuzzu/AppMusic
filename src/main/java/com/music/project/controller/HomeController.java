package com.music.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("username", "Alberto");
        return "index";
    }

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code, Model model) {
        model.addAttribute("authCode", code);
        return "login_ok";
    }

}