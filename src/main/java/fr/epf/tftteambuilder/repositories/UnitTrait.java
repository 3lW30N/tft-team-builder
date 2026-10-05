package fr.epf.tftteambuilder.repositories;

import jakarta.persistence.*;

@Entity
@Table(name = "unit_trait", schema = "public", uniqueConstraints = {@UniqueConstraint(name = "pk_unit_trait",
        columnNames = {
                "unit_id",
                "trait_id"})})
public class UnitTrait {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trait_id", nullable = false)
    private Trait trait;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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