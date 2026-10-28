package fr.epf.tftteambuilder.repositories;

import fr.epf.tftteambuilder.models.Trait;
import fr.epf.tftteambuilder.models.Unit;
import fr.epf.tftteambuilder.models.UnitTrait;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UnitTraitRepository extends JpaRepository<UnitTrait, Integer> {
    @Query("select ut.trait from UnitTrait ut where ut.unit.id = :unitId")
    List<Trait> findTraitsByUnitId(@Param("unitId") String unitId);

    @Query("select ut.unit from UnitTrait ut where ut.trait.id = :traitId")
    List<Unit> findUnitsByTraitId(@Param("traitId") String traitId);
}
