package com.pjs.golf.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/")
public class MainController {

    @ResponseBody
    @RequestMapping(value = "hello")
    public String hello() {

        return "hello";
    }

    @GetMapping("/")
    public String main() {
        return "index";
    }
}
