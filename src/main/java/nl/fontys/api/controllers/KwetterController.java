package nl.fontys.api.controllers;

import nl.fontys.Utils.Constants;
import nl.fontys.data.services.KwetterService;
import nl.fontys.data.services.interfaces.IKwetterService;
import nl.fontys.models.Kwetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Constants.KWETTER_API_BASE_ROUTE)
public class KwetterController {
    private IKwetterService kwetterService;

    @Autowired
    public KwetterController(IKwetterService kwetterService){
        this.kwetterService = kwetterService;
    }

    @GetMapping(produces = "application/json")
    public List<Kwetter> get(){
        return kwetterService.findAll();
    }

    @GetMapping(value = "/searchbymessage", produces = "application/json")
    public List<Kwetter> searchByMessage(@RequestParam String message){
        return kwetterService.findAllByMessage(message);
    }

    @GetMapping(value = "/searchbyauthorid", produces = "application/json")
    public List<Kwetter> searchByAuthorId(@RequestParam UUID authorId){
        return kwetterService.findAllByAuthorId(authorId);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public Kwetter get(@PathVariable(value = "id") UUID kwetterId){
        return kwetterService.findById(kwetterId);
    }

    @GetMapping(value = "/timeline/{userId}", produces = "application/json")
    public List<Kwetter> getUserTimeline(@PathVariable(value = "userId") UUID userId) { return kwetterService.getUserTimeline(userId); }

    @PostMapping(value = "/{authorId}", produces = "application/json")
    public Kwetter post(@RequestBody String message, @PathVariable UUID authorId){
        return kwetterService.save(authorId, message);
    }
}