package fr.epf.tftteambuilder.models;

import jakarta.persistence.*;

@Entity
@Table(name = "build", schema = "public")
public class Build {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "avg_placement")
    private Double avgPlacement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item1_id")
    private Item item1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item2_id")
    private Item item2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item3_id")
    private Item item3;

    @Column(name = "stars")
    private Integer stars;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getAvgPlacement() {
        return avgPlacement;
    }

    public void setAvgPlacement(Double avgPlacement) {
        this.avgPlacement = avgPlacement;
    }

    public Item getItem1() {
        return item1;
    }

    public void setItem1(Item item1) {
        this.item1 = item1;
    }

    public Item getItem2() {
        return item2;
    }

    public void setItem2(Item item2) {
        this.item2 = item2;
    }

    public Item getItem3() {
        return item3;
    }

    public void setItem3(Item item3) {
        this.item3 = item3;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

}