package cornieri.vinicius.turma926.bd.gerador.dados.controller;

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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
@RequestMapping("/produto")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoRepository produtoRepository;

    @PostMapping(value = "/lote", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Produto> criarLote(@RequestBody PostProdutoDTO entrada) {
        Stream<Produto> produtos = generateProdutoStream(entrada);

        return produtoRepository.saveAll(produtos.toList());
    }

    @PostMapping(value = "/lote/sql", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String criarLoteSql(@RequestBody PostProdutoDTO entrada) {
        Stream<Produto> produtos = generateProdutoStream(entrada);
        String produtosSql = produtos
                           .map(Produto::toValuesSql)
                           .collect(Collectors.joining(", \n    "));
        StringBuilder insert = new StringBuilder("insert into produto(descricao, codigo_barras, valor) values \n    ");

        insert.append(produtosSql);
        insert.append(";");
        return insert.toString();
    }

    private Stream<Produto> generateProdutoStream(PostProdutoDTO entrada) {
        int quantidade = entrada.quantidade();

        if (quantidade <= 0) {
            throw new ResponseStatusException(400, "quantidade deve ser maior que zero", null);
        }

        Stream<Produto> produtos = IntStream.range(0, quantidade)
                                            .mapToObj(this::createProduto);
        return produtos;
    }

    private Produto createProduto(int i) {
        Produto produto = new Produto();
        DataFakerFactory.FakerCustomizado faker = DataFakerFactory.INSTANCE;
        produto.setCodigoBarras(String.valueOf(faker.barcode().gtin14()));
        produto.setDescricao(faker.produtos().nextProductName());
        produto.setValor(BigDecimal.valueOf(faker.random().nextDouble(0.0, 500.0)));
        return produto;
    }

    public record PostProdutoDTO(int quantidade){}
}
