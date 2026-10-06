package fr.epf.tftteambuilder.repositories;

import fr.epf.tftteambuilder.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, String> {
    @Query("SELECT i FROM Item i WHERE i.component1.id = :id OR i.component2.id = :id")
    List<Item> findByComponentId(@Param("id") String id);

    @Query("SELECT i FROM Item i WHERE i.component1.id = :id AND i.component2.id = :id")
    Item findCraft(@Param("c1_id") String c1_id, @Param("c2_id") String c2_id);
}
