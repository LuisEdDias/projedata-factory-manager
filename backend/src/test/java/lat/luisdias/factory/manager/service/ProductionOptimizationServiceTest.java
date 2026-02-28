package lat.luisdias.factory.manager.service;

import lat.luisdias.factory.manager.dto.production.ProductionPlanItem;
import lat.luisdias.factory.manager.dto.production.ProductionPlanResponse;
import lat.luisdias.factory.manager.model.MeasurementUnit;
import lat.luisdias.factory.manager.model.Product;
import lat.luisdias.factory.manager.model.ProductMaterial;
import lat.luisdias.factory.manager.model.RawMaterial;
import lat.luisdias.factory.manager.repository.ProductRepository;
import lat.luisdias.factory.manager.repository.RawMaterialRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductionOptimizationServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @InjectMocks
    private ProductionOptimizationService optimizationService;

    private RawMaterial createMaterial(String code, double stock) {
        return new RawMaterial(
                code,
                "Material " + code,
                BigDecimal.valueOf(stock),
                MeasurementUnit.KG,
                BigDecimal.valueOf(200)
        );
    }

    private Product createProduct(String code, double price, RawMaterial material, double quantityRequired) {
        Product product = new Product(code, "Product " + code, BigDecimal.valueOf(price));
        ProductMaterial productMaterial = new ProductMaterial(material, BigDecimal.valueOf(quantityRequired));
        product.addMaterial(productMaterial);
        return product;
    }

    @Test
    void shouldMaximizeRevenueUsingGreedyStrategy() {
        // Arrange
        RawMaterial wood = createMaterial("WOOD-01", 10.0);

        Product luxuryTable = createProduct("PROD-A", 1000.0, wood, 4.0);
        Product cheapChair = createProduct("PROD-B", 200.0, wood, 2.0);

        when(productRepository.findAllFetchCompositionsSortedByPriceDesc())
                .thenReturn(List.of(luxuryTable, cheapChair));
        when(rawMaterialRepository.findAll())
                .thenReturn(List.of(wood));

        // Act
        ProductionPlanResponse response = optimizationService.getPlanByRevenue();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.plan().size());

        ProductionPlanItem item1 = response.plan().get(0);
        assertEquals("PROD-A", item1.productCode());
        assertEquals(2L, item1.quantityToProduce());
        assertEquals(0, BigDecimal.valueOf(2000.0).compareTo(item1.totalValue()));
        assertEquals(0, BigDecimal.valueOf(400.0).compareTo(item1.itemProfit()));

        ProductionPlanItem item2 = response.plan().get(1);
        assertEquals("PROD-B", item2.productCode());
        assertEquals(1L, item2.quantityToProduce());
        assertEquals(0, BigDecimal.valueOf(200.0).compareTo(item2.totalValue()));
        assertEquals(0, BigDecimal.valueOf(-200.0).compareTo(item2.itemProfit()));

        assertEquals(0, BigDecimal.valueOf(2200.0).compareTo(response.estimatedTotalRevenue()));
        assertEquals(0, BigDecimal.valueOf(200.0).compareTo(response.estimatedTotalProfit()));
        assertEquals(0, BigDecimal.ZERO.compareTo(response.remainingStock().get("WOOD-01")));

        verify(productRepository, times(1)).findAllFetchCompositionsSortedByPriceDesc();
        verify(rawMaterialRepository, times(1)).findAll();
    }

    @Test
    void shouldStopProductionDueToBottleneckIngredient() {
        // Arrange
        RawMaterial wood = createMaterial("WOOD-01", 100.0);
        RawMaterial screw = createMaterial("SCREW-01", 3.0);

        Product table = new Product("PROD-A", "Table", BigDecimal.valueOf(500.0));
        table.addMaterial(new ProductMaterial(wood, BigDecimal.valueOf(10.0)));
        table.addMaterial(new ProductMaterial(screw, BigDecimal.valueOf(2.0)));

        when(productRepository.findAllFetchCompositionsSortedByPriceDesc())
                .thenReturn(List.of(table));
        when(rawMaterialRepository.findAll())
                .thenReturn(List.of(wood, screw));

        // Act
        ProductionPlanResponse response = optimizationService.getPlanByRevenue();

        // Assert
        assertEquals(1, response.plan().size());
        assertEquals(1L, response.plan().get(0).quantityToProduce());

        assertEquals(0, BigDecimal.valueOf(90.0).compareTo(response.remainingStock().get("WOOD-01")));
        assertEquals(0, BigDecimal.valueOf(1.0).compareTo(response.remainingStock().get("SCREW-01")));

        verify(productRepository, times(1)).findAllFetchCompositionsSortedByPriceDesc();
        verify(rawMaterialRepository, times(1)).findAll();
    }

    @Test
    void shouldRoundDownWhenFractionalStockRemains() {
        // Arrange
        RawMaterial fabric = createMaterial("TEC-01", 5.5);
        Product shirt = createProduct("PROD-A", 50.0, fabric, 2.0);

        when(productRepository.findAllFetchCompositionsSortedByPriceDesc())
                .thenReturn(List.of(shirt));
        when(rawMaterialRepository.findAll())
                .thenReturn(List.of(fabric));

        // Act
        ProductionPlanResponse response = optimizationService.getPlanByRevenue();

        // Assert
        assertEquals(1, response.plan().size());
        assertEquals(2L, response.plan().get(0).quantityToProduce());
        assertEquals(0, BigDecimal.valueOf(1.5).compareTo(response.remainingStock().get("TEC-01")));

        verify(productRepository, times(1)).findAllFetchCompositionsSortedByPriceDesc();
        verify(rawMaterialRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyPlanWhenDatabaseIsEmpty() {
        // Arrange
        when(productRepository.findAllFetchCompositionsSortedByPriceDesc())
                .thenReturn(Collections.emptyList());
        when(rawMaterialRepository.findAll())
                .thenReturn(Collections.emptyList());

        // Act
        ProductionPlanResponse response = optimizationService.getPlanByRevenue();

        // Assert
        assertTrue(response.plan().isEmpty());
        assertEquals(BigDecimal.ZERO, response.estimatedTotalRevenue());
        assertEquals(BigDecimal.ZERO, response.estimatedTotalProfit());
        assertTrue(response.remainingStock().isEmpty());

        verify(productRepository, times(1)).findAllFetchCompositionsSortedByPriceDesc();
        verify(rawMaterialRepository, times(1)).findAll();
    }

    @Test
    void shouldSkipProductsWithEmptyComposition() {
        // Arrange
        Product emptyProduct = new Product("PROD-EMPTY", "Product", BigDecimal.valueOf(100.0));

        when(productRepository.findAllFetchCompositionsSortedByPriceDesc())
                .thenReturn(List.of(emptyProduct));
        when(rawMaterialRepository.findAll())
                .thenReturn(Collections.emptyList());

        // Act
        ProductionPlanResponse response = optimizationService.getPlanByRevenue();

        // Assert
        assertTrue(response.plan().isEmpty());
        assertEquals(BigDecimal.ZERO, response.estimatedTotalRevenue());
        assertEquals(BigDecimal.ZERO, response.estimatedTotalProfit());

        verify(productRepository, times(1)).findAllFetchCompositionsSortedByPriceDesc();
        verify(rawMaterialRepository, times(1)).findAll();
    }

    @Test
    void shouldSkipProductsWhenNotEnoughStockForOneUnit() {
        // Arrange
        RawMaterial wood = createMaterial("WOOD-01", 2.0);
        Product table = createProduct("PROD-A", 500.0, wood, 5.0);

        when(productRepository.findAllFetchCompositionsSortedByPriceDesc())
                .thenReturn(List.of(table));
        when(rawMaterialRepository.findAll())
                .thenReturn(List.of(wood));

        // Act
        ProductionPlanResponse response = optimizationService.getPlanByRevenue();

        // Assert
        assertTrue(response.plan().isEmpty());
        assertEquals(BigDecimal.ZERO, response.estimatedTotalRevenue());
        assertEquals(BigDecimal.ZERO, response.estimatedTotalProfit());
        assertEquals(0, BigDecimal.valueOf(2.0).compareTo(response.remainingStock().get("WOOD-01")));

        verify(productRepository, times(1)).findAllFetchCompositionsSortedByPriceDesc();
        verify(rawMaterialRepository, times(1)).findAll();
    }

    @Test
    void shouldPrioritizeProductWithHigherUnitProfit() {
        // Arrange
        RawMaterial wood = new RawMaterial(
                "MAT-01",
                "Wood",
                BigDecimal.valueOf(100),
                MeasurementUnit.UN,
                BigDecimal.valueOf(100)
        );

        Product highRevenueLowProfit = new Product("PROD-A", "A", BigDecimal.valueOf(1000.0));
        highRevenueLowProfit.addMaterial(new ProductMaterial(wood, BigDecimal.valueOf(3.0)));

        Product lowerRevenueHigherProfit = new Product("PROD-B", "B", BigDecimal.valueOf(1000.0));
        lowerRevenueHigherProfit.addMaterial(new ProductMaterial(wood, BigDecimal.valueOf(2.0)));

        when(productRepository.findAllFetchCompositionsSortedByPriceDesc())
                .thenReturn(List.of(highRevenueLowProfit, lowerRevenueHigherProfit));

        when(rawMaterialRepository.findAll())
                .thenReturn(List.of(wood));

        // Act
        ProductionPlanResponse response = optimizationService.getPlanByProfit();

        // Assert
        assertEquals(1, response.plan().size());

        ProductionPlanItem item = response.plan().get(0);
        assertEquals("PROD-B", item.productCode());
        assertEquals(50L, item.quantityToProduce());

        verify(productRepository, times(1)).findAllFetchCompositionsSortedByPriceDesc();
        verify(rawMaterialRepository, times(1)).findAll();
    }
}
