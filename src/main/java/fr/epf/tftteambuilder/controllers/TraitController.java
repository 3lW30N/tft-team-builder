package fr.epf.tftteambuilder.controllers;

import fr.epf.tftteambuilder.repositories.TraitRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("trait")
@RestController
public class TraitController {
    private final TraitRepository TraitRepository;

    public TraitController(TraitRepository TraitRepository) { this.TraitRepository = TraitRepository; }

    @GetMapping("/all")
    public Object getAllTraits() {
        return TraitRepository.findAll();
    }

    @GetMapping("/{id}")
    public Object getTraitById(@PathVariable String id) {
        return TraitRepository.findById(id);
    }
}
