package fr.epf.tftteambuilder.repositories;

import jakarta.persistence.*;

@Entity
@Table(name = "unit_trait", schema = "public")
public class UnitTrait {
    @EmbeddedId
    private UnitTraitId id;

    @MapsId("unitId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @MapsId("traitId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trait_id", nullable = false)
    private Trait trait;

    public UnitTraitId getId() {
        return id;
    }

    public void setId(UnitTraitId id) {
        this.id = id;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Trait getTrait() {
        return trait;
    }

    public void setTrait(Trait trait) {
        this.trait = trait;
    }

}