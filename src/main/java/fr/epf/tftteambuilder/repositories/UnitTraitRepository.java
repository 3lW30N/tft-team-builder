package fr.epf.tftteambuilder.repositories;

import fr.epf.tftteambuilder.models.Trait;
import fr.epf.tftteambuilder.models.Unit;
import fr.epf.tftteambuilder.models.UnitTrait;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnitTraitRepository extends JpaRepository<UnitTrait, Long> {
    List<Trait> findTraitsByUnitId(String unitId);
    List<Unit> findUnitsByTraitId(String traitId);
}
