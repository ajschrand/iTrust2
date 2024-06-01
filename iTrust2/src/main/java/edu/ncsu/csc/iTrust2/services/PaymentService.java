package edu.ncsu.csc.iTrust2.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.Payment;
import edu.ncsu.csc.iTrust2.repositories.PaymentRepository;

/**
 * Service class for interacting with Payment model, performing CRUD tasks with
 * database.
 *
 * @author yli246
 *
 */
@Component
@Transactional
public class PaymentService extends Service<Payment, Long> {

    /** Repository for CRUD operations */
    @Autowired
    private PaymentRepository repository;

    @Override
    protected JpaRepository<Payment, Long> getRepository () {
        return repository;
    }
}
