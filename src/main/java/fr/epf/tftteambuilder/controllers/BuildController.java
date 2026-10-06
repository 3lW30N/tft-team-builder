package fr.epf.tftteambuilder.controllers;

import fr.epf.tftteambuilder.models.Build;
import fr.epf.tftteambuilder.repositories.BuildRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RequestMapping("build")
@RestController
public class BuildController {
    private final BuildRepository buildRepository;

    public BuildController(BuildRepository buildRepository) { this.buildRepository = buildRepository; }

    @GetMapping("/all")
    public List<Build> getAllBuilds() {
        return buildRepository.findAll();
    }
    
    @GetMapping("{unit_id}")
    public Build getBuildByUnitId(@RequestParam Long unit_id) {
        return buildRepository.findByUnitId(unit_id).orElse(null);
    }

    @GetMapping("/{id}")
    public Build getBuildById(@RequestParam Long id) {
        return buildRepository.findById(id).orElse(null);
    }
}
