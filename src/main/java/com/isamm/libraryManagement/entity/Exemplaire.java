package com.isamm.libraryManagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exemplaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le code barre est obligatoire")
    private String codeBarre;

    @NotNull(message = "La disponibilité doit être indiquée")
    private Boolean disponible;

    @NotBlank(message = "L’état de l’exemplaire est obligatoire")
    private String etat;

    // @NotNull(message = "La date d’acquisition est obligatoire")
    @PastOrPresent(message = "La date d’acquisition ne peut pas être dans le futur")

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateAcquisition;

    @ManyToOne
    @JoinColumn(name = "ressource_id")
    @NotNull(message = "La ressource associée est obligatoire")
    private Ressource ressource;

    @ManyToOne
    @JoinColumn(name = "bibliotheque_id")
    @NotNull(message = "La bibliothèque associée est obligatoire")
    private Bibliotheque bibliotheque;
}