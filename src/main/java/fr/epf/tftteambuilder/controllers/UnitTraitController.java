package fr.epf.tftteambuilder.controllers;

import fr.epf.tftteambuilder.models.Trait;
import fr.epf.tftteambuilder.models.Unit;
import fr.epf.tftteambuilder.repositories.UnitTraitRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("traits")
@RestController
public class UnitTraitController {
    private final UnitTraitRepository unitTraitRepository;

    public UnitTraitController(UnitTraitRepository unitTraitRepository) {
        this.unitTraitRepository = unitTraitRepository;
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