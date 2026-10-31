package fr.epf.tftteambuilder.repositories;

import fr.epf.tftteambuilder.models.Build;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuildRepository extends JpaRepository<Build, Integer> {
    List<Build> getByCompId(Integer id);
}
