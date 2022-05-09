package vn.com.tma.training.ProjectTraining.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class IndexController {
    @GetMapping("/")
    public String index() {
        return "Hello World!";
    }
}