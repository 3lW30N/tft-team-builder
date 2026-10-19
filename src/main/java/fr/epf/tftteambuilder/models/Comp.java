package fr.epf.tftteambuilder.models;

import jakarta.persistence.*;

import java.util.List;

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

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "comp_id")
    private List<Build> builds;

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

    public List<Build> getBuilds() {
        return builds;
    }

    public void setBuilds(List<Build> builds) {
        this.builds = builds;
    }

}