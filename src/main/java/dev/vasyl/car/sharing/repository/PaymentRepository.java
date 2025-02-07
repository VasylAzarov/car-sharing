package dev.vasyl.car.sharing.repository;

import dev.vasyl.car.sharing.model.Payment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findAllByRental_UserId(Long userId, Pageable pageable);

    Optional<Payment> findByRentalId(Long id);

    boolean existsByRentalId(Long id);
}
