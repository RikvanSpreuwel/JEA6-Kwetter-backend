package nl.fontys.api.controllers;

import nl.fontys.utils.Constants;
import nl.fontys.data.services.interfaces.IUserService;
import nl.fontys.models.entities.User;
import nl.fontys.models.resources.UserResource;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = Constants.USER_API_BASE_ROUTE, produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class UserController {
    private IUserService userService;
    private ModelMapper modelMapper;

    @Autowired
    public UserController(IUserService userService, ModelMapper modelMapper){
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public Resources<UserResource> get(){
        return new Resources<>(modelMapper.map(userService.findAll(), new TypeToken<List<UserResource>>(){}.getType()));
    }

    @GetMapping(value = "/search")
    public Resources<UserResource> search(@RequestParam String userName){
        return new Resources<>(modelMapper.map(userService.findAllByUserName(userName),
                new TypeToken<List<UserResource>>(){}.getType()));
    }

    @GetMapping(value = "/{id}")
    public UserResource get(@PathVariable(value = "id") UUID userId){
        return modelMapper.map(userService.findById(userId), UserResource.class);
    }

    @PostMapping()
    public UserResource post(@RequestBody @Valid User user){
        return modelMapper.map(userService.save(user), UserResource.class);
    }

    @PutMapping()
    public UserResource put(@RequestBody @Valid User user){
        return modelMapper.map(userService.update(user), UserResource.class);
    }

    @PutMapping(value = "/{userIdThatFollows}/follow/{userIdToFollow}")
    public boolean follow(@PathVariable UUID userIdThatFollows, @PathVariable UUID userIdToFollow){
        return userService.follow(userIdThatFollows, userIdToFollow);
    }

    @DeleteMapping(value = "/{userIdThatFollows}/unfollow/{userIdToUnFollow}")
    public boolean unfollow(@PathVariable UUID userIdThatFollows, @PathVariable UUID userIdToUnFollow) {
        return userService.unFollow(userIdThatFollows, userIdToUnFollow);
    }

    @GetMapping(value = "/getcurrentuser")
    public @ResponseBody
    UserResource getCurrentUser(Authentication authentication) {
        final String username = (String) authentication.getPrincipal();

        return userService.findAllByUserName(username).isEmpty() ? null : modelMapper.map(userService.findAllByUserName(username).get(0), UserResource.class);
    }
}
