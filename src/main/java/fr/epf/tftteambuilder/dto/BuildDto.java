package fr.epf.tftteambuilder.dto;

import java.util.List;

public record BuildDto(
        String unitId,
        Integer stars,
        Double avgPlacement,
        List<ItemDto> items
) {
}
