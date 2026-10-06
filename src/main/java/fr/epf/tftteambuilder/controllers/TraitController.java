package fr.epf.tftteambuilder.controllers;

import fr.epf.tftteambuilder.repositories.ItemRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("trait")
@RestController
public class TraitController {
    private final ItemRepository itemRepository;

    public TraitController(ItemRepository itemRepository) { this.itemRepository = itemRepository; }

    @GetMapping("/all")
    public Object getAllTraits() {
        return itemRepository.findAll();
    }

    @GetMapping("/{id}")
    public Object getTraitById(@PathVariable String id) {
        return itemRepository.findById(id);
    }
}
