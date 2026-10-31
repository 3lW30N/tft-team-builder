package fr.epf.tftteambuilder.controllers;


import fr.epf.tftteambuilder.models.Unit;
import fr.epf.tftteambuilder.repositories.UnitRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("unit")
@RestController
public class UnitController {
    private final UnitRepository unitRepository;

    public UnitController(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @GetMapping("/all")
    public List<Unit> getAllUnits() {
        return unitRepository.findAll();
    }

    @GetMapping("/{id}")
    public Unit getUnitById(@PathVariable String id) {
        return unitRepository.findById(id).orElse(null);
    }

    @GetMapping("/{id}/cost")
    public Integer getUnitCost(@PathVariable String id) {
        Unit unit = unitRepository.findById(id).orElse(null);
        if (unit != null) {
            return unit.getCost();
        }
        return null;
    }
}
