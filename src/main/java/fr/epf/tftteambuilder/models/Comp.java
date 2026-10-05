package fr.epf.tftteambuilder.models;

import jakarta.persistence.*;

@Entity
@Table(name = "comp", schema = "public")
public class Comp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "avg_placement")
    private Double avgPlacement;

    @Column(name = "win_rate")
    private Double winRate;

    @Column(name = "difficulty")
    private Double difficulty;

    @Column(name = "levelling")
    private String levelling;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "build_id")
    private Build build;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAvgPlacement() {
        return avgPlacement;
    }

    public void setAvgPlacement(Double avgPlacement) {
        this.avgPlacement = avgPlacement;
    }

    public Double getWinRate() {
        return winRate;
    }

    public void setWinRate(Double winRate) {
        this.winRate = winRate;
    }

    public Double getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Double difficulty) {
        this.difficulty = difficulty;
    }

    public String getLevelling() {
        return levelling;
    }

    public void setLevelling(String levelling) {
        this.levelling = levelling;
    }

    public Build getBuild() {
        return build;
    }

    public void setBuild(Build build) {
        this.build = build;
    }

}