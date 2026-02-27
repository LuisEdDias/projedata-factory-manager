package lat.luisdias.factory.manager.service;

import lat.luisdias.factory.manager.dto.product.ProductCreateRequest;
import lat.luisdias.factory.manager.dto.product.ProductResponse;
import lat.luisdias.factory.manager.dto.product.ProductUpdateRequest;
import lat.luisdias.factory.manager.dto.productmaterial.ProductMaterialRequest;
import lat.luisdias.factory.manager.exeption.exceptions.DomainInvariantViolationException;
import lat.luisdias.factory.manager.exeption.exceptions.NotFoundException;
import lat.luisdias.factory.manager.model.Product;
import lat.luisdias.factory.manager.model.ProductMaterial;
import lat.luisdias.factory.manager.model.RawMaterial;
import lat.luisdias.factory.manager.model.vo.IdentificationCodeVO;
import lat.luisdias.factory.manager.repository.ProductRepository;
import lat.luisdias.factory.manager.repository.RawMaterialRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;

    public ProductService(
            ProductRepository productRepository,
            RawMaterialRepository rawMaterialRepository
    ) {
        this.productRepository = productRepository;
        this.rawMaterialRepository = rawMaterialRepository;
    }

    public ProductResponse create(ProductCreateRequest createRequest) {
        IdentificationCodeVO normalizedProductCode = new IdentificationCodeVO(createRequest.code());

        if (productRepository.existsByCode(normalizedProductCode)) {
            throw new DomainInvariantViolationException(
                    "exception.domain.product.code_already_exists",
                    normalizedProductCode.getValue()
            );
        }

        try {
            Product product = new Product(
                    normalizedProductCode.getValue(),
                    createRequest.name(),
                    createRequest.price()
            );

            if (createRequest.materials() != null && !createRequest.materials().isEmpty()) {
                for (ProductMaterialRequest materialRequest : createRequest.materials()) {
                    IdentificationCodeVO materialCode = new IdentificationCodeVO(materialRequest.rawMaterialCode());
                    RawMaterial rawMaterial = materialFindOrThrow(materialCode);

                    ProductMaterial productMaterial = new ProductMaterial(
                            rawMaterial,
                            materialRequest.quantityRequired()
                    );

                    product.addMaterial(productMaterial);
                }
            }

            return new ProductResponse(productRepository.save(product));

        } catch (DataIntegrityViolationException ex) {
            throw new DomainInvariantViolationException(
                    "exception.domain.product.code_already_exists",
                    ex,
                    normalizedProductCode.getValue()
            );
        }
    }

    @Transactional(readOnly = true)
    public ProductResponse getByCode(String code) {
        Product product = productFindOrThrow(code);
        return new ProductResponse(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> list(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);

        if (!productPage.isEmpty()) {
            productRepository.fetchCompositionsForProducts(productPage.getContent());
        }

        return productPage.map(ProductResponse::new);
    }

    public ProductResponse updateProduct(String code, ProductUpdateRequest updateRequest) {
        Product product = productFindOrThrow(code);

        if (updateRequest.name() != null) {
            product.updateName(updateRequest.name());
        }

        if (updateRequest.price() != null) {
            product.updatePrice(updateRequest.price());
        }

        return new ProductResponse(product);
    }

    public ProductResponse addProductMaterial(String productCode, ProductMaterialRequest materialRequest) {
        Product product = productFindOrThrow(productCode);
        IdentificationCodeVO materialCode = new IdentificationCodeVO(materialRequest.rawMaterialCode());

        RawMaterial rawMaterial = materialFindOrThrow(materialCode);

        ProductMaterial productMaterial = new ProductMaterial(rawMaterial, materialRequest.quantityRequired());

        product.addMaterial(productMaterial);

        return new ProductResponse(product);
    }

    public ProductResponse updateProductMaterial(String productCode, ProductMaterialRequest materialRequest) {
        Product product = productFindOrThrow(productCode);

        IdentificationCodeVO materialCode = new IdentificationCodeVO(materialRequest.rawMaterialCode());

        product.updateMaterialQuantity(materialCode.getValue(), materialRequest.quantityRequired());

        return new ProductResponse(product);
    }

    public void removeProductMaterial(String productCode, String materialCode) {
        Product product = productFindOrThrow(productCode);
        IdentificationCodeVO normalizedMaterialCode = new IdentificationCodeVO(materialCode);

        product.removeMaterial(normalizedMaterialCode.getValue());
    }

    public void deleteProductByCode(String code) {
        Product product = productFindOrThrow(code);
        productRepository.delete(product);
    }

    private Product productFindOrThrow(String code) {
        IdentificationCodeVO normalizedCode = new IdentificationCodeVO(code);

        return productRepository.findByCodeWithComposition(normalizedCode)
                .orElseThrow(() -> new NotFoundException(
                        "exception.domain.product.not_found",
                        normalizedCode.getValue()
                ));
    }

    private RawMaterial materialFindOrThrow(IdentificationCodeVO normalizedCode) {
        return rawMaterialRepository.findByCode(normalizedCode)
                .orElseThrow(() -> new NotFoundException(
                        "exception.domain.raw_material.not_found",
                        normalizedCode.getValue()
                ));
    }
}
