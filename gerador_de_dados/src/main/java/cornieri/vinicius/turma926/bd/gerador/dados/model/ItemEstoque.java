package cornieri.vinicius.turma926.bd.gerador.dados.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class ItemEstoque {

    private Estoque estoque;

    private Produto produto;

    private int quantidade;

    public String toValuesSql() {
        return "(%s, %s, %s)".formatted(estoque.getId(), produto.getId(), quantidade);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemEstoque that = (ItemEstoque) o;
        return Objects.equals(estoque.getId(), that.estoque.getId())
                && Objects.equals(produto.getId(), that.produto.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(estoque, produto);
    }
}
