package com.isamm.libraryManagement.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private String adresse;
    private String localisation;
    private String horaires;

    @OneToMany(mappedBy = "bibliotheque", cascade = CascadeType.ALL)
    private List<Exemplaire> exemplaires;
}
