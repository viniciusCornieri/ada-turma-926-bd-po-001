package cornieri.vinicius.turma926.bd.gerador.dados.controller;

import cornieri.vinicius.turma926.bd.gerador.dados.model.Cliente;
import cornieri.vinicius.turma926.bd.gerador.dados.model.ItemCarrinho;
import cornieri.vinicius.turma926.bd.gerador.dados.model.Produto;
import cornieri.vinicius.turma926.bd.gerador.dados.service.DataFakerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
@RequestMapping("/item_carrinho")
@RequiredArgsConstructor
public class ItemCarrinhoController {
    @PostMapping(value = "/lote/sql", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String criarLote(@RequestBody PostItemCarrinhoDTO entrada) {

        if (entrada.produtos().maximoDeProdutosPorCliente() <= 0) {
            throw new ResponseStatusException(400, "produtos.maximoDeProdutosPorCliente deve ser maior que zero", null);
        }

        if (entrada.produtos().maximoDeQuantidadeDeProdutos() <= 0) {
            throw new ResponseStatusException(400, "produtos.maximoDeQuantidadeDeProdutos deve ser maior que zero", null);
        }

        StringBuilder insert = new StringBuilder("insert into item_carrinho(id_cliente, id_produto, quantidade, data_insercao) values \n");

        DataFakerFactory.FakerCustomizado faker = DataFakerFactory.INSTANCE;
        Integer quantidadeDeClientesComItensNoCarrinho = faker.random().nextInt(entrada.clientes.initialIndex, entrada.clientes.finalIndex);

        String itensCliente = IntStream.range(0, quantidadeDeClientesComItensNoCarrinho)
                                          .map(i -> faker.random().nextInt(entrada.clientes.initialIndex, entrada.clientes.finalIndex))
                                          .distinct()
                                          .mapToObj(i -> this.create(i, entrada.produtos()))
                                          .flatMap(Function.identity())
                                          .map(ItemCarrinho::toValuesSql)
                                          .collect(Collectors.joining(", \n    "));
        insert.append(itensCliente);
        insert.append(";");
        return insert.toString();
    }

    private Stream<ItemCarrinho> create(int idCliente, PostProdutosDTO produtosDTO) {
        DataFakerFactory.FakerCustomizado faker = DataFakerFactory.INSTANCE;
        Integer quantidadeDeProdutosDoCliente = faker.random().nextInt(1, produtosDTO.maximoDeProdutosPorCliente);
        return IntStream.range(0, quantidadeDeProdutosDoCliente)
                .mapToObj(i -> this.createSomenteComIds(idCliente, produtosDTO))
                .distinct();
    }

    public ItemCarrinho createSomenteComIds(int idCliente, PostProdutosDTO produtosDTO) {
        ItemCarrinho itemCarrinho = new ItemCarrinho();
        DataFakerFactory.FakerCustomizado faker = DataFakerFactory.INSTANCE;
        Cliente cliente = new Cliente();
        cliente.setId(idCliente);
        Produto produto = new Produto();
        Integer idProduto = faker.random().nextInt(produtosDTO.initialIndex, produtosDTO.finalIndex);
        produto.setId(idProduto);
        itemCarrinho.setCliente(cliente);
        itemCarrinho.setProduto(produto);
        itemCarrinho.setQuantidade(faker.random().nextInt(1, produtosDTO.maximoDeQuantidadeDeProdutos));
        LocalDateTime randomDate = faker.nextCustomRandomLocalDateTime();
        itemCarrinho.setDataInsercao(randomDate);
        return itemCarrinho;
    }


    public record PostItemCarrinhoDTO(PostClienteDTO clientes, PostProdutosDTO produtos){}
    public record PostClienteDTO(Integer initialIndex, Integer finalIndex){}
    public record PostProdutosDTO(Integer initialIndex, Integer finalIndex, Integer maximoDeProdutosPorCliente, Integer maximoDeQuantidadeDeProdutos){}
}
