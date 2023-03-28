package cornieri.vinicius.turma926.bd.gerador.dados.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(schema = "ecommerce926", name = "produto")
@Data
@NoArgsConstructor
public class Produto {
    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="produto_sequence")
    @SequenceGenerator(name="produto_sequence", sequenceName="produto_id_seq", schema = "ecommerce926", allocationSize = 1)
    private Integer id;
    @Column(name = "codigo_barras")
    private String codigoBarras;
    @Column
    private String descricao;
    @Column
    private BigDecimal valor;

    public BigDecimal getValor() {
        return valor != null ? valor.setScale(2, RoundingMode.HALF_EVEN) : null;
    }

    public String toValuesSql() {
        return "('%s', '%s', %s)".formatted(getDescricao(), getCodigoBarras(), getValor());
    }


}
