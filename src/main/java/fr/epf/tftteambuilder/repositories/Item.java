package fr.epf.tftteambuilder.repositories;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Item {
    @Id
    private String id;
}
