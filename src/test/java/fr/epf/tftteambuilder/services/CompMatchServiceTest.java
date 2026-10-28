package fr.epf.tftteambuilder.services;

import fr.epf.tftteambuilder.dto.CompMatchDto;
import fr.epf.tftteambuilder.dto.MatchRequest;
import fr.epf.tftteambuilder.models.Build;
import fr.epf.tftteambuilder.models.Comp;
import fr.epf.tftteambuilder.models.Item;
import fr.epf.tftteambuilder.models.Trait;
import fr.epf.tftteambuilder.models.Unit;
import fr.epf.tftteambuilder.models.UnitTrait;
import fr.epf.tftteambuilder.repositories.CompRepository;
import fr.epf.tftteambuilder.repositories.UnitTraitRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CompMatchServiceTest {
    private final CompRepository comps = mock(CompRepository.class);
    private final UnitTraitRepository traits = mock(UnitTraitRepository.class);
    private final CompMatchService service = new CompMatchService(comps, traits);

    @Test
    void ranksByChampionTraitsAndItemMultiset() {
        Unit carry = unit("Carry");
        Unit support = unit("Support");
        Unit other = unit("Other");
        Item sword = item("Sword");
        Comp best = comp(1, build(carry, sword, sword), build(support));
        Comp worse = comp(2, build(other, sword));
        when(comps.findAll()).thenReturn(List.of(worse, best));
        when(traits.findAll()).thenReturn(List.of(relation(carry, "Lunar"), relation(support, "Lunar")));

        List<CompMatchDto> matches = service.rank(new MatchRequest(
                List.of(new MatchRequest.BoardUnit("Carry", 2)), List.of("Sword")));

        assertThat(matches).extracting(match -> match.comp().id()).containsExactly(1, 2);
        assertThat(matches.getFirst().score()).isEqualTo(60);
        assertThat(matches.getFirst().matchedUnits()).containsExactly("Carry");
        assertThat(matches.getFirst().missingUnits()).containsExactly("Support");
        assertThat(matches.get(1).score()).isEqualTo(5);
    }

    private static Unit unit(String id) {
        Unit unit = new Unit();
        unit.setId(id);
        unit.setName(id);
        return unit;
    }

    private static Item item(String id) {
        Item item = new Item();
        item.setId(id);
        item.setName(id);
        return item;
    }

    private static Build build(Unit unit, Item... items) {
        Build build = new Build();
        build.setUnit(unit);
        if (items.length > 0) build.setItem1(items[0]);
        if (items.length > 1) build.setItem2(items[1]);
        if (items.length > 2) build.setItem3(items[2]);
        return build;
    }

    private static Comp comp(int id, Build... builds) {
        Comp comp = new Comp();
        comp.setId(id);
        comp.setName("Comp " + id);
        comp.setBuilds(List.of(builds));
        return comp;
    }

    private static UnitTrait relation(Unit unit, String traitId) {
        Trait trait = new Trait();
        trait.setId(traitId);
        trait.setName(traitId);
        UnitTrait relation = new UnitTrait();
        relation.setUnit(unit);
        relation.setTrait(trait);
        return relation;
    }
}
