package fr.epf.tftteambuilder.repositories;

import fr.epf.tftteambuilder.models.Trait;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TraitRepository extends JpaRepository<Trait, String> {
}
