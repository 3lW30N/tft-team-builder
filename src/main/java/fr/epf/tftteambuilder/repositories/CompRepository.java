package fr.epf.tftteambuilder.repositories;

import fr.epf.tftteambuilder.models.Build;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompRepository extends JpaRepository<Build, Long> {
}
