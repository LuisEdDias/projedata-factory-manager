package lat.luisdias.factory.manager.model;

import jakarta.persistence.*;
import lat.luisdias.factory.manager.exeption.exceptions.DomainInvariantViolationException;
import lat.luisdias.factory.manager.model.vo.IdentificationCodeVO;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "raw_materials")
public class RawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private IdentificationCodeVO code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 14, scale = 4)
    private BigDecimal stockQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MeasurementUnit unit;

    protected RawMaterial() {
    }

    public RawMaterial(String code, String name, BigDecimal initialStock, MeasurementUnit unit) {
        validateName(name);
        validateStock(initialStock);
        validateUnit(unit);

        this.code = new IdentificationCodeVO(code);
        this.name = name;
        this.stockQuantity = initialStock;
        this.unit = unit;
    }

    public void updateName(String newName) {
        validateName(newName);
        this.name = newName;
    }

    public void addStock(BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainInvariantViolationException(
                    "exception.domain.raw_material.add_stock_invalid",
                    quantity
            );
        }
        this.stockQuantity = this.stockQuantity.add(quantity);
    }

    public void consumeStock(BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainInvariantViolationException(
                    "exception.domain.raw_material.consume_stock_invalid",
                    quantity
            );
        }
        if (this.stockQuantity.compareTo(quantity) < 0) {
            throw new DomainInvariantViolationException(
                    "exception.domain.raw_material.insufficient_stock",
                    quantity
            );
        }
        this.stockQuantity = this.stockQuantity.subtract(quantity);
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainInvariantViolationException(
                    "exception.domain.raw_material.name_invalid",
                    name
            );
        }
    }

    private void validateStock(BigDecimal stock) {
        if (stock == null || stock.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainInvariantViolationException(
                    "exception.domain.raw_material.stock_invalid",
                    stock
            );
        }
    }

    private void validateUnit(MeasurementUnit unit) {
        if (unit == null) {
            throw new DomainInvariantViolationException(
                    "exception.domain.raw_material.unit_invalid"
            );
        }
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code.getValue();
    }

    public String getName() {
        return name;
    }

    public BigDecimal getStockQuantity() {
        return stockQuantity;
    }

    public MeasurementUnit getUnit() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawMaterial that = (RawMaterial) o;
        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
