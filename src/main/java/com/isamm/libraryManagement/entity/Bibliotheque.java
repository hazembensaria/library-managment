package com.isamm.libraryManagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bibliotheque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String code;
    private String adresse;
    private Double latitude;
    private Double longitude;
    private String horaireSemaine;
    private String horaireWeekend;

    @Transient
    private Long parentId;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @ToString.Exclude
    private Bibliotheque parent;

    @OneToMany(mappedBy = "parent")
    @ToString.Exclude
    private List<Bibliotheque> sousBibliotheques = new ArrayList<>();

    @OneToMany(mappedBy = "bibliotheque")
    @JsonIgnore
    @ToString.Exclude
    private List<Exemplaire> exemplaires = new ArrayList<>();
}
