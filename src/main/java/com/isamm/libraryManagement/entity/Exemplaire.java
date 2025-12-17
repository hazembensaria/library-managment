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

    @NotBlank(message = "The barcode is mandatory")
    private String codeBarre;

    // @NotNull(message = "Availability must be indicated")
    private Boolean disponible = true;
    @NotBlank(message = "The condition of the copy is mandatory")
    private String etat;

    // @NotNull(message = "La date d’acquisition est obligatoire")
    @PastOrPresent(message = "The acquisition date cannot be in the future")

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateAcquisition;

    @ManyToOne
    @JoinColumn(name = "ressource_id")
    // @NotNull(message = "The associated resource is mandatory.")
    private Ressource ressource;

    @ManyToOne
    @JoinColumn(name = "bibliotheque_id")
    // @NotNull(message = "The associated library is mandatory.")
    private Bibliotheque bibliotheque;
}