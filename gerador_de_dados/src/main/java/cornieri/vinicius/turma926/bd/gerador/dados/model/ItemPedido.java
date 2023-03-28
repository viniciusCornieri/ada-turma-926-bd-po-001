package cornieri.vinicius.turma926.bd.gerador.dados.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Data
@NoArgsConstructor
public class ItemPedido {
    private Pedido pedido;

    private Produto produto;

    private int quantidade;

    private BigDecimal valor;

    public BigDecimal getValor() {
        return valor.setScale(2, RoundingMode.HALF_EVEN);
    }

    public String toValuesSql() {
        return "(%s, %s, %s, %s)".formatted(pedido.getId(), produto.getId(), quantidade, getValor());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemPedido that = (ItemPedido) o;
        return Objects.equals(pedido.getId(), that.pedido.getId())
                && Objects.equals(produto.getId(), that.produto.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(pedido, produto);
    }
}
