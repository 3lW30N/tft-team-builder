package fr.epf.tftteambuilder.repositories;

import fr.epf.tftteambuilder.models.Comp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompRepository extends JpaRepository<Comp, Integer> {
}