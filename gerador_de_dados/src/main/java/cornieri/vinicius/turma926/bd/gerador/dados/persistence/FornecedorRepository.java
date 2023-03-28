package cornieri.vinicius.turma926.bd.gerador.dados.persistence;

import cornieri.vinicius.turma926.bd.gerador.dados.model.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Integer> {
}
