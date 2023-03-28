package cornieri.vinicius.turma926.bd.gerador.dados.persistence;

import cornieri.vinicius.turma926.bd.gerador.dados.model.Cupom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CupomRepository extends JpaRepository<Cupom, Integer> {
}
