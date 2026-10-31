package fr.epf.tftteambuilder.models;

import jakarta.persistence.*;

@Entity
@Table(name = "trait", schema = "public", uniqueConstraints = {@UniqueConstraint(name = "trait_name_key",
        columnNames = {"name"})})
public class Trait {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image_link")
    private String imageLink;

    public String getId() {
        return id;
    }

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