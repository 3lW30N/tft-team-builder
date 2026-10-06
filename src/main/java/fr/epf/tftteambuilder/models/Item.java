package fr.epf.tftteambuilder.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "item", schema = "public", uniqueConstraints = {@UniqueConstraint(name = "item_name_key",
        columnNames = {"name"})})
public class Item {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image_link")
    private String imageLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "component1_id")
    private Item component1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "component2_id")
    private Item component2;

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

    public Item getComponent1() {
        return component1;
    }

    public void setComponent1(Item component1) {
        this.component1 = component1;
    }

    public Item getComponent2() {
        return component2;
    }

    public void setComponent2(Item component2) {
        this.component2 = component2;
    }

}