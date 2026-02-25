package lat.luisdias.factory.manager.repository;

import lat.luisdias.factory.manager.model.ProductMaterial;
import lat.luisdias.factory.manager.model.vo.IdentificationCodeVO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductMaterialRepository extends JpaRepository<ProductMaterial, Long> {
    boolean existsByRawMaterialCode(IdentificationCodeVO rawMaterialCode);

    boolean existsByRawMaterialId(Long rawMaterialId);
}
