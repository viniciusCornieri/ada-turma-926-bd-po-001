package cornieri.vinicius.turma926.bd.gerador.dados.persistence;

import cornieri.vinicius.turma926.bd.gerador.dados.model.Cliente;
import cornieri.vinicius.turma926.bd.gerador.dados.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
}
