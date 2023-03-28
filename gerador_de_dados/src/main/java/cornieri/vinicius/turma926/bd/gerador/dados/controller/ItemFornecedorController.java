package cornieri.vinicius.turma926.bd.gerador.dados.controller;

import cornieri.vinicius.turma926.bd.gerador.dados.model.Endereco;
import cornieri.vinicius.turma926.bd.gerador.dados.model.Fornecedor;
import cornieri.vinicius.turma926.bd.gerador.dados.model.ItemFornecedor;
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
@RequestMapping("/item_fornecedor")
@RequiredArgsConstructor
public class ItemFornecedorController {
    @PostMapping(value = "/lote/sql", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String criarLote(@RequestBody PostItemFornecedorDTO entrada) {

        if (entrada.produtos().maximoDeProdutosPorFornecedor() <= 0) {
            throw new ResponseStatusException(400, "produtos.maximoDeProdutosPorFornecedor deve ser maior que zero", null);
        }

        StringBuilder insert = new StringBuilder("insert into item_fornecedor(id_fornecedor, id_produto) values \n");

        String itensFornecedor = IntStream.range(entrada.fornecedores.initialIndex, entrada.fornecedores.finalIndex + 1)
                                          .mapToObj(i -> this.create(i, entrada.produtos()))
                                          .flatMap(Function.identity())
                                          .map(ItemFornecedor::toValuesSql)
                                          .collect(Collectors.joining(", "));
        insert.append(itensFornecedor);
        insert.append(";");
        return insert.toString();
    }

    private Stream<ItemFornecedor> create(int idFornecedor, PostProdutosDTO produtosDTO) {
        DataFakerFactory.FakerCustomizado faker = DataFakerFactory.INSTANCE;
        Integer quantidadeDeProdutosDoFornecedor = faker.random().nextInt(1, produtosDTO.maximoDeProdutosPorFornecedor + 1);
        return IntStream.range(0, quantidadeDeProdutosDoFornecedor)
                .mapToObj(i -> this.createSomenteComIds(idFornecedor, produtosDTO))
                .distinct();
    }

    public ItemFornecedor createSomenteComIds(int idFornecedor, PostProdutosDTO produtosDTO) {
        ItemFornecedor itemFornecedor = new ItemFornecedor();
        DataFakerFactory.FakerCustomizado faker = DataFakerFactory.INSTANCE;
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(idFornecedor);
        Produto produto = new Produto();
        Integer idProduto = faker.random().nextInt(produtosDTO.initialIndex, produtosDTO.finalIndex);
        produto.setId(idProduto);
        itemFornecedor.setFornecedor(fornecedor);
        itemFornecedor.setProduto(produto);
        return itemFornecedor;
    }


    public record PostItemFornecedorDTO(PostFornecedorDTO fornecedores, PostProdutosDTO produtos){}
    public record PostFornecedorDTO(Integer initialIndex, Integer finalIndex){}
    public record PostProdutosDTO(Integer initialIndex, Integer finalIndex, Integer maximoDeProdutosPorFornecedor){}
}
