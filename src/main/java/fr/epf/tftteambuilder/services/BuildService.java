package fr.epf.tftteambuilder.services;

import fr.epf.tftteambuilder.models.Build;
import fr.epf.tftteambuilder.models.Unit;
import fr.epf.tftteambuilder.repositories.BuildRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildService {
    private final BuildRepository buildRepository;

    public BuildService(BuildRepository buildRepository) { this.buildRepository = buildRepository;}

    // Returns the percentage of meta unit the player has weighted by their cost
    public float buildSimilarity(List<Build> playerUnits, List<Build> metaUnits) {
        float s = 0f;
        for (Build build : metaUnits) {
            Unit unit = build.getUnit();
            Integer cost = unit.getCost();
            Integer starLevel = build.getStars();

            if (playerUnits.contains(unit)) {
                s+=(unit.getCost());
            }

        }
        Integer metaUnitsCost = metaUnits.stream().mapToInt(build -> build.getUnit().getCost()).sum();
        return s/metaUnitsCost;
    }


}
