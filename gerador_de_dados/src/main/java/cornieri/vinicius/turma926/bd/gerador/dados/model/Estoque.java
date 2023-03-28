package cornieri.vinicius.turma926.bd.gerador.dados.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "ecommerce926", name = "estoque")
@Data
@NoArgsConstructor
public class Estoque {
    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="estoque_sequence")
    @SequenceGenerator(name="estoque_sequence", sequenceName="estoque_id_seq", schema = "ecommerce926", allocationSize = 1)
    private Integer id;
    @Column
    private String descricao;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name="id_endereco", referencedColumnName = "id")
    private Endereco endereco;

    public String toValuesSql() {
        return "('%s', %s)".formatted(descricao, endereco.getId());
    }
}
