package nl.fontys.api.controllers;

import nl.fontys.data.services.interfaces.IKwetterService;
import nl.fontys.data.services.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private IUserService userService;
    private IKwetterService kwetterService;

    @Autowired
    public AdminController(IUserService userService, IKwetterService kwetterService){
        this.userService = userService;
        this.kwetterService = kwetterService;
    }

    @GetMapping
    public String adminPanel(Model model, @RequestParam(required = false) UUID userId){
        if (userId != null)
            model.addAttribute("selectedUser", userService.findById(userId));

        model.addAttribute("users", userService.findAll());

        return "admin-panel.html";
    }

    @DeleteMapping("/delete/kwetter")
    public String deleteKwetter(Model model, @RequestParam UUID kwetterId) {
        UUID correspondingUserId = kwetterService.findById(kwetterId).getAuthor().getId();
        kwetterService.delete(kwetterId);

        return "redirect:/admin?userId=" + correspondingUserId;
    }

    @DeleteMapping("/delete/user")
    public String deleteUser(Model model, @RequestParam UUID userId) {
        userService.delete(userId);

        return "redirect:/admin";
    }
}
