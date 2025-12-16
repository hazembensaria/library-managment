package com.isamm.libraryManagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ressource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "The title is mandatory")
    @Size(min = 2, max = 100, message = "The title must be between 2 and 100 characters long.")
    private String titre;

    @NotBlank(message = "The author is required")
    @Size(min = 2, max = 50, message = "The author's name is too short.")
    private String auteur;

    @Size(max = 255, message = "The description must not exceed 255 characters")
    private String description;

    private String categorie;
    private String coverPath;
    private String previewPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_ressource")
    private TypeRessource typeRessource;

    @OneToMany(mappedBy = "ressource", cascade = CascadeType.ALL)
    private List<Exemplaire> exemplaires;
}
