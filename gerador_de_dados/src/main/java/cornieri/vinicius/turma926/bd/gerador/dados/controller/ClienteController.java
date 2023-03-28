package cornieri.vinicius.turma926.bd.gerador.dados.controller;

import cornieri.vinicius.turma926.bd.gerador.dados.model.Cliente;
import cornieri.vinicius.turma926.bd.gerador.dados.model.Endereco;
import cornieri.vinicius.turma926.bd.gerador.dados.persistence.ClienteRepository;
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
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
@RequestMapping("/cliente")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteRepository clienteRepository;

    private final EnderecoController enderecoController;

    @PostMapping(value = "/lote", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Cliente> criarLote(@RequestBody PostClienteDTO entrada) {
        Stream<Cliente> clientes = generateClienteStream(entrada.quantidade, enderecoController::create);

        return clienteRepository.saveAll(clientes.toList());
    }

    @PostMapping(value = "/lote/sql", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String criarLoteSql(@RequestBody PostClienteSQLDTO entrada) {


        if (entrada.enderecoInitialIndex() <= 0) {
            throw new ResponseStatusException(400, "endereco initial index deve ser maior que zero", null);
        }


        Stream<Cliente> clientes = generateClienteStream(entrada.quantidade(), (i) -> this.createEnderecoSomenteComId(entrada.enderecoInitialIndex(), i));
        String clientesSql = clientes
                           .map(Cliente::toValuesSql)
                           .collect(Collectors.joining(", \n    "));
        StringBuilder insert = new StringBuilder("insert into cliente(nome, cpf, id_endereco) values \n    ");

        insert.append(clientesSql);
        insert.append(";");
        return insert.toString();
    }

    private Stream<Cliente> generateClienteStream(int quantidade, IntFunction<Endereco> enderecoSuplier) {

        if (quantidade <= 0) {
            throw new ResponseStatusException(400, "quantidade deve ser maior que zero", null);
        }


        Stream<Cliente> clientes = IntStream.range(0, quantidade)
                .mapToObj(enderecoSuplier)
                .map(this::createCliente);
        return clientes;
    }

    private Cliente createCliente(Endereco endereco) {
        Cliente cliente = new Cliente();
        DataFakerFactory.FakerCustomizado faker = DataFakerFactory.INSTANCE;
        cliente.setNome(faker.name().firstName() + " " + faker.name().lastName() );
        cliente.setCpf(faker.cpf().valid(false));
        cliente.setEndereco(endereco);
        return cliente;
    }

    private Endereco createEnderecoSomenteComId(int enderecoInitialIndex, int i) {
        Endereco endereco = new Endereco();
        endereco.setId(enderecoInitialIndex + i);
        return endereco;
    }

    public record PostClienteDTO(int quantidade){}
    public record PostClienteSQLDTO(int enderecoInitialIndex, int quantidade){}
}
