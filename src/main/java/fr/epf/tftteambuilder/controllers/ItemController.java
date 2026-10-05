package fr.epf.tftteambuilder.controllers;

import fr.epf.tftteambuilder.models.Item;
import fr.epf.tftteambuilder.services.ItemService;
import fr.epf.tftteambuilder.repositories.ItemRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RequestMapping("item")
@RestController
public class ItemController {
    private final ItemRepository itemRepository;

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping("/all")
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @GetMapping("/items")
    public List<Item> getItemsByComponent(@RequestParam String componentId) {
        return itemRepository.findByComponentId(componentId);
    }

    @GetMapping("/components")
    public List<Item> getComponentsByItem(@RequestParam String itemId) {
        Item item, component1, component2;
        item = itemRepository.getById(itemId);
        component1 = item.getComponent1();
        component2 = item.getComponent2();
        return List.of(component1, component2);
    }
}
