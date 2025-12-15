package com.isamm.libraryManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat; // <-- à ajouter
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

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

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateAcquisition;

    @ManyToOne
    @JoinColumn(name = "ressource_id")
    private Ressource ressource;

    @ManyToOne
    @JoinColumn(name = "bibliotheque_id")
    private Bibliotheque bibliotheque;
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 3d1183696545fc79ba2791077b427b2268eb245a

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
<<<<<<< HEAD
=======
}
>>>>>>> 3545eab516ecbe0e6d3b0f64602a1b3e1e5eb51d
=======
}
>>>>>>> 3d1183696545fc79ba2791077b427b2268eb245a
