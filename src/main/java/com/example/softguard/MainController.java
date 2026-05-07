package com.example.softguard;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home() {
        return "index"; // templates/index.html 파일을 찾아서 보여줌
    }
}