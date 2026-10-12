package fr.epf.tftteambuilder.services;

import fr.epf.tftteambuilder.models.Item;
import fr.epf.tftteambuilder.repositories.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    private Optional<Item> craft(Item component1, Item component2) {
        return Optional.ofNullable(itemRepository.findCraft(component1.getId(), component2.getId()));
    }

    /**
     * Returns every possible set of crafts that can be made from the supplied
     * components. A component can only be used once in a given combination.
     */
    public List<List<Item>> findCraftCombinations(List<Item> components) {
        if (components == null || components.size() < 2) {
            return List.of();
        }

        List<List<Item>> results = new ArrayList<>();
        findCraftCombinations(new ArrayList<>(components), new ArrayList<>(), results);
        return results;
    }
    // Backtracking
    private void findCraftCombinations(List<Item> remainingComponents, List<Item> currentCombination, List<List<Item>> results) {
        for (int i = 0; i < remainingComponents.size() - 1; i++) {
            for (int j = i + 1; j < remainingComponents.size(); j++) {
                Optional<Item> craftedItem = craft(remainingComponents.get(i), remainingComponents.get(j));

                if (craftedItem.isEmpty()) {
                    continue;
                }

                List<Item> nextCombination = new ArrayList<>(currentCombination);
                nextCombination.add(craftedItem.get());
                results.add(Collections.unmodifiableList(nextCombination));

                List<Item> nextRemainingComponents = new ArrayList<>(remainingComponents);
                nextRemainingComponents.remove(j);
                nextRemainingComponents.remove(i);
                findCraftCombinations(nextRemainingComponents, nextCombination, results);
            }
        }
    }


    public List<Integer> similarityScore(
            List<List<Item>> craftCombinations,
            List<List<Item>> compositionItems)
    {

        Map<String, Integer> dictionary = new HashMap<String, Integer>();
        // initialisation de la hashmap :
        for (List<Item> items : craftCombinations) {
            for (Item item : items) {
                dictionary.put(item.getName(), 0);
            }
        }

        for (int i = 0; i < craftCombinations.size(); i++) {
            for (int k = 0 ; k < compositionItems.size() ; k++)
                for (int l = 0 ; l < compositionItems.get(i).size() ; l++)
                    for ()

                List<Item> compoRn = new ArrayList<>(compositionItems); // un simple copy de la compo actuellement étudiée



                    }

                    }


                }


            }
        }

    }

}
