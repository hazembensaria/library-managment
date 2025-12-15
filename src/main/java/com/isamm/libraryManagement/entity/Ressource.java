package com.isamm.libraryManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ressource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String auteur;
    private String description;
    private String categorie;
    // chemin vers l'img du couerture
    private String coverPath;

    // chemin vers le fichier de preview (PDF ou images)
    private String previewPath;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_ressource")
    private TypeRessource typeRessource;

    @OneToMany(mappedBy = "ressource", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Exemplaire> exemplaires;
}
