package cornieri.vinicius.turma926.bd.gerador.dados.controller;

import cornieri.vinicius.turma926.bd.gerador.dados.model.Endereco;
import cornieri.vinicius.turma926.bd.gerador.dados.service.DataFakerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/endereco")
@RequiredArgsConstructor
public class EnderecoController {
    @PostMapping(value = "/lote/sql", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String criarLote(@RequestBody PostEnderecoDTO entrada) {
        int quantidade = entrada.quantidade();

        if (quantidade <= 0) {
            throw new ResponseStatusException(400, "quantidade deve ser maior que zero", null);
        }

        if (entrada.initialIndex() <= 0) {
            throw new ResponseStatusException(400, "initial index deve ser maior que zero", null);
        }

        StringBuilder insert = new StringBuilder("insert into endereco(id, cep, logradouro, numero, cidade, uf) values \n");

        String endereco = IntStream.range(0, quantidade)
                                   .mapToObj(i -> this.create(entrada.initialIndex(), i))
                                   .map(Endereco::toValuesSql)
                                   .collect(Collectors.joining(", \n    "));
        insert.append(endereco);
        insert.append(";");
        return insert.toString();
    }

    private Endereco create(int initialIndex, int i) {
        Endereco end = create(i);
        end.setId(initialIndex+i);
        return end;
    }

    public Endereco create(int i) {
        Endereco endereco = new Endereco();
        DataFakerFactory.FakerCustomizado faker = DataFakerFactory.INSTANCE;
        endereco.setCep(faker.address().postcode().replace("-", ""));
        endereco.setLogradouro(faker.address().streetName());
        endereco.setNumero(faker.address().buildingNumber());
        endereco.setCidade(faker.address().cityName());
        endereco.setUf(faker.address().stateAbbr());
        return endereco;
    }


    public record PostEnderecoDTO(int initialIndex, int quantidade){}
}
