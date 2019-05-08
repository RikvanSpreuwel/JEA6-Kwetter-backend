package nl.fontys.api.controllers;

import nl.fontys.Utils.Constants;
import nl.fontys.data.services.interfaces.IUserService;
import nl.fontys.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Constants.USER_API_BASE_ROUTE)
public class UserController {
    private IUserService userService;

    @Autowired
    public UserController(IUserService userService){
        this.userService = userService;
    }

    @GetMapping(produces = "application/json")
    public List<User> get(){
        return userService.findAll();
    }

    @GetMapping(value = "/search", produces = "application/json")
    public List<User> search(@RequestParam String userName){
        return userService.findAllByUserName(userName);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public User get(@PathVariable(value = "id") UUID userId){
        return userService.findById(userId);
    }

    @PostMapping(produces = "application/json")
    public User post(@RequestBody @Valid User user){
        return userService.save(user);
    }

    @PutMapping(produces = "application/json")
    public User put(@RequestBody @Valid User user){
        return userService.update(user);
    }

    @PutMapping(value = "/{userIdThatFollows}/follow/{userIdToFollow}", produces = "application/json")
    public boolean follow(@PathVariable UUID userIdThatFollows, @PathVariable UUID userIdToFollow){
        return userService.follow(userIdThatFollows, userIdToFollow);
    }

    @DeleteMapping(value = "/{userIdThatFollows}/unfollow/{userIdToUnFollow}", produces = "application/json")
    public boolean unfollow(@PathVariable UUID userIdThatFollows, @PathVariable UUID userIdToUnFollow) {
        return userService.unFollow(userIdThatFollows, userIdToUnFollow);
    }

    @GetMapping(value = "/getcurrentuser", produces = "application/json")
    public @ResponseBody
    User getCurrentUser() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final String username = (String) auth.getPrincipal();

        return userService.findAllByUserName(username).isEmpty() ? null : userService.findAllByUserName(username).get(0);
    }
}
