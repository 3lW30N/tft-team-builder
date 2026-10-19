package fr.epf.tftteambuilder.controllers;

import fr.epf.tftteambuilder.dto.CompDto;
import fr.epf.tftteambuilder.dto.Mapper;
import fr.epf.tftteambuilder.models.Comp;
import fr.epf.tftteambuilder.repositories.CompRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("comp")
@RestController
public class CompController {
    private final CompRepository compRepository;
    private final Mapper mapper;

    public CompController(CompRepository compRepository) {
        this.compRepository = compRepository;
        this.mapper = new Mapper();
    }

    @GetMapping("/all")
    public List<CompDto> getAllComps() {
        List<Comp> comps = compRepository.findAll();
        return comps.stream().map(mapper::compMapper).toList();
    }

}
