package dev.vasyl.car.sharing.repository;

import dev.vasyl.car.sharing.model.Rental;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    Page<Rental> findAllByUserIdAndActualReturnDateIsNull(Pageable pageable, Long userId);

    Page<Rental> findAllByUserIdAndActualReturnDateIsNotNull(Pageable pageable, Long userId);

    List<Rental> findByReturnDateBeforeAndActualReturnDateIsNull(LocalDate tomorrow);

}
