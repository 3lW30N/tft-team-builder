package fr.epf.tftteambuilder.dto;

import java.util.List;

public record CompMatchDto(CompDto comp, int score, List<String> matchedUnits,
                           List<String> missingUnits, List<String> reasons) {}
