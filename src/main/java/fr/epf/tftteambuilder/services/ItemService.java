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

    public Optional<Item> getCraft(Item component1, Item component2) {
        if (component1 == null || component2 == null) return Optional.empty();
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
                Optional<Item> craftedItem = getCraft(remainingComponents.get(i), remainingComponents.get(j));

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
            List<Item> compositionItems)
    {
        Map<String, Integer> dictionary = new HashMap<>();

        // Quantité de chaque item recherché dans la composition.
        for (Item item : compositionItems) {
            String name = item.getId();
            dictionary.put(name, dictionary.getOrDefault(name, 0) + 1);
        }

        List<Integer> scores = new ArrayList<>();

        for (List<Item> possibleItemCombinaison : craftCombinations) {
            // Chaque combinaison dispose des mêmes quantités au départ.
            Map<String, Integer> remainingItems = new HashMap<>(dictionary);
            int score = 0;

            for (Item item : possibleItemCombinaison) {
                String name = item.getId();
                int remaining = remainingItems.getOrDefault(name, 0);

                if (remaining > 0) {
                    remainingItems.put(name, remaining - 1);
                    score++;
                }
            }

            scores.add(score);
        }

        return scores;
    }
}

