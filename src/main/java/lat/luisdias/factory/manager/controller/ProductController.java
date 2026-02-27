package lat.luisdias.factory.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lat.luisdias.factory.manager.dto.product.ProductCreateRequest;
import lat.luisdias.factory.manager.dto.product.ProductResponse;
import lat.luisdias.factory.manager.dto.product.ProductUpdateRequest;
import lat.luisdias.factory.manager.dto.productmaterial.ProductMaterialRequest;
import lat.luisdias.factory.manager.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Endpoints for managing products and their recipes (compositions).")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Register a new product", description = "Create a product along with its recipe (list of necessary raw materials).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Validation error in the submitted data", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Raw material listed in the recipe not found", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Business rule violated (e.g., duplicate code, duplicate input in the recipe)", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ProductResponse> create(@RequestBody @Valid ProductCreateRequest req) {
        ProductResponse response = service.create(req);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{code}")
                .buildAndExpand(response.code())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{code}")
    @Operation(summary = "Search for product by code", description = "Returns the product details and its complete recipe.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ProductResponse get(@PathVariable String code) {
        return service.getByCode(code);
    }

    @GetMapping
    @Operation(summary = "List products", description = "Returns a page of products with their respective recipes.")
    @ApiResponse(responseCode = "200", description = "Success")
    public Page<ProductResponse> list(
            @PageableDefault(size = 20, sort = "name") Pageable pageable
    ) {
        return service.list(pageable);
    }

    @PutMapping("/{code}")
    @Operation(
            summary = "Update basic product data",
            description = "Allows you to update the product name or price independently."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Validation error in the submitted data", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ProductResponse updateProduct(
            @PathVariable String code,
            @RequestBody @Valid ProductUpdateRequest req
    ) {
        return service.updateProduct(code, req);
    }

    @PostMapping("/{code}/materials")
    @Operation(
            summary = "Add an ingredient to the recipe",
            description = "It includes a new raw material in the composition of the existing product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Validation error in the submitted data", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Product or raw material not found", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Ingredient is already in the recipe for this product", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ProductResponse addProductMaterial(
            @PathVariable String code,
            @RequestBody @Valid ProductMaterialRequest req
    ) {
        return service.addProductMaterial(code, req);
    }

    @PutMapping("/{code}/materials")
    @Operation(
            summary = "Updates the quantity of an input",
            description = "It changes the required quantity of a raw material that is already part of the recipe."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Product not found or ingredient not included in the recipe", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ProductResponse updateProductMaterial(
            @PathVariable String code,
            @RequestBody @Valid ProductMaterialRequest req
    ) {
        return service.updateProductMaterial(code, req);
    }

    @DeleteMapping("/{code}/materials/{materialCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Remove an ingredient from the recipe",
            description = "It removes a raw material from the product's composition."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Product not found or ingredient not included in the recipe", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public void removeProductMaterial(
            @PathVariable String code,
            @PathVariable String materialCode
    ) {
        service.removeProductMaterial(code, materialCode);
    }

    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a product", description = "Remove the product and unlink its recipe from the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(
                    mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public void delete(@PathVariable String code) {
        service.deleteProductByCode(code);
    }
}
