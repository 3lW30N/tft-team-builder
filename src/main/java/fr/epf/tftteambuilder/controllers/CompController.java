package fr.epf.tftteambuilder.controllers;

import fr.epf.tftteambuilder.dto.CompDto;
import fr.epf.tftteambuilder.dto.Mapper;
import fr.epf.tftteambuilder.dto.CompMatchDto;
import fr.epf.tftteambuilder.dto.MatchRequest;
import fr.epf.tftteambuilder.models.Comp;
import fr.epf.tftteambuilder.repositories.CompRepository;
import fr.epf.tftteambuilder.services.CompMatchService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("comp")
@RestController
@Transactional(readOnly = true)
public class CompController {
    private final CompRepository compRepository;
    private final Mapper mapper;
    private final CompMatchService matchService;

    public CompController(CompRepository compRepository, CompMatchService matchService) {
        this.compRepository = compRepository;
        this.matchService = matchService;
        this.mapper = new Mapper();
    }

    @GetMapping("/all")
    public List<CompDto> getAllComps() {
        List<Comp> comps = compRepository.findAll();
        return mapper.compMapper(comps);
    }

    @GetMapping("/{id}")
    public CompDto getCompById(@PathVariable Integer id) {
        Comp comp = compRepository.getById(id);
        return mapper.compMapper(comp);
    }

    @PostMapping("/match")
    public List<CompMatchDto> match(@RequestBody MatchRequest request) {
        return matchService.rank(request);
    }

}
