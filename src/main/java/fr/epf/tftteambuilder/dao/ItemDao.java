package fr.epf.tftteambuilder.dao;

import fr.epf.tftteambuilder.repositories.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ItemDao extends JpaRepository<Item, String> {
}