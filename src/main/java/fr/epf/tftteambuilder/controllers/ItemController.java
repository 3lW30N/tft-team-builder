package fr.epf.tftteambuilder.controllers;

import fr.epf.tftteambuilder.services.ItemService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RequestMapping("item")
@RestController
public class ItemController {
    private final ItemService itemService;


}
