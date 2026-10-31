package fr.epf.tftteambuilder.unit;

import fr.epf.tftteambuilder.models.Item;
import fr.epf.tftteambuilder.repositories.ItemRepository;
import fr.epf.tftteambuilder.services.ItemService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private Item component1;

    @Mock
    private Item component2;

    @Mock
    private Item craftedItem;

    @Test
    void testFindCraftCombinationsWithLessThanTwoComponents() {
        assertTrue(itemService.findCraftCombinations(List.of(component1)).isEmpty());

        verifyNoInteractions(itemRepository);
    }

    @Test
    void testFindCraftCombinationsWithNoPossibleCraft() {
        when(component1.getId()).thenReturn("1L");
        when(component2.getId()).thenReturn("2L");

        when(itemRepository.findCraft("1L", "2L")).thenReturn(null);

        List<List<Item>> result = itemService.findCraftCombinations(
                List.of(component1, component2)
        );

        assertTrue(result.isEmpty());

        verify(itemRepository).findCraft("1L", "2L");
    }

    @Test
    void testFindCraftCombinationsWithOnePossibleCraft() {
        when(component1.getId()).thenReturn("1L");
        when(component2.getId()).thenReturn("2L");

        when(itemRepository.findCraft("1L", "2L")).thenReturn(craftedItem);

        List<List<Item>> result = itemService.findCraftCombinations(
                List.of(component1, component2)
        );

        assertEquals(1, result.size());
        assertEquals(List.of(craftedItem), result.get(0));

        verify(itemRepository).findCraft("1L", "2L");
    }
}