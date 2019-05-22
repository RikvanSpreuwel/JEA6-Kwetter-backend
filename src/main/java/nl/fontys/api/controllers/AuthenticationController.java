package nl.fontys.api.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthenticationController {
    @GetMapping("/login")
    public String login(Model model, @RequestParam(defaultValue = "false") boolean error) {
        if (error)
            model.addAttribute("loginError", true);

        return "login";
    }
}
