package fr.epf.tftteambuilder.services;

import fr.epf.tftteambuilder.models.Item;
import fr.epf.tftteambuilder.repositories.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @Test
    void shouldReturnItemCrafted() {
        // Given
        Item negatronCloak = new Item();
        negatronCloak.setId("NegatronCloak");
        negatronCloak.setName("Negatron Cloak");

        Item tearOfTheGoddess = new Item();
        tearOfTheGoddess.setId("TearOfTheGoddess");
        tearOfTheGoddess.setName("Tear Of The Goddess");

        Item adaptiveHelm = new Item();
        adaptiveHelm.setId("AdaptiveHelm");
        adaptiveHelm.setName("Adaptive Helm");
        adaptiveHelm.setComponent1(negatronCloak);
        adaptiveHelm.setComponent2(tearOfTheGoddess);

        when(itemRepository.findCraft("NegatronCloak", "TearOfTheGoddess"))
                .thenReturn(adaptiveHelm);

        // When
        when(itemRepository.findCraft("NegatronCloak", "TearOfTheGoddess")).thenReturn(adaptiveHelm);
        Optional<Item> craft = itemService.getCraft(negatronCloak, tearOfTheGoddess);

        // Then
        assertThat(craft).contains(adaptiveHelm);

    }

    @Test
    void getCraftReturnsEmptyForNullComponentsOrUnknownCraft() {
        assertThat(itemService.getCraft(null, component("Tear"))).isEmpty();
        assertThat(itemService.getCraft(component("Sword"), null)).isEmpty();

        when(itemRepository.findCraft("Sword", "Tear")).thenReturn(null);
        assertThat(itemService.getCraft(component("Sword"), component("Tear"))).isEmpty();

        verify(itemRepository).findCraft("Sword", "Tear");
    }

    @Test
    void getCraftFromLooksUpCraftUsingComponentIds() {
        Item sword = component("Sword");
        Item tear = component("Tear");
        Item crafted = component("BlueBuff");
        when(itemRepository.findCraft("Sword", "Tear")).thenReturn(crafted);

        assertThat(itemService.getCraftFrom(sword, tear)).contains(crafted);
    }

    @Test
    void similarityScoreCountsDuplicateItemsOnlyUpToOwnedQuantity() {
        Item sword = component("Sword");
        Item bow = component("Bow");
        Item tear = component("Tear");

        assertThat(itemService.similarityScore(
                List.of(List.of(sword, sword, bow), List.of(tear, bow), List.of()),
                List.of(sword, bow, sword)
        )).containsExactly(3, 1, 0);
    }

    @Test
    void findCraftCombinationsHandlesNullAndEmptyComponents() {
        assertThat(itemService.findCraftCombinations(null)).isEmpty();
        assertThat(itemService.findCraftCombinations(List.of())).isEmpty();
        verifyNoInteractions(itemRepository);
    }

    @Test
    void findCraftCombinationsRecursivelyCraftsWithoutReusingComponents() {
        Item a = component("A");
        Item b = component("B");
        Item c = component("C");
        Item d = component("D");
        Item ab = component("AB");
        Item cd = component("CD");
        when(itemRepository.findCraft("A", "B")).thenReturn(ab);
        when(itemRepository.findCraft("C", "D")).thenReturn(cd);

        List<List<Item>> combinations = itemService.findCraftCombinations(List.of(a, b, c, d));

        assertThat(combinations).containsExactly(
                List.of(ab),
                List.of(ab, cd),
                List.of(cd),
                List.of(cd, ab)
        );
        verify(itemRepository, never()).findCraft("AB", "C");
        verify(itemRepository, never()).findCraft("CD", "A");
    }

    private static Item component(String id) {
        Item item = new Item();
        item.setId(id);
        item.setName(id);
        return item;
    }
}
