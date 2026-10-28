package fr.epf.tftteambuilder.controllers;

import fr.epf.tftteambuilder.dto.BuildDto;
import fr.epf.tftteambuilder.dto.Mapper;
import fr.epf.tftteambuilder.repositories.BuildRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("build")
@RestController
public class BuildController {
    private final BuildRepository buildRepository;
    private final Mapper mapper;

    public BuildController(BuildRepository buildRepository) {
        this.buildRepository = buildRepository;
        this.mapper = new Mapper();
    }

    @GetMapping("/all")
    public List<BuildDto> getAllBuilds() {
        return mapper.buildMapper(buildRepository.findAll());
    }

    @GetMapping("/{comp_id}")
    public List<BuildDto> getBuildsByCompId(@PathVariable Integer comp_id) {
        return mapper.buildMapper(buildRepository.getByCompId(comp_id));
    }
}
