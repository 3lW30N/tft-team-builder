package fr.epf.tftteambuilder.repositories;

import fr.epf.tftteambuilder.models.Build;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuildRepository extends JpaRepository<Build, Long> {
    java.util.Optional<Build> findByUnitId(Long unitId);
}
