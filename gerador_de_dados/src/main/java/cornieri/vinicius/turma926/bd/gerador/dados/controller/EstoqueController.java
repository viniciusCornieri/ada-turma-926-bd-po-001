package cornieri.vinicius.turma926.bd.gerador.dados.controller;

import cornieri.vinicius.turma926.bd.gerador.dados.model.Endereco;
import cornieri.vinicius.turma926.bd.gerador.dados.model.Estoque;
import cornieri.vinicius.turma926.bd.gerador.dados.persistence.EstoqueRepository;
import cornieri.vinicius.turma926.bd.gerador.dados.service.DataFakerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
@RequestMapping("/estoque")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueRepository estoqueRepository;

    private final EnderecoController enderecoController;

    @PostMapping(value = "/lote", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Estoque> criarLote(@RequestBody PostEstoqueDTO entrada) {
        Stream<Estoque> estoques = generateEstoqueStream(entrada.quantidade, enderecoController::create);

        return estoqueRepository.saveAll(estoques.toList());
    }

    @PostMapping(value = "/lote/sql", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String criarLoteSql(@RequestBody PostEstoqueSQLDTO entrada) {


        if (entrada.enderecoInitialIndex() <= 0) {
            throw new ResponseStatusException(400, "endereco initial index deve ser maior que zero", null);
        }


        Stream<Estoque> estoques = generateEstoqueStream(entrada.quantidade(), (i) -> this.createEnderecoSomenteComId(entrada.enderecoInitialIndex(), i));
        String estoquesSql = estoques
                           .map(Estoque::toValuesSql)
                           .collect(Collectors.joining(", \n    "));
        StringBuilder insert = new StringBuilder("insert into estoque(descricao, id_endereco) values \n    ");

        insert.append(estoquesSql);
        insert.append(";");
        return insert.toString();
    }

    private Stream<Estoque> generateEstoqueStream(int quantidade, IntFunction<Endereco> enderecoSuplier) {

        if (quantidade <= 0) {
            throw new ResponseStatusException(400, "quantidade deve ser maior que zero", null);
        }


        Stream<Estoque> estoques = IntStream.range(0, quantidade)
                .mapToObj(enderecoSuplier)
                .map(this::createEstoque);
        return estoques;
    }

    private Estoque createEstoque(Endereco endereco) {
        Estoque estoque = new Estoque();
        DataFakerFactory.FakerCustomizado faker = DataFakerFactory.INSTANCE;
        estoque.setDescricao(faker.lordOfTheRings().location());
        estoque.setEndereco(endereco);
        return estoque;
    }

    private Endereco createEnderecoSomenteComId(int enderecoInitialIndex, int i) {
        Endereco endereco = new Endereco();
        endereco.setId(enderecoInitialIndex + i);
        return endereco;
    }

    public record PostEstoqueDTO(int quantidade){}
    public record PostEstoqueSQLDTO(int enderecoInitialIndex, int quantidade){}
}
