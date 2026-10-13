package fr.epf.tftteambuilder.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "unit", schema = "public")
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image_link")
    private String imageLink;

    @Column(name = "tier")
    private Integer tier;

    @ManyToMany
    @JoinTable(
            name = "unit_trait",
            joinColumns = @JoinColumn(name = "unit_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "trait_id", referencedColumnName = "id")
    )
    private List<Trait> traits;

    public String getId() {
        return id;
    }

    public Integer getCost() {
        return tier;
    }
    public void setTier(Integer tier) {}

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

}