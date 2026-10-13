package fr.epf.tftteambuilder.services;

import fr.epf.tftteambuilder.models.Build;
import fr.epf.tftteambuilder.models.Trait;
import fr.epf.tftteambuilder.models.Unit;
import fr.epf.tftteambuilder.repositories.UnitRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Long.sum;

@Service
public class UnitService {
    private final UnitRepository unitRepository;

    public UnitService(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

}
