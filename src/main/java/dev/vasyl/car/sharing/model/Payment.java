package dev.vasyl.car.sharing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.net.URL;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@Entity
@SQLDelete(sql = "UPDATE payments SET is_deleted = true WHERE id=?")
@SQLRestriction("is_deleted=false")
@Table(name = "payments")
public class Payment {
    @Id
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType type;

    @OneToOne
    @MapsId
    private Rental rental;

    @Column(nullable = false,
            columnDefinition = "longtext")
    private URL sessionUrl;

    @Column(nullable = false,
            columnDefinition = "longtext",
            unique = true)
    private String sessionId;

    @Column(nullable = false)
    private BigDecimal amountToPay;

    @Column(nullable = false)
    private boolean isDeleted = false;

    public enum PaymentStatus {
        PENDING,
        PAID
    }

    public enum PaymentType {
        PAYMENT,
        FINE
    }
}
