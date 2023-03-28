package cornieri.vinicius.turma926.bd.gerador.dados.controller;

import cornieri.vinicius.turma926.bd.gerador.dados.model.Estoque;
import cornieri.vinicius.turma926.bd.gerador.dados.model.ItemEstoque;
import cornieri.vinicius.turma926.bd.gerador.dados.model.Produto;
import cornieri.vinicius.turma926.bd.gerador.dados.service.DataFakerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
@RequestMapping("/item_estoque")
@RequiredArgsConstructor
public class ItemEstoqueController {
    @PostMapping(value = "/lote/sql", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String criarLote(@RequestBody PostItemEstoqueDTO entrada) {

        if (entrada.produtos().maximoDeProdutosPorEstoque() <= 0) {
            throw new ResponseStatusException(400, "produtos.maximoDeProdutosPorEstoque deve ser maior que zero", null);
        }

        if (entrada.produtos().maximoDeQuantidadeDeProdutos() <= 0) {
            throw new ResponseStatusException(400, "produtos.maximoDeQuantidadeDeProdutos deve ser maior que zero", null);
        }

        StringBuilder insert = new StringBuilder("insert into item_estoque(id_estoque, id_produto, quantidade) values \n");

        String itensEstoque = IntStream.range(entrada.estoques.initialIndex, entrada.estoques.finalIndex + 1)
                                          .mapToObj(i -> this.create(i, entrada.produtos()))
                                          .flatMap(Function.identity())
                                          .map(ItemEstoque::toValuesSql)
                                          .collect(Collectors.joining(", "));
        insert.append(itensEstoque);
        insert.append(";");
        return insert.toString();
    }

    private Stream<ItemEstoque> create(int idEstoque, PostProdutosDTO produtosDTO) {
        DataFakerFactory.FakerCustomizado faker = DataFakerFactory.INSTANCE;
        Integer quantidadeDeProdutosDoEstoque = faker.random().nextInt(1, produtosDTO.maximoDeProdutosPorEstoque + 1);
        return IntStream.range(0, quantidadeDeProdutosDoEstoque)
                .mapToObj(i -> this.createSomenteComIds(idEstoque, produtosDTO))
                .distinct();
    }

    public ItemEstoque createSomenteComIds(int idEstoque, PostProdutosDTO produtosDTO) {
        ItemEstoque itemEstoque = new ItemEstoque();
        DataFakerFactory.FakerCustomizado faker = DataFakerFactory.INSTANCE;
        Estoque estoque = new Estoque();
        estoque.setId(idEstoque);
        Produto produto = new Produto();
        Integer idProduto = faker.random().nextInt(produtosDTO.initialIndex, produtosDTO.finalIndex);
        produto.setId(idProduto);
        itemEstoque.setEstoque(estoque);
        itemEstoque.setProduto(produto);
        itemEstoque.setQuantidade(faker.random().nextInt(0, produtosDTO.maximoDeQuantidadeDeProdutos));
        return itemEstoque;
    }


    public record PostItemEstoqueDTO(PostEstoqueDTO estoques, PostProdutosDTO produtos){}
    public record PostEstoqueDTO(Integer initialIndex, Integer finalIndex){}
    public record PostProdutosDTO(Integer initialIndex, Integer finalIndex, Integer maximoDeProdutosPorEstoque, Integer maximoDeQuantidadeDeProdutos){}
}
