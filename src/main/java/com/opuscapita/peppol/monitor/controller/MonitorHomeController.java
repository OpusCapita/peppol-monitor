package com.opuscapita.peppol.monitor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MonitorHomeController {

    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }
}
