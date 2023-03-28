package cornieri.vinicius.turma926.bd.gerador.dados.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Table(schema = "ecommerce926", name = "cupom")
@Data
@NoArgsConstructor
public class Cupom {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="cupom_sequence")
    @SequenceGenerator(name="cupom_sequence", sequenceName="cupom_id_seq", schema = "ecommerce926", allocationSize = 1)
    private Integer id;
    @Column
    private String descricao;
    @Column
    private BigDecimal valor;

    @Column
    private LocalDateTime dataInicio;

    @Column
    private LocalDateTime dataExpiracao;

    @Column
    private Integer limiteMaximoUsos;

    public String toValuesSql() {
        return "('%s', %s, to_date('%s', 'DD-MM-YYYY HH24:MI:SS'), to_date('%s', 'DD-MM-YYYY HH24:MI:SS'), %s)".formatted(descricao, valor, getDataInicioFormatted(), getdataExpiracaoFormatted(), limiteMaximoUsos);
    }

    public String getDataInicioFormatted() {
        return dataInicio.format(DATE_TIME_FORMATTER);
    }

    public String getdataExpiracaoFormatted() {
        return dataExpiracao.format(DATE_TIME_FORMATTER);
    }

}
