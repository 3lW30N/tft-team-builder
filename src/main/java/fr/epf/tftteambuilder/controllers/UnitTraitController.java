package fr.epf.tftteambuilder.controllers;

import fr.epf.tftteambuilder.models.Trait;
import fr.epf.tftteambuilder.models.Unit;
import fr.epf.tftteambuilder.repositories.UnitTraitRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.springframework.transaction.annotation.Transactional;

@RequestMapping("traits")
@RestController
public class UnitTraitController {
    private final UnitTraitRepository unitTraitRepository;

    public UnitTraitController(UnitTraitRepository unitTraitRepository) {
        this.unitTraitRepository = unitTraitRepository;
    }

    public record UnitTraitsDto(String unitId, List<String> traitIds) {}

    @GetMapping("/all")
    @Transactional(readOnly = true)
    public List<UnitTraitsDto> getAllUnitTraits() {
        Map<String, List<String>> grouped = new LinkedHashMap<>();
        unitTraitRepository.findAll().forEach(row -> grouped
                .computeIfAbsent(row.getUnit().getId(), ignored -> new ArrayList<>())
                .add(row.getTrait().getId()));
        return grouped.entrySet().stream()
                .map(entry -> new UnitTraitsDto(entry.getKey(), entry.getValue())).toList();
    }

    @GetMapping("/{unitId}")
    public List<Trait> getTraitsByUnitId(@PathVariable String unitId) {
        return unitTraitRepository.findTraitsByUnitId(unitId);
    }

    @GetMapping("/units/{traitId}")
    public List<Unit> getUnitsByTraitId(@PathVariable String traitId) {
        return unitTraitRepository.findUnitsByTraitId(traitId);
    }
}
