package com.isamm.libraryManagement.entity;

import com.isamm.libraryManagement.entity.Exemplaire;
import com.isamm.libraryManagement.entity.Ressource;
import com.isamm.libraryManagement.entity.TypeRessource;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.time.LocalDate;

public class RessourceSpecifications {

    public static Specification<Ressource> advanced(
            String q,
            String categorie,
            TypeRessource type,
            Boolean ignoredAvailability,
            LocalDate dateFrom,
            LocalDate dateTo) {

        return (root, query, cb) -> {
            Predicate p = cb.conjunction();
            query.distinct(true);

            if (q != null && !q.isBlank()) {
                String like = "%" + q.toLowerCase().trim() + "%";
                p = cb.and(p, cb.or(
                        cb.like(cb.lower(root.get("titre")), like),
                        cb.like(cb.lower(root.get("auteur")), like),
                        cb.like(cb.lower(root.get("description")), like),
                        cb.like(cb.lower(root.get("categorie")), like)));
            }

            if (categorie != null && !categorie.isBlank()) {
                String likeCat = "%" + categorie.toLowerCase().trim() + "%";
                p = cb.and(p, cb.like(cb.lower(root.get("categorie")), likeCat));
            }

            if (type != null) {
                p = cb.and(p, cb.equal(root.get("typeRessource"), type));
            }

            if (dateFrom != null) {
                p = cb.and(p, cb.greaterThanOrEqualTo(root.get("publishDate"), dateFrom));
            }
            if (dateTo != null) {
                p = cb.and(p, cb.lessThanOrEqualTo(root.get("publishDate"), dateTo));
            }

            return p;
        };
    }

}