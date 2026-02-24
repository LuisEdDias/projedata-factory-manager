package lat.luisdias.factory.manager.model;

import jakarta.persistence.*;
import lat.luisdias.factory.manager.exeption.exceptions.DomainInvariantViolationException;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "product_materials")
public class ProductMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_id", nullable = false, updatable = false)
    private RawMaterial rawMaterial;

    @Column(name = "quantity_required", nullable = false, precision = 14, scale = 4)
    private BigDecimal quantityRequired;

    protected ProductMaterial() {
    }

    public ProductMaterial(RawMaterial rawMaterial, BigDecimal quantityRequired) {
        validateRawMaterial(rawMaterial);
        validateQuantity(quantityRequired);

        this.rawMaterial = rawMaterial;
        this.quantityRequired = quantityRequired;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void updateQuantity(BigDecimal newQuantity) {
        validateQuantity(newQuantity);
        this.quantityRequired = newQuantity;
    }

    private void validateRawMaterial(RawMaterial rawMaterial) {
        if (rawMaterial == null) {
            throw new DomainInvariantViolationException(
                    "exception.domain.product_material.raw_material_invalid"
            );
        }
    }

    private void validateQuantity(BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainInvariantViolationException(
                    "exception.domain.product_material.quantity_invalid",
                    quantity
            );
        }
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public RawMaterial getRawMaterial() {
        return rawMaterial;
    }

    public BigDecimal getQuantityRequired() {
        return quantityRequired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductMaterial that = (ProductMaterial) o;
        return Objects.equals(rawMaterial, that.rawMaterial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawMaterial.getCode());
    }
}
