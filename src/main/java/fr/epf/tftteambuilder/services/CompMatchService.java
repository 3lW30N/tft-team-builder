package fr.epf.tftteambuilder.services;

import fr.epf.tftteambuilder.dto.CompMatchDto;
import fr.epf.tftteambuilder.dto.MatchRequest;
import fr.epf.tftteambuilder.dto.Mapper;
import fr.epf.tftteambuilder.models.Build;
import fr.epf.tftteambuilder.models.Comp;
import fr.epf.tftteambuilder.models.Item;
import fr.epf.tftteambuilder.models.UnitTrait;
import fr.epf.tftteambuilder.repositories.CompRepository;
import fr.epf.tftteambuilder.repositories.UnitTraitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CompMatchService {
    private final CompRepository comps;
    private final UnitTraitRepository unitTraits;
    private final Mapper mapper = new Mapper();

    public CompMatchService(CompRepository comps, UnitTraitRepository unitTraits) {
        this.comps = comps;
        this.unitTraits = unitTraits;
    }

    @Transactional(readOnly = true)
    public List<CompMatchDto> rank(MatchRequest request) {
        Set<String> owned = request.units() == null ? Set.of() : request.units().stream()
                .filter(unit -> unit != null && unit.unitId() != null)
                .map(MatchRequest.BoardUnit::unitId).collect(Collectors.toSet());
        Map<String, Long> items = counts(request.itemIds() == null ? List.of() : request.itemIds());
        Map<String, List<String>> traitsByUnit = new HashMap<>();
        for (UnitTrait relation : unitTraits.findAll()) {
            traitsByUnit.computeIfAbsent(relation.getUnit().getId(), ignored -> new ArrayList<>())
                    .add(relation.getTrait().getId());
        }
        Map<String, Long> ownedTraits = traitCounts(owned, traitsByUnit);
        return comps.findAll().stream()
                .map(comp -> score(comp, owned, items, ownedTraits, traitsByUnit))
                .sorted(Comparator.comparingInt(CompMatchDto::score).reversed()
                        .thenComparing(match -> match.comp().id()))
                .toList();
    }

    private CompMatchDto score(Comp comp, Set<String> owned, Map<String, Long> items,
                               Map<String, Long> ownedTraits, Map<String, List<String>> traitsByUnit) {
        List<Build> builds = comp.getBuilds();
        List<String> units = builds.stream().map(build -> build.getUnit().getId()).toList();
        Set<String> carries = builds.stream().filter(build -> !buildItems(build).isEmpty())
                .map(build -> build.getUnit().getId()).collect(Collectors.toSet());
        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        int haveWeight = 0;
        int totalWeight = 0;
        for (String unit : units) {
            int weight = carries.contains(unit) ? 2 : 1;
            totalWeight += weight;
            if (owned.contains(unit)) {
                haveWeight += weight;
                matched.add(unit);
            } else {
                missing.add(unit);
            }
        }
        double unitOverlap = totalWeight == 0 ? 0 : (double) haveWeight / totalWeight;

        Map<String, Long> compTraits = traitCounts(units, traitsByUnit);
        long traitTotal = compTraits.values().stream().mapToLong(Long::longValue).sum();
        long traitShared = compTraits.entrySet().stream()
                .mapToLong(entry -> Math.min(entry.getValue(), ownedTraits.getOrDefault(entry.getKey(), 0L))).sum();
        double traitOverlap = traitTotal == 0 ? 0 : (double) traitShared / traitTotal;

        Map<String, Long> wanted = counts(builds.stream().flatMap(build -> buildItems(build).stream())
                .map(Item::getId).toList());
        long itemTotal = wanted.values().stream().mapToLong(Long::longValue).sum();
        long itemHave = wanted.entrySet().stream()
                .mapToLong(entry -> Math.min(entry.getValue(), items.getOrDefault(entry.getKey(), 0L))).sum();
        double itemOverlap = itemTotal == 0 ? 0 : (double) itemHave / itemTotal;

        double raw = 100 * (0.60 * unitOverlap + 0.25 * traitOverlap + 0.15 * itemOverlap);
        if (matched.isEmpty()) raw = Math.min(raw, 5);
        List<String> reasons = new ArrayList<>();
        if (!matched.isEmpty()) reasons.add("You have " + matched.size() + "/" + units.size() + " of the comp's units");
        long matchedCarries = matched.stream().filter(carries::contains).count();
        if (matchedCarries > 0) reasons.add("Shares " + matchedCarries + (matchedCarries > 1 ? " carry units" : " carry"));
        if (traitOverlap >= 0.5) reasons.add("Strong trait overlap");
        if (itemTotal > 0 && itemHave > 0) reasons.add("You hold " + itemHave + "/" + itemTotal + " of its items");
        return new CompMatchDto(mapper.compMapper(comp), Math.round((float) raw), matched, missing, reasons);
    }

    private static List<Item> buildItems(Build build) {
        List<Item> result = new ArrayList<>(3);
        if (build.getItem1() != null) result.add(build.getItem1());
        if (build.getItem2() != null) result.add(build.getItem2());
        if (build.getItem3() != null) result.add(build.getItem3());
        return result;
    }

    private static Map<String, Long> traitCounts(Iterable<String> units, Map<String, List<String>> traitsByUnit) {
        Map<String, Long> result = new HashMap<>();
        for (String unit : units) {
            for (String trait : traitsByUnit.getOrDefault(unit, List.of())) result.merge(trait, 1L, Long::sum);
        }
        return result;
    }

    private static Map<String, Long> counts(List<String> ids) {
        return ids.stream().filter(id -> id != null && !id.isBlank())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }
}
