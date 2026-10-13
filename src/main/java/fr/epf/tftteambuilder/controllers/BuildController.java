package fr.epf.tftteambuilder.controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.epf.tftteambuilder.models.Build;
import fr.epf.tftteambuilder.models.Unit;
import fr.epf.tftteambuilder.repositories.BuildRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RequestMapping("build")
@RestController
public class BuildController {
    private final BuildRepository buildRepository;

    public BuildController(BuildRepository buildRepository) { this.buildRepository = buildRepository; }

    @GetMapping("/all")
    @JsonIgnore
    public List<Build> getAllBuilds() {
        return buildRepository.findAll();
    }

    @GetMapping("/{id}")
    public Build getBuildById(@PathVariable Long id) {
        return buildRepository.findById(id).orElse(null);
    }

    @GetMapping("/{buildId}/unit")
    public Unit getUnitByBuildId(@PathVariable String buildId) {
        Build build = buildRepository.findById(buildId);
        return build.getUnit();
    }
}
