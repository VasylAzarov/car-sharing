package dev.vasyl.car.sharing.repository;

import dev.vasyl.car.sharing.model.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Page<Car> findAll(Pageable pageable);

}
