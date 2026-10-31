package fr.epf.tftteambuilder.dto;

import java.util.List;

public record MatchRequest(List<BoardUnit> units, List<String> itemIds) {
    public record BoardUnit(String unitId, Integer stars) {}
}
