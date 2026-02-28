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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @InjectMocks
    private ProductService productService;

    private static BigDecimal bigDec(String value) {
        return new BigDecimal(value);
    }

    @Test
    void shouldThrowWhenCodeAlreadyExists() {
        // Arrange
        ProductCreateRequest request = new ProductCreateRequest(
                "prd-1", "Product 1", bigDec("10.00"),
                List.of()
        );

        when(productRepository.existsByCode(argThat(vo -> vo.getValue().equals("PRD-1"))))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(DomainInvariantViolationException.class)
                .hasMessageContaining("exception.domain.product.code_already_exists");

        verify(productRepository, never()).save(any());
        verifyNoInteractions(rawMaterialRepository);
    }

    @Test
    void shouldCreateProductWithoutMaterialsAndSave() {
        // Arrange
        ProductCreateRequest request = new ProductCreateRequest(
                "prd-1", "Product 1", bigDec("10.00"),
                null
        );

        when(productRepository.existsByCode(any())).thenReturn(false);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        when(productRepository.save(productCaptor.capture()))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        ProductResponse response = productService.create(request);

        // Assert
        Product saved = productCaptor.getValue();
        assertThat(saved.getCode()).isEqualTo("PRD-1");
        assertThat(saved.getName()).isEqualTo("Product 1");
        assertThat(saved.getPrice()).isEqualByComparingTo("10.00");

        assertThat(response).isNotNull();
        verify(productRepository).save(any(Product.class));
        verifyNoInteractions(rawMaterialRepository);
    }

    @Test
    void shouldCreateProductWithMaterials() {
        // Arrange
        ProductMaterialRequest m1 = new ProductMaterialRequest("rm-1", bigDec("2.5000"));
        ProductMaterialRequest m2 = new ProductMaterialRequest("rm-2", bigDec("1.0000"));

        ProductCreateRequest request = new ProductCreateRequest(
                "prd-1", "Product 1", bigDec("10.00"),
                List.of(m1, m2)
        );

        when(productRepository.existsByCode(any())).thenReturn(false);

        RawMaterial rm1 = mock(RawMaterial.class);
        when(rm1.getCode()).thenReturn("RM-1");
        when(rm1.getUnitCost()).thenReturn(BigDecimal.ONE);
        RawMaterial rm2 = mock(RawMaterial.class);
        when(rm2.getCode()).thenReturn("RM-2");
        when(rm2.getUnitCost()).thenReturn(BigDecimal.TEN);

        when(rawMaterialRepository.findByCode(any()))
                .thenAnswer(invocation -> {
                    IdentificationCodeVO idCode = invocation.getArgument(0);
                    return switch (idCode.getValue()) {
                        case "RM-1" -> Optional.of(rm1);
                        case "RM-2" -> Optional.of(rm2);
                        default -> Optional.empty();
                    };
                });

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        when(productRepository.save(productCaptor.capture()))
                .thenAnswer(inv -> inv.getArgument(0));

        ProductResponse response = productService.create(request);

        Product saved = productCaptor.getValue();
        assertThat(saved.getComposition()).hasSize(2);

        ArgumentCaptor<IdentificationCodeVO> captor = ArgumentCaptor.forClass(IdentificationCodeVO.class);
        verify(rawMaterialRepository, times(2)).findByCode(captor.capture());

        List<String> lookedUp = captor.getAllValues().stream()
                .map(IdentificationCodeVO::getValue)
                .toList();

        assertThat(lookedUp).containsExactly("RM-1", "RM-2");
        assertThat(response).isNotNull();
    }

    @Test
    void shouldWrapDataIntegrityViolationAsDomainInvariantViolation() {
        // Arrange
        ProductCreateRequest request = new ProductCreateRequest(
                "prd-1", "Product 1", bigDec("10.00"),
                List.of()
        );

        when(productRepository.existsByCode(any())).thenReturn(false);
        when(productRepository.save(any(Product.class)))
                .thenThrow(new DataIntegrityViolationException("unique"));

        // Act & Assert
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(DomainInvariantViolationException.class);

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldReturnProductResponseWhenFound() {
        // Arrange
        Product product = new Product("PRD-1", "Product 1", bigDec("10.00"));
        when(productRepository.findByCodeWithComposition(argThat(vo -> vo.getValue().equals("PRD-1"))))
                .thenReturn(Optional.of(product));

        // Act
        ProductResponse response = productService.getByCode("prd-1");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo("PRD-1");
    }

    @Test
    void shouldThrowNotFoundWhenMissing() {
        when(productRepository.findByCodeWithComposition(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getByCode("prd-404"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldCallFetchCompositionsWhenPageNotEmpty() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("code"));
        Product p1 = new Product("PRD-1", "P1", bigDec("10.00"));
        Product p2 = new Product("PRD-2", "P2", bigDec("20.00"));

        Page<Product> page = new PageImpl<>(List.of(p1, p2), pageable, 2);
        when(productRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<ProductResponse> result = productService.list(pageable);

        // Assert
        verify(productRepository).fetchCompositionsForProducts(argThat(list -> list.size() == 2));
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void shouldNotCallFetchCompositionsWhenPageEmpty() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> empty = Page.empty(pageable);
        when(productRepository.findAll(pageable)).thenReturn(empty);

        // Act
        Page<ProductResponse> result = productService.list(pageable);

        // Assert
        verify(productRepository, never()).fetchCompositionsForProducts(anyList());
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void shouldUpdateNameAndPriceWhenProvided() {
        // Arrange
        Product product = spy(new Product("PRD-1", "Old", bigDec("10.00")));
        when(productRepository.findByCodeWithComposition(any()))
                .thenReturn(Optional.of(product));

        ProductUpdateRequest request = new ProductUpdateRequest("New", bigDec("99.90"));

        // Act
        ProductResponse response = productService.updateProduct("prd-1", request);

        // Assert
        verify(product).updateName("New");
        verify(product).updatePrice(bigDec("99.90"));
        assertThat(response.code()).isEqualTo("PRD-1");
    }

    @Test
    void shouldAddMaterialAndReturnResponse() {
        // Arrange
        Product product = spy(new Product("PRD-1", "P1", bigDec("10.00")));
        when(productRepository.findByCodeWithComposition(any()))
                .thenReturn(Optional.of(product));

        RawMaterial rawMaterial = mock(RawMaterial.class);
        when(rawMaterial.getCode()).thenReturn("RM-1");
        when(rawMaterial.getUnitCost()).thenReturn(BigDecimal.TEN);
        when(rawMaterialRepository.findByCode(argThat(idCode -> idCode.getValue().equals("RM-1"))))
                .thenReturn(Optional.of(rawMaterial));

        ProductMaterialRequest request = new ProductMaterialRequest("rm-1", bigDec("2.0000"));

        // Act
        ProductResponse response = productService.addProductMaterial("prd-1", request);

        // Assert
        verify(product, times(1)).addMaterial(any(ProductMaterial.class));
        assertThat(response.materials().stream().anyMatch(m -> m.rawMaterialCode().equals("RM-1"))).isTrue();
        assertThat(response.code()).isEqualTo("PRD-1");
    }

    @Test
    void shouldDelegateToDomainUpdateMaterialQuantity() {
        // Arrange
        Product product = spy(new Product("PRD-1", "P1", bigDec("10.00")));

        RawMaterial rawMaterial = mock(RawMaterial.class);
        when(rawMaterial.getCode()).thenReturn("RM-1");
        when(rawMaterial.getUnitCost()).thenReturn(BigDecimal.TEN);

        ProductMaterial productMaterial = new ProductMaterial(rawMaterial, bigDec("1.0000"));
        product.addMaterial(productMaterial);

        when(productRepository.findByCodeWithComposition(any()))
                .thenReturn(Optional.of(product));

        ProductMaterialRequest request = new ProductMaterialRequest("rm-1", bigDec("3.5000"));

        // Act
        ProductResponse response = productService.updateProductMaterial("prd-1", request);

        // Assert
        verify(product).updateMaterialQuantity("RM-1", bigDec("3.5000"));
        assertThat(response.code()).isEqualTo("PRD-1");
    }

    @Test
    void shouldDelegateToDomainRemoveMaterial() {
        // Arrange
        Product product = spy(new Product("PRD-1", "P1", bigDec("10.00")));

        RawMaterial rawMaterial = mock(RawMaterial.class);
        when(rawMaterial.getCode()).thenReturn("RM-1");

        ProductMaterial productMaterial = new ProductMaterial(rawMaterial, bigDec("1.0000"));
        product.addMaterial(productMaterial);

        when(productRepository.findByCodeWithComposition(any()))
                .thenReturn(Optional.of(product));

        // Act
        productService.removeProductMaterial("prd-1", "rm-1");

        // Assert
        verify(product).removeMaterial("RM-1");
    }

    @Test
    void shouldDeleteProductWhenFound() {
        // Arrange
        Product product = new Product("PRD-1", "P1", bigDec("10.00"));
        when(productRepository.findByCodeWithComposition(any()))
                .thenReturn(Optional.of(product));

        // Act
        productService.deleteProductByCode("prd-1");

        // Assert
        verify(productRepository).delete(product);
    }
}
