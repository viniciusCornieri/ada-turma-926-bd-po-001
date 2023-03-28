package cornieri.vinicius.turma926.bd.gerador.dados.controller;

import cornieri.vinicius.turma926.bd.gerador.dados.model.Pedido;
import cornieri.vinicius.turma926.bd.gerador.dados.model.ItemPedido;
import cornieri.vinicius.turma926.bd.gerador.dados.model.Produto;
import cornieri.vinicius.turma926.bd.gerador.dados.persistence.ProdutoRepository;
import cornieri.vinicius.turma926.bd.gerador.dados.service.DataFakerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
@RequestMapping("/item_pedido")
@RequiredArgsConstructor
public class ItemPedidoController {

    private final ProdutoRepository produtoRepository;

    @PostMapping(value = "/lote/sql", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String criarLote(@RequestBody PostItemPedidoDTO entrada) {

        if (entrada.produtos().maximoDeProdutosPorPedido() <= 0) {
            throw new ResponseStatusException(400, "produtos.maximoDeProdutosPorPedido deve ser maior que zero", null);
        }

        StringBuilder insert = new StringBuilder("insert into item_pedido(id_pedido, id_produto, quantidade, valor) values \n");
        Map<Integer, BigDecimal> mapValorProdutos = produtoRepository
                .findAll()
                .stream()
                .collect(Collectors.toMap(Produto::getId, Produto::getValor));
        List<String> itensPedido = IntStream.range(entrada.pedidos.initialIndex, entrada.pedidos.finalIndex + 1)
                                            .mapToObj(i -> this.create(i, entrada.produtos(), mapValorProdutos))
                                            .flatMap(Function.identity())
                                            .map(ItemPedido::toValuesSql)
                                            .collect(Collectors.toList());

        for (int i = 0; i < itensPedido.size(); i++) {
            insert.append(itensPedido.get(i));
            if (i < itensPedido.size() -1) {
                insert.append(", ");
            }
            if (i % 5 == 0) {
                insert.append("\n    ");
            }
        }

        insert.append(";");
        return insert.toString();
    }

    private Stream<ItemPedido> create(int idPedido, PostProdutosDTO produtosDTO, Map<Integer, BigDecimal> mapValorProdutos) {
        DataFakerFactory.FakerCustomizado faker = DataFakerFactory.INSTANCE;
        Integer quantidadeDeProdutosDoPedido = faker.random().nextInt(1, produtosDTO.maximoDeProdutosPorPedido + 1);
        return IntStream.range(0, quantidadeDeProdutosDoPedido)
                .mapToObj(i -> this.createSomenteComIds(idPedido, produtosDTO, mapValorProdutos))
                .distinct();
    }

    public ItemPedido createSomenteComIds(int idPedido, PostProdutosDTO produtosDTO, Map<Integer, BigDecimal> mapValorProdutos) {
        ItemPedido itemPedido = new ItemPedido();
        DataFakerFactory.FakerCustomizado faker = DataFakerFactory.INSTANCE;
        Pedido pedido = new Pedido();
        pedido.setId(idPedido);
        Produto produto = new Produto();
        Integer idProduto = faker.random().nextInt(produtosDTO.initialIndex, produtosDTO.finalIndex);
        produto.setId(idProduto);
        itemPedido.setPedido(pedido);
        itemPedido.setProduto(produto);
        Integer quantidadeDeProdutos = faker.random().nextInt(1, produtosDTO.maximoDeQuantidadeDeProdutos());
        itemPedido.setQuantidade(quantidadeDeProdutos);
        BigDecimal valorOriginalProduto = mapValorProdutos.getOrDefault(idProduto, BigDecimal.valueOf(faker.random().nextDouble(0.0, 500.0)));
        BigDecimal margemAleatoria = BigDecimal.valueOf(faker.random().nextDouble(0.9, 1.1));
        itemPedido.setValor(valorOriginalProduto.multiply(margemAleatoria));
        return itemPedido;
    }

    public record PostItemPedidoDTO(PostPedidoDTO pedidos, PostProdutosDTO produtos){}
    public record PostPedidoDTO(Integer initialIndex, Integer finalIndex){}
    public record PostProdutosDTO(Integer initialIndex, Integer finalIndex, Integer maximoDeProdutosPorPedido, Integer maximoDeQuantidadeDeProdutos){}
}
