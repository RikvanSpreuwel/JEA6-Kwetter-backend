package nl.fontys.api.controllers;

import nl.fontys.models.resources.KwetterResource;
import nl.fontys.utils.Constants;
import nl.fontys.data.services.interfaces.IKwetterService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = Constants.KWETTER_API_BASE_ROUTE, produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class KwetterController {
    private IKwetterService kwetterService;
    private ModelMapper modelMapper;

    @Autowired
    public KwetterController(IKwetterService kwetterService, ModelMapper modelMapper){
        this.kwetterService = kwetterService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public Resources<KwetterResource> get(){
        return new Resources<>(modelMapper.map(kwetterService.findAll(),
                new TypeToken<List<KwetterResource>>(){}.getType()));
    }

    @GetMapping(value = "/searchbymessage")
    public Resources<KwetterResource> searchByMessage(@RequestParam String message){
        return new Resources<>(modelMapper.map(kwetterService.findAllByMessage(message),
                new TypeToken<List<KwetterResource>>(){}.getType()));
    }

    @GetMapping(value = "/searchbyauthorid")
    public Resources<KwetterResource> searchByAuthorId(@RequestParam UUID authorId){
        return new Resources<>(modelMapper.map(kwetterService.findAllByAuthorId(authorId),
                new TypeToken<List<KwetterResource>>(){}.getType()));
    }

    @GetMapping(value = "/{id}")
    public KwetterResource get(@PathVariable(value = "id") UUID kwetterId){
        return modelMapper.map(kwetterService.findById(kwetterId), KwetterResource.class);
    }

    @GetMapping(value = "/timeline/{userId}")
    public Resources<KwetterResource> getUserTimeline(@PathVariable(value = "userId") UUID userId) {
        return new Resources<>(modelMapper.map(kwetterService.getUserTimeline(userId),
                new TypeToken<List<KwetterResource>>(){}.getType()));
    }

    @PostMapping(value = "/{authorId}")
    public KwetterResource post(@RequestBody String message, @PathVariable UUID authorId){
        return modelMapper.map(kwetterService.save(authorId, message), KwetterResource.class);
    }
}