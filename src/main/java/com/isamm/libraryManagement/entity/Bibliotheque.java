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

    @NotBlank(message = "The name is required")
    @Size(min = 2, max = 100, message = "The name must contain between 2 and 100 characters")
    private String nom;

    @NotBlank(message = "The code is required")
    @Size(min = 2, max = 20, message = "The code must contain between 2 and 20 characters")
    @Column(unique = true)
    private String code;

    private String adresse;

    private Double latitude;
    private Double longitude;

    @Pattern(regexp = "^([01]?\\d|2[0-3]):[0-5]\\d-([01]?\\d|2[0-3]):[0-5]\\d$", message = "Invalid format (ex: 08:00-17:00)")
    private String horaireSemaine;

    @Pattern(regexp = "^([01]?\\d|2[0-3]):[0-5]\\d-([01]?\\d|2[0-3]):[0-5]\\d$", message = "Invalid format (ex: 09:00-12:00)")
    private String horaireWeekend;
    @Transient
    private Long parentId;
    // Relation auto-référencée : parent
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Bibliotheque parent;

    // Sous-bibliothèques
    @OneToMany(mappedBy = "parent")
    private List<Bibliotheque> sousBibliotheques = new ArrayList<>();

    @OneToMany(mappedBy = "bibliotheque")
    @JsonIgnore
    private List<Exemplaire> exemplaires = new ArrayList<>();

}
