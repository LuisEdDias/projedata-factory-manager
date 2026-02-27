package lat.luisdias.factory.manager.service;

import lat.luisdias.factory.manager.dto.rawmaterial.RawMaterialCreateRequest;
import lat.luisdias.factory.manager.dto.rawmaterial.RawMaterialResponse;
import lat.luisdias.factory.manager.dto.rawmaterial.RawMaterialUpdateRequest;
import lat.luisdias.factory.manager.dto.rawmaterial.StockChangeRequest;
import lat.luisdias.factory.manager.exeption.exceptions.DomainInvariantViolationException;
import lat.luisdias.factory.manager.exeption.exceptions.NotFoundException;
import lat.luisdias.factory.manager.model.RawMaterial;
import lat.luisdias.factory.manager.model.vo.IdentificationCodeVO;
import lat.luisdias.factory.manager.repository.ProductMaterialRepository;
import lat.luisdias.factory.manager.repository.RawMaterialRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RawMaterialService {
    private final RawMaterialRepository rawMaterialRepository;
    private final ProductMaterialRepository productMaterialRepository;

    public RawMaterialService(
            RawMaterialRepository rawMaterialRepository,
            ProductMaterialRepository productMaterialRepository
    ) {
        this.rawMaterialRepository = rawMaterialRepository;
        this.productMaterialRepository = productMaterialRepository;
    }

    public RawMaterialResponse create(RawMaterialCreateRequest materialCreateRequest) {
        IdentificationCodeVO normalizedCode = new IdentificationCodeVO(materialCreateRequest.code());

        if (rawMaterialRepository.existsByCode(normalizedCode)) {
            throw new DomainInvariantViolationException(
                    "exception.domain.raw_material.code_already_exists",
                    normalizedCode.getValue()
            );
        }

        try {
            RawMaterial rawMaterial = new RawMaterial(
                    normalizedCode.getValue(),
                    materialCreateRequest.name(),
                    materialCreateRequest.initialStock(),
                    materialCreateRequest.unit()
            );

            return new RawMaterialResponse(rawMaterialRepository.save(rawMaterial));

        } catch (DataIntegrityViolationException ex) {
            throw new DomainInvariantViolationException(
                    "exception.domain.raw_material.code_already_exists",
                    ex,
                    normalizedCode.getValue()
            );
        }
    }

    @Transactional(readOnly = true)
    public RawMaterialResponse getByCode(String code) {
        RawMaterial rawMaterial = findOrThrow(code);
        return new RawMaterialResponse(rawMaterial);
    }

    @Transactional(readOnly = true)
    public Page<RawMaterialResponse> list(Pageable pageable) {
        return rawMaterialRepository.findAll(pageable)
                .map(RawMaterialResponse::new);
    }

    public RawMaterialResponse updateName(String code, RawMaterialUpdateRequest materialUpdateRequest) {
        RawMaterial rawMaterial = findOrThrow(code);
        rawMaterial.updateName(materialUpdateRequest.name());
        return new RawMaterialResponse(rawMaterial);
    }

    public RawMaterialResponse addStock(String code, StockChangeRequest stockChangeRequest) {
        RawMaterial rawMaterial = findOrThrow(code);
        rawMaterial.addStock(stockChangeRequest.quantity());
        return new RawMaterialResponse(rawMaterial);
    }

    public RawMaterialResponse consumeStock(String code, StockChangeRequest stockChangeRequest) {
        RawMaterial rawMaterial = findOrThrow(code);
        rawMaterial.consumeStock(stockChangeRequest.quantity());
        return new RawMaterialResponse(rawMaterial);
    }

    public void deleteByCode(String code) {
        IdentificationCodeVO normalizedCode = new IdentificationCodeVO(code);

        boolean inUse = productMaterialRepository.existsByRawMaterialCode(normalizedCode);
        if (inUse) {
            throw new DomainInvariantViolationException(
                    "exception.domain.raw_material.in_use",
                    normalizedCode.getValue()
            );
        }

        int deletedCount = rawMaterialRepository.deleteByCodeDirectly(normalizedCode);
        if (deletedCount == 0) {
            throw new NotFoundException(
                    "exception.domain.raw_material.not_found",
                    normalizedCode.getValue()
            );
        }
    }

    private RawMaterial findOrThrow(String code) {
        IdentificationCodeVO normalizedCode = new IdentificationCodeVO(code);
        return rawMaterialRepository.findByCode(normalizedCode)
                .orElseThrow(() -> new NotFoundException(
                        "exception.domain.raw_material.not_found",
                        normalizedCode.getValue()
                ));
    }
}
