package nl.fontys.api.controllers;

import nl.fontys.data.services.UserService;
import nl.fontys.data.services.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.websocket.server.PathParam;
import java.util.UUID;

@Controller
public class AuthenticationController {
    @RequestMapping("/login")
    public String login(Model model, @RequestParam(defaultValue = "false") boolean error) {
        if (error)
            model.addAttribute("loginError", true);

        return "login";
    }
}
