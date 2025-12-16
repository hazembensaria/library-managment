package com.isamm.libraryManagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @ManyToOne
    @JoinColumn(name = "exemplaire_id")
    @NotNull
    private Exemplaire exemplaire;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LoanStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date borrowedAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dueAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date returnedAt;
}


