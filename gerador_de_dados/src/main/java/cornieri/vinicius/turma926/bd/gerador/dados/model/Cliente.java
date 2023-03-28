package cornieri.vinicius.turma926.bd.gerador.dados.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(schema = "ecommerce926", name = "cliente")
@Data
@NoArgsConstructor
public class Cliente {
    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="cliente_sequence")
    @SequenceGenerator(name="cliente_sequence", sequenceName="cliente_id_seq", schema = "ecommerce926", allocationSize = 1)
    private Integer id;
    @Column
    private String nome;
    @Column
    private String cpf;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name="id_endereco", referencedColumnName = "id")
    private Endereco endereco;

    public String toValuesSql() {
        return "('%s', '%s', %s)".formatted(nome, cpf, endereco.getId());
    }
}
