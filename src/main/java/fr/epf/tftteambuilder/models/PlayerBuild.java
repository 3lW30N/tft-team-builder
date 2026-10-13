package fr.epf.tftteambuilder.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerBuild {
    private List<Build> playerBuilds;
    private List<Item> playerItems;

    public List<Build> getPlayerBuilds() {
        return playerBuilds;
    }

    public void setPlayerUnits(List<Build> playerUnits) {
        this.playerBuilds = playerUnits;
    }

    public List<Item> getPlayerItems() {
        return playerItems;
    }

    public void setPlayerItems(List<Item> playerItems) {
        this.playerItems = playerItems;
    }

    public List<Unit> getPlayerUnits() {
        return playerBuilds.stream().map(build -> build.getUnit()).toList();
    }

    public HashMap<Unit, Integer> getPlayerUnitsCount() {
        HashMap<Unit, Integer> counts = new HashMap<>();
        List<Unit> playerUnits = getPlayerUnits();

        for (Unit unit : playerUnits) {
            counts.merge(unit, 1, Integer::sum);
        }

        return counts;
    }

    private Float countUnitStarLevel(List<Build> buildOccurences) {
        Float count = 0f;
        for (Build build : buildOccurences) {
            count += build.getStars();
        }
        return count/3 +1;
    }
}
