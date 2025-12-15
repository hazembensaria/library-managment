package com.isamm.libraryManagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exemplaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codeBarre;
    private Boolean disponible;
    private String etat;
    private Date dateAcquisition;

    @ManyToOne
    @JoinColumn(name = "ressource_id")
    private Ressource ressource;

    @ManyToOne
    @JoinColumn(name = "bibliotheque_id")
    private Bibliotheque bibliotheque;

    @PrePersist
    public void prePersist() {
        // Si "disponible" n'est pas renseigné, on le considère disponible par défaut
        if (disponible == null) {
            disponible = true;
        }
        // Si "etat" n'est pas renseigné, on met un état par défaut
        if (etat == null || etat.isBlank()) {
            etat = "bon";
        }
    }
}
