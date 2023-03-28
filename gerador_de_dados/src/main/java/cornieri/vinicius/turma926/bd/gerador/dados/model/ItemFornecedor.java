package cornieri.vinicius.turma926.bd.gerador.dados.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class ItemFornecedor {
    private Fornecedor fornecedor;

    private Produto produto;

    public String toValuesSql() {
        return "(%s, %s)".formatted(fornecedor.getId(), produto.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemFornecedor that = (ItemFornecedor) o;
        return Objects.equals(fornecedor.getId(), that.fornecedor.getId())
                && Objects.equals(produto.getId(), that.produto.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(fornecedor, produto);
    }
}
