package com.isamm.libraryManagement.repository;

import com.isamm.libraryManagement.entity.Exemplaire;
import com.isamm.libraryManagement.entity.Loan;
import com.isamm.libraryManagement.entity.LoanStatus;
import com.isamm.libraryManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Collection;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    long countByUserAndStatusIn(User user, Collection<LoanStatus> statuses);

    boolean existsByExemplaireAndStatusIn(Exemplaire exemplaire, Collection<LoanStatus> statuses);

    List<Loan> findByUser(User user);

    List<Loan> findByStatus(LoanStatus status);
    
    
    @Query("select r.categorie, count(l) " +
    	       "from Loan l join l.exemplaire e join e.ressource r " +
    	       "where l.status = :status " +
    	       "group by r.categorie")
    	List<Object[]> countLoansByCategorie(@Param("status") LoanStatus status);

    	@Query("select b.nom, count(l) " +
    	       "from Loan l join l.exemplaire e join e.bibliotheque b " +
    	       "where l.status = :status " +
    	       "group by b.nom")
    	List<Object[]> countLoansByBibliotheque(@Param("status") LoanStatus status);

}


