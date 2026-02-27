package lat.luisdias.factory.manager.model;

import jakarta.persistence.*;
import lat.luisdias.factory.manager.exeption.exceptions.DomainInvariantViolationException;
import lat.luisdias.factory.manager.model.vo.IdentificationCodeVO;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private IdentificationCodeVO code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductMaterial> composition = new HashSet<>();

    protected Product() {
    }

    public Product(String code, String name, BigDecimal price) {
        validateName(name);
        validatePrice(price);

        this.code = new IdentificationCodeVO(code);
        this.name = name;
        this.price = price;
    }


    public Set<ProductMaterial> getComposition() {
        return Set.copyOf(composition);
    }

    public void updateName(String newName) {
        validateName(newName);
        this.name = newName;
    }

    public void updatePrice(BigDecimal newPrice) {
        validatePrice(newPrice);
        this.price = newPrice;
    }

    public void updateMaterialQuantity(String rawMaterialCode, BigDecimal newQuantity) {
        ProductMaterial material = this.composition.stream()
                .filter(m -> m.getRawMaterial().getCode().equals(rawMaterialCode))
                .findFirst()
                .orElseThrow(() -> new DomainInvariantViolationException(
                        "exception.domain.product.product_material_not_found"
                ));
        material.updateQuantity(newQuantity);
    }

    public void addMaterial(ProductMaterial material) {
        if (material == null) {
            throw new DomainInvariantViolationException(
                    "exception.domain.product.product_material_invalid"
            );
        }

        material.setProduct(this);
        boolean added = this.composition.add(material);

        if (!added) {
            throw new DomainInvariantViolationException(
                    "exception.domain.product.duplicate_raw_material",
                    material.getRawMaterial().getCode()
            );
        }
    }

    public void removeMaterial(String rawMaterialCode) {
        if (rawMaterialCode == null || rawMaterialCode.isBlank()) {
            throw new DomainInvariantViolationException(
                    "exception.domain.product.product_material_invalid"
            );
        }

        ProductMaterial material = this.composition.stream()
                .filter(m -> m.getRawMaterial().getCode().equals(rawMaterialCode))
                .findFirst()
                .orElseThrow(() -> new DomainInvariantViolationException(
                        "exception.domain.product.product_material_not_found",
                        rawMaterialCode
                ));

        this.composition.remove(material);
        material.setProduct(null);
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainInvariantViolationException(
                    "exception.domain.product.name_invalid",
                    name
            );
        }
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainInvariantViolationException(
                    "exception.domain.product.price_invalid",
                    price
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

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return code.equals(product.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
