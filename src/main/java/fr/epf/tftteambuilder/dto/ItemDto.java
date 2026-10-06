package fr.epf.tftteambuilder.dto;

public record ItemDto(
        String id,
        String name,
        String imageLink,
        String component1Id,
        String component2Id
) {}