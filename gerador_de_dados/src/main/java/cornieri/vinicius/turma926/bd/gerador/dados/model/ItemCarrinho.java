package cornieri.vinicius.turma926.bd.gerador.dados.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Data
@NoArgsConstructor
public class ItemCarrinho {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private Cliente cliente;

    private Produto produto;

    private int quantidade;

    private LocalDateTime dataInsercao;

    public String toValuesSql() {
        return "(%s, %s, %s, to_date('%s', 'DD-MM-YYYY HH24:MI:SS'))".formatted(cliente.getId(), produto.getId(), quantidade, getDataInsercaoFormatted());
    }

    public String getDataInsercaoFormatted() {
        return dataInsercao.format(DATE_TIME_FORMATTER);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemCarrinho that = (ItemCarrinho) o;
        return Objects.equals(cliente.getId(), that.cliente.getId())
                && Objects.equals(produto.getId(), that.produto.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(cliente, produto);
    }
}
