package cornieri.vinicius.turma926.bd.gerador.dados.controller;

import cornieri.vinicius.turma926.bd.gerador.dados.model.Fornecedor;
import cornieri.vinicius.turma926.bd.gerador.dados.model.Endereco;
import cornieri.vinicius.turma926.bd.gerador.dados.persistence.FornecedorRepository;
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
@RequestMapping("/fornecedor")
@RequiredArgsConstructor
public class FornecedorController {

    private final FornecedorRepository fornecedorRepository;

    private final EnderecoController enderecoController;

    @PostMapping(value = "/lote", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Fornecedor> criarLote(@RequestBody PostFornecedorDTO entrada) {
        Stream<Fornecedor> fornecedores = generateFornecedorStream(entrada.quantidade, enderecoController::create);

        return fornecedorRepository.saveAll(fornecedores.toList());
    }

    @PostMapping(value = "/lote/sql", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String criarLoteSql(@RequestBody PostFornecedorSQLDTO entrada) {


        if (entrada.enderecoInitialIndex() <= 0) {
            throw new ResponseStatusException(400, "endereco initial index deve ser maior que zero", null);
        }


        Stream<Fornecedor> fornecedors = generateFornecedorStream(entrada.quantidade(), (i) -> this.createEnderecoSomenteComId(entrada.enderecoInitialIndex(), i));
        String fornecedorsSql = fornecedors
                           .map(Fornecedor::toValuesSql)
                           .collect(Collectors.joining(", \n    "));
        StringBuilder insert = new StringBuilder("insert into fornecedor(nome, cnpj, id_endereco) values \n    ");

        insert.append(fornecedorsSql);
        insert.append(";");
        return insert.toString();
    }

    private Stream<Fornecedor> generateFornecedorStream(int quantidade, IntFunction<Endereco> enderecoSuplier) {

        if (quantidade <= 0) {
            throw new ResponseStatusException(400, "quantidade deve ser maior que zero", null);
        }


        Stream<Fornecedor> fornecedores = IntStream.range(0, quantidade)
                .mapToObj(enderecoSuplier)
                .map(this::createFornecedor);
        return fornecedores;
    }

    private Fornecedor createFornecedor(Endereco endereco) {
        Fornecedor fornecedor = new Fornecedor();
        DataFakerFactory.FakerCustomizado faker = DataFakerFactory.INSTANCE;
        fornecedor.setNome(faker.name().firstName() + " " + faker.name().lastName());
        fornecedor.setCnpj(faker.cnpj().valid(false));
        fornecedor.setEndereco(endereco);
        return fornecedor;
    }

    private Endereco createEnderecoSomenteComId(int enderecoInitialIndex, int i) {
        Endereco endereco = new Endereco();
        endereco.setId(enderecoInitialIndex + i);
        return endereco;
    }

    public record PostFornecedorDTO(int quantidade){}
    public record PostFornecedorSQLDTO(int enderecoInitialIndex, int quantidade){}
}
