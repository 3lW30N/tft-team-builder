package fr.epf.tftteambuilder.dto;

import java.util.List;

public record CompDto(
        Integer id,
        String name,
        Double avgPlacement,
        Double winRate,
        Double difficulty,
        String levelling,
        List<BuildDto> builds
) {
}
