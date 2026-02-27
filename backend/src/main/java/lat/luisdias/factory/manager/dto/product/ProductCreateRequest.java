package lat.luisdias.factory.manager.dto.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lat.luisdias.factory.manager.dto.productmaterial.ProductMaterialRequest;

import java.math.BigDecimal;
import java.util.List;

public record ProductCreateRequest(
        @NotBlank(message = "{validation.product.code.required}")
        @Size(max = 50, message = "{validation.product.code.size}")
        String code,

        @NotBlank(message = "{validation.product.name.required}")
        @Size(max = 255, message = "{validation.product.name.size}")
        String name,

        @NotNull(message = "{validation.product.price.required}")
        @DecimalMin(value = "0.0", message = "{validation.product.price.min}")
        BigDecimal price,

        @Valid
        List<ProductMaterialRequest> materials
) {
}
