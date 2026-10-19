package fr.epf.tftteambuilder.dto;

import fr.epf.tftteambuilder.models.Build;
import fr.epf.tftteambuilder.models.Comp;
import fr.epf.tftteambuilder.models.Item;

import java.util.ArrayList;
import java.util.List;

public class Mapper {

    public ItemDto itemMapper(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getImageLink()
        );
    }

    public BuildDto buildMapper(Build build) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        if (build.getItem1() != null) { itemDtoList.add(itemMapper(build.getItem1())); }
        if (build.getItem2() != null) { itemDtoList.add(itemMapper(build.getItem2())); }
        if (build.getItem3() != null) { itemDtoList.add(itemMapper(build.getItem3())); }
        return new BuildDto(
                build.getUnit().getId(),
                build.getStars(),
                build.getAvgPlacement(),
                itemDtoList
        );
    }

    public CompDto compMapper(Comp comp) {
        List<BuildDto> buildDtoList = new ArrayList<>();
        comp.getBuilds().forEach(build -> buildDtoList.add(buildMapper(build)));
        return new CompDto(
                comp.getId(),
                comp.getName(),
                comp.getAvgPlacement(),
                comp.getWinRate(),
                comp.getDifficulty(),
                comp.getLevelling(),
                buildDtoList
        );
    }
}
