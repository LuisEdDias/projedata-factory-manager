package lat.luisdias.factory.manager.repository;

import lat.luisdias.factory.manager.model.RawMaterial;
import lat.luisdias.factory.manager.model.vo.IdentificationCodeVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {
    Optional<RawMaterial> findByCode(IdentificationCodeVO code);

    boolean existsByCode(IdentificationCodeVO code);

    @Modifying
    @Query("DELETE FROM RawMaterial r WHERE r.code = :code")
    int deleteByCodeDirectly(@Param("code") IdentificationCodeVO code);
}
