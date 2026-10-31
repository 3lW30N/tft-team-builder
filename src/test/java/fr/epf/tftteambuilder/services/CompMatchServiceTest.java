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

import java.util.ArrayList;
import java.util.Arrays;
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
        assertThat(matches.getFirst().score()).isEqualTo(61);
        assertThat(matches.getFirst().matchedUnits()).containsExactly("Carry");
        assertThat(matches.getFirst().missingUnits()).containsExactly("Support");
        assertThat(matches.getFirst().reasons()).contains("Upgraded units add 1 point");
        assertThat(matches.get(1).score()).isEqualTo(5);
    }

    @Test
    void addsSmallStarBonusOnlyForMatchedUnits() {
        Comp comp = comp(1, build(unit("Carry")), build(unit("Support")));
        when(comps.findAll()).thenReturn(List.of(comp));
        when(traits.findAll()).thenReturn(List.of());

        assertThat(scoreFor(List.of(new MatchRequest.BoardUnit("Carry", 1)))).isEqualTo(30);
        assertThat(scoreFor(List.of(new MatchRequest.BoardUnit("Carry", 2)))).isEqualTo(31);
        assertThat(scoreFor(List.of(new MatchRequest.BoardUnit("Carry", 3)))).isEqualTo(32);
        assertThat(scoreFor(List.of(new MatchRequest.BoardUnit("Carry", 3),
                new MatchRequest.BoardUnit("Other", 3)))).isEqualTo(32);
        assertThat(scoreFor(List.of(new MatchRequest.BoardUnit("Carry", null)))).isEqualTo(30);
    }

    @Test
    void capsStarBonus() {
        Build[] builds = new Build[6];
        List<MatchRequest.BoardUnit> board = new java.util.ArrayList<>();
        for (int i = 0; i < builds.length; i++) {
            String id = "Unit" + i;
            builds[i] = build(unit(id));
            board.add(new MatchRequest.BoardUnit(id, 3));
        }
        when(comps.findAll()).thenReturn(List.of(comp(1, builds)));
        when(traits.findAll()).thenReturn(List.of());

        CompMatchDto match = service.rank(new MatchRequest(board, List.of())).getFirst();
        assertThat(match.score()).isEqualTo(65);
        assertThat(match.reasons()).contains("Upgraded units add 5 points");
    }

    @Test
    void keepsPerfectMatchAtOneHundred() {
        Unit carry = unit("Carry");
        Item sword = item("Sword");
        when(comps.findAll()).thenReturn(List.of(comp(1, build(carry, sword))));
        when(traits.findAll()).thenReturn(List.of(relation(carry, "Lunar")));

        CompMatchDto match = service.rank(new MatchRequest(
                List.of(new MatchRequest.BoardUnit("Carry", 3)), List.of("Sword"))).getFirst();
        assertThat(match.score()).isEqualTo(100);
    }

    @Test
    void handlesNullInputsUnmatchedUnitsAndEmptyComps() {
        Comp unmatched = comp(2, build(unit("Carry")));
        Comp empty = comp(1);
        when(comps.findAll()).thenReturn(List.of(unmatched, empty));
        when(traits.findAll()).thenReturn(List.of());

        List<CompMatchDto> matches = service.rank(new MatchRequest(null, null));

        assertThat(matches).extracting(match -> match.comp().id()).containsExactly(1, 2);
        assertThat(matches).extracting(CompMatchDto::score).containsExactly(0, 0);
        assertThat(matches.get(1).matchedUnits()).isEmpty();
        assertThat(matches.get(1).missingUnits()).containsExactly("Carry");
        assertThat(matches.get(1).reasons()).isEmpty();
    }

    @Test
    void normalizesBoardStarsAndIgnoresInvalidItemIds() {
        Unit carry = unit("Carry");
        Unit support = unit("Support");
        Comp comp = comp(1, build(carry, item("Sword")), build(support));
        when(comps.findAll()).thenReturn(List.of(comp));
        when(traits.findAll()).thenReturn(List.of(relation(carry, "Lunar")));

        CompMatchDto match = service.rank(new MatchRequest(
                new ArrayList<>(Arrays.asList(
                        new MatchRequest.BoardUnit("Carry", 0),
                        new MatchRequest.BoardUnit("Carry", 9),
                        new MatchRequest.BoardUnit(null, 2),
                        null
                )),
                Arrays.asList(null, " ", "Unknown")
        )).getFirst();

        assertThat(match.matchedUnits()).containsExactly("Carry");
        assertThat(match.missingUnits()).containsExactly("Support");
        assertThat(match.score()).isEqualTo(67);
        assertThat(match.reasons()).containsExactly(
                "You have 1/2 of the comp's units",
                "Shares 1 carry",
                "Upgraded units add 2 points",
                "Strong trait overlap"
        );
    }

    private int scoreFor(List<MatchRequest.BoardUnit> board) {
        return service.rank(new MatchRequest(board, List.of())).getFirst().score();
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
