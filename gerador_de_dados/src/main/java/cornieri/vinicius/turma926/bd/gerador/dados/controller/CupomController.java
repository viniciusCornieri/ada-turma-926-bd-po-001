package cornieri.vinicius.turma926.bd.gerador.dados.controller;

import cornieri.vinicius.turma926.bd.gerador.dados.model.Cupom;
import cornieri.vinicius.turma926.bd.gerador.dados.persistence.CupomRepository;
import cornieri.vinicius.turma926.bd.gerador.dados.service.DataFakerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
@RequestMapping("/cupom")
@RequiredArgsConstructor
public class CupomController {

    private final CupomRepository clienteRepository;

    @PostMapping(value = "/lote", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Cupom> criarLote(@RequestBody PostCupomDTO entrada) {
        Stream<Cupom> cupons = generateCupomStream(entrada.quantidade);

        return clienteRepository.saveAll(cupons.toList());
    }

    @PostMapping(value = "/lote/sql", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String criarLoteSql(@RequestBody PostCupomSQLDTO entrada) {

        Stream<Cupom> cupons = generateCupomStream(entrada.quantidade());
        String cuponsSql = cupons
                           .map(Cupom::toValuesSql)
                           .collect(Collectors.joining(", \n    "));

        StringBuilder insert = new StringBuilder();
        insert.append("insert into cupom(descricao, valor, data_inicio, data_expiracao, limite_maximo_usos) values \n    ");
        insert.append(cuponsSql);
        insert.append(";");
        return insert.toString();
    }

    private Stream<Cupom> generateCupomStream(int quantidade) {

        if (quantidade <= 0) {
            throw new ResponseStatusException(400, "quantidade deve ser maior que zero", null);
        }


        Stream<Cupom> cupons = IntStream.range(0, quantidade)
                .mapToObj(this::createCupom);
        return cupons;
    }

    private Cupom createCupom(int i) {
        Cupom cupom = new Cupom();
        DataFakerFactory.FakerCustomizado faker = DataFakerFactory.INSTANCE;
        cupom.setValor(BigDecimal.valueOf(faker.number().numberBetween(1, 500)));
        cupom.setDescricao(faker.job().field() + " | " + faker.job().keySkills());
        LocalDateTime randomDate = faker.nextCustomRandomLocalDateTime();
        cupom.setDataInicio(randomDate);
        cupom.setDataExpiracao(randomDate.plusDays(faker.random().nextInt(1, 90)));
        Integer limiteMaximoUsos = faker.random().nextInt(-500, 500);
        if (limiteMaximoUsos > 0) {
            cupom.setLimiteMaximoUsos(limiteMaximoUsos);
        }
        return cupom;
    }

    public record PostCupomDTO(int quantidade){}
    public record PostCupomSQLDTO(int quantidade){}
}
