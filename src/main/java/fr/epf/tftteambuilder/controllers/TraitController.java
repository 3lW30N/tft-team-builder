package fr.epf.tftteambuilder.controllers;

import fr.epf.tftteambuilder.models.Trait;
import fr.epf.tftteambuilder.models.Unit;
import fr.epf.tftteambuilder.repositories.TraitRepository;
import fr.epf.tftteambuilder.repositories.UnitTraitRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("trait")
@RestController
public class TraitController {
    private final UnitTraitRepository unitTraitRepository;
    private final TraitRepository traitRepository;

    public TraitController(UnitTraitRepository unitTraitRepository, TraitRepository traitRepository) {
        this.unitTraitRepository = unitTraitRepository;
        this.traitRepository = traitRepository;
    }

    @GetMapping("/all")
    public List<Trait> getAllTraits() {
        return traitRepository.findAll();
    }

    @GetMapping("/{id}")
    public Trait getTraitById(@PathVariable String id) {
        return traitRepository.findById(id).orElse(null);
    }

    @GetMapping("/units/{traitId}")
    public List<Unit> getUnitsOfTrait(@PathVariable String traitId) {
        return unitTraitRepository.findUnitsByTraitId(traitId);
    }
}
