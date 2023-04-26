package com.controllers.viewers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {
    @RequestMapping("/view-qap")
    public String viewProducts() {
        return "viewQAPEntities";
    }
    @RequestMapping("/add-qap")
    public String addProducts() {
        return "add-qap";
    }
}