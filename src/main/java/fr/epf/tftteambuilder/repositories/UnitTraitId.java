package fr.epf.tftteambuilder.repositories;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UnitTraitId implements Serializable {
    private static final long serialVersionUID = 1938138981013442288L;
    @Column(name = "unit_id", nullable = false)
    private Integer unitId;

    @Column(name = "trait_id", nullable = false)
    private String traitId;

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public String getTraitId() {
        return traitId;
    }

    public void setTraitId(String traitId) {
        this.traitId = traitId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnitTraitId entity = (UnitTraitId) o;
        return Objects.equals(this.unitId, entity.unitId) &&
                Objects.equals(this.traitId, entity.traitId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unitId, traitId);
    }
}