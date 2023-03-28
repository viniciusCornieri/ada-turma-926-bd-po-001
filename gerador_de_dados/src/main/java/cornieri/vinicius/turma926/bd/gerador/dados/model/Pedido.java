package cornieri.vinicius.turma926.bd.gerador.dados.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Data
@NoArgsConstructor
public class Pedido {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private Integer id;

    private Cliente cliente;


    private Cupom cupom;

    private StatusPedido status;

    private MeioPagamento meioPagamento;

    private LocalDateTime dataCriacao;

    private LocalDateTime previsaoEntrega;
    public String toValuesSql() {
        return "(%s, %s, '%s', '%s', to_date('%s', 'DD-MM-YYYY HH24:MI:SS'), to_date('%s', 'DD-MM-YYYY'))".formatted(cliente.getId(), getIdCupom(), status, meioPagamento, getDataCriacaoFormatted(), getPrevisaoEntregaFormatted());
    }

    public String getDataCriacaoFormatted() {
        return dataCriacao.format(DATE_TIME_FORMATTER);
    }

    public String getPrevisaoEntregaFormatted() {
        return previsaoEntrega.format(DATE_FORMATTER);
    }

    public Integer getIdCupom() {
        return cupom != null ? cupom.getId() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pedido that = (Pedido) o;
        return Objects.equals(this.cliente.getId(), that.cliente.getId())
                && this.cupom != null
                && that.cupom != null
                && Objects.equals(this.cupom.getId(), that.cupom.getId());
    }


    @Override
    public int hashCode() {
        return Objects.hash(this.cliente, this.cupom);
    }
}
