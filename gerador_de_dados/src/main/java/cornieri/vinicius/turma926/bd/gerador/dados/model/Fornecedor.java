package cornieri.vinicius.turma926.bd.gerador.dados.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "ecommerce926", name = "fornecedor")
@Data
@NoArgsConstructor
public class Fornecedor {
    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="fornecedor_sequence")
    @SequenceGenerator(name="fornecedor_sequence", sequenceName="fornecedor_id_seq", schema = "ecommerce926", allocationSize = 1)
    private Integer id;
    @Column
    private String nome;
    @Column
    private String cnpj;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name="id_endereco", referencedColumnName = "id")
    private Endereco endereco;

    public String toValuesSql() {
        return "('%s', '%s', %s)".formatted(nome, cnpj, endereco.getId());
    }
}
