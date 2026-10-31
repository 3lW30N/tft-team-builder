package fr.epf.tftteambuilder.services;

import fr.epf.tftteambuilder.models.Item;
import fr.epf.tftteambuilder.repositories.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

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
}
