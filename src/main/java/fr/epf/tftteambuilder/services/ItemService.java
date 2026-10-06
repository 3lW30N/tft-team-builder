package fr.epf.tftteambuilder.services;

import fr.epf.tftteambuilder.models.Item;
import fr.epf.tftteambuilder.repositories.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    ItemRepository itemRepository;

    private Optional<Item> craft(Item component1, Item component2) {
        return Optional.ofNullable(itemRepository.findCraft(component1.getId(), component2.getId()));
    }

    private List<Optional<Item>> findCrafts(List<Item> components) {
        List<Optional<Item>> results = new ArrayList<>();

        for (int i = 0; i < components.size(); i++) {
            for (int j = i + 1; j < components.size(); j++) {
                results.add(craft(components.get(i), components.get(j)));
            }
        }
        return results;
    }


}
