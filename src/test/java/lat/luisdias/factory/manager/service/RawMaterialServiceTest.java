package lat.luisdias.factory.manager.service;

import lat.luisdias.factory.manager.dto.rawmaterial.RawMaterialCreateRequest;
import lat.luisdias.factory.manager.dto.rawmaterial.RawMaterialResponse;
import lat.luisdias.factory.manager.dto.rawmaterial.RawMaterialUpdateRequest;
import lat.luisdias.factory.manager.dto.rawmaterial.StockChangeRequest;
import lat.luisdias.factory.manager.exeption.exceptions.DomainInvariantViolationException;
import lat.luisdias.factory.manager.exeption.exceptions.NotFoundException;
import lat.luisdias.factory.manager.model.MeasurementUnit;
import lat.luisdias.factory.manager.model.RawMaterial;
import lat.luisdias.factory.manager.model.vo.IdentificationCodeVO;
import lat.luisdias.factory.manager.repository.ProductMaterialRepository;
import lat.luisdias.factory.manager.repository.RawMaterialRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RawMaterialServiceTest {

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @Mock
    private ProductMaterialRepository productMaterialRepository;

    @InjectMocks
    private RawMaterialService rawMaterialService;

    private RawMaterial createValidRawMaterial() {
        return new RawMaterial("WOOD-01", "Wood", BigDecimal.valueOf(100), MeasurementUnit.KG);
    }

    @Test
    void shouldCreateRawMaterialSuccessfully() {
        // Arrange
        RawMaterialCreateRequest request = new RawMaterialCreateRequest(
                "WOOD-01",
                "Wood",
                BigDecimal.valueOf(100),
                MeasurementUnit.KG
        );
        RawMaterial rawMaterial = createValidRawMaterial();

        when(rawMaterialRepository.existsByCode(any(IdentificationCodeVO.class))).thenReturn(false);
        when(rawMaterialRepository.save(any(RawMaterial.class))).thenReturn(rawMaterial);

        // Act
        RawMaterialResponse response = rawMaterialService.create(request);

        // Assert
        assertNotNull(response);
        assertEquals("WOOD-01", response.code());
        verify(rawMaterialRepository, times(1)).save(any(RawMaterial.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingIfCodeAlreadyExists() {
        // Arrange
        RawMaterialCreateRequest request = new RawMaterialCreateRequest(
                "WOOD-01",
                "Wood",
                BigDecimal.TEN,
                MeasurementUnit.UN
        );
        when(rawMaterialRepository.existsByCode(any(IdentificationCodeVO.class))).thenReturn(true);

        // Act & Assert
        assertThrows(DomainInvariantViolationException.class, () -> rawMaterialService.create(request));
        verify(rawMaterialRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCreatingIfDataIntegrityViolationOccurs() {
        // Arrange
        RawMaterialCreateRequest request = new RawMaterialCreateRequest(
                "WOOD-01",
                "Wood",
                BigDecimal.TEN,
                MeasurementUnit.UN
        );

        when(rawMaterialRepository.existsByCode(any(IdentificationCodeVO.class))).thenReturn(false);
        when(rawMaterialRepository.save(any(RawMaterial.class))).thenThrow(DataIntegrityViolationException.class);

        // Act & Assert
        assertThrows(DomainInvariantViolationException.class, () -> rawMaterialService.create(request));
    }

    @Test
    void shouldGetByCodeSuccessfully() {
        // Arrange
        RawMaterial rawMaterial = createValidRawMaterial();
        when(rawMaterialRepository.findByCode(any(IdentificationCodeVO.class))).thenReturn(Optional.of(rawMaterial));

        // Act
        RawMaterialResponse response = rawMaterialService.getByCode("WOOD-01");

        // Assert
        assertNotNull(response);
        assertEquals("WOOD-01", response.code());
    }

    @Test
    void shouldReturnPageOfRawMaterials() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 10);
        RawMaterial rawMaterial = createValidRawMaterial();
        Page<RawMaterial> page = new PageImpl<>(List.of(rawMaterial));

        when(rawMaterialRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<RawMaterialResponse> result = rawMaterialService.list(pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("WOOD-01", result.getContent().get(0).code());
    }

    @Test
    void shouldDeleteSuccessfully() {
        // Arrange
        when(productMaterialRepository.existsByRawMaterialCode(any(IdentificationCodeVO.class))).thenReturn(false);
        when(rawMaterialRepository.deleteByCodeDirectly(any(IdentificationCodeVO.class))).thenReturn(1);

        // Act & Assert
        assertDoesNotThrow(() -> rawMaterialService.deleteByCode("WOOD-01"));
        verify(rawMaterialRepository, times(1)).deleteByCodeDirectly(any(IdentificationCodeVO.class));
    }

    @Test
    void shouldThrowExceptionWhenDeletingMaterialInUse() {
        // Arrange
        when(productMaterialRepository.existsByRawMaterialCode(any(IdentificationCodeVO.class))).thenReturn(true);

        // Act & Assert
        assertThrows(DomainInvariantViolationException.class, () -> rawMaterialService.deleteByCode("WOOD-01"));
        verify(rawMaterialRepository, never()).deleteByCodeDirectly(any());
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentMaterial() {
        // Arrange
        when(productMaterialRepository.existsByRawMaterialCode(any(IdentificationCodeVO.class))).thenReturn(false);
        when(rawMaterialRepository.deleteByCodeDirectly(any(IdentificationCodeVO.class))).thenReturn(0);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> rawMaterialService.deleteByCode("WOOD-01"));
    }

    @Test
    void shouldUpdateNameSuccessfully() {
        // Arrange
        RawMaterial rawMaterial = createValidRawMaterial();
        RawMaterialUpdateRequest request = new RawMaterialUpdateRequest("Solid Wood");
        when(rawMaterialRepository.findByCode(any(IdentificationCodeVO.class))).thenReturn(Optional.of(rawMaterial));

        // Act
        RawMaterialResponse response = rawMaterialService.updateName("WOOD-01", request);

        // Assert
        assertEquals("Solid Wood", response.name());
    }

    @Test
    void shouldAddStockSuccessfully() {
        // Arrange
        RawMaterial rawMaterial = createValidRawMaterial();
        StockChangeRequest request = new StockChangeRequest(BigDecimal.valueOf(50));
        when(rawMaterialRepository.findByCode(any(IdentificationCodeVO.class))).thenReturn(Optional.of(rawMaterial));

        // Act
        RawMaterialResponse response = rawMaterialService.addStock("WOOD-01", request);

        // Assert
        assertEquals(BigDecimal.valueOf(150), response.stockQuantity());
    }

    @Test
    void shouldConsumeStockSuccessfully() {
        // Arrange
        RawMaterial rawMaterial = createValidRawMaterial();
        StockChangeRequest request = new StockChangeRequest(BigDecimal.valueOf(50));
        when(rawMaterialRepository.findByCode(any(IdentificationCodeVO.class))).thenReturn(Optional.of(rawMaterial));

        // Act
        RawMaterialResponse response = rawMaterialService.consumeStock("WOOD-01", request);

        // Assert
        assertEquals(BigDecimal.valueOf(50), response.stockQuantity());
    }
}
