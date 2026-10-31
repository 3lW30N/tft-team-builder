package fr.epf.tftteambuilder.repositories;

import fr.epf.tftteambuilder.models.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitRepository extends JpaRepository<Unit, String> {
}
