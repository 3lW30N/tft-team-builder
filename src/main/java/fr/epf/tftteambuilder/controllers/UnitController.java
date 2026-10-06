package fr.epf.tftteambuilder.controllers;


import fr.epf.tftteambuilder.models.Unit;
import fr.epf.tftteambuilder.repositories.UnitRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

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
    public Unit getUnitById(@PathVariable @RequestParam String id) {
        return unitRepository.findById(id).orElse(null);
    }
}
