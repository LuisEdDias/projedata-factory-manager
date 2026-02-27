package lat.luisdias.factory.manager.repository;

import lat.luisdias.factory.manager.model.Product;
import lat.luisdias.factory.manager.model.vo.IdentificationCodeVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("""
        SELECT p FROM Product p
        LEFT JOIN FETCH p.composition c
        LEFT JOIN FETCH c.rawMaterial
        WHERE p.code = :code
    """)
    Optional<Product> findByCodeWithComposition(@Param("code") IdentificationCodeVO code);

    @Query("""
        SELECT DISTINCT p FROM Product p
        LEFT JOIN FETCH p.composition c
        LEFT JOIN FETCH c.rawMaterial
        WHERE p IN :products
   """)
    void fetchCompositionsForProducts(@Param("products") List<Product> products);

    boolean existsByCode(IdentificationCodeVO code);
}
