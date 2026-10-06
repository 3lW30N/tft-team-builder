package fr.epf.tftteambuilder.controllers;

import fr.epf.tftteambuilder.repositories.CompRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("comp")
@RestController
public class CompController {
    private final CompRepository compRepository;

    public CompController(CompRepository compRepository) {
        this.compRepository = compRepository;
    }

    
}
