package lat.luisdias.factory.manager.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductUpdateRequest(
        @Pattern(regexp = ".*\\S.*", message = "{validation.product.name.not_blank}")
        @Size(max = 255, message = "{validation.product.name.size}")
        String name,

        @DecimalMin(value = "0.0", message = "{validation.product.price.min}")
        BigDecimal price
) {
}
