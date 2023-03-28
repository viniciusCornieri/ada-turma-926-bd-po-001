package cornieri.vinicius.turma926.bd.gerador.dados.controller;

import cornieri.vinicius.turma926.bd.gerador.dados.model.Cliente;
import cornieri.vinicius.turma926.bd.gerador.dados.model.Cupom;
import cornieri.vinicius.turma926.bd.gerador.dados.model.Pedido;
import cornieri.vinicius.turma926.bd.gerador.dados.model.StatusPedido;
import cornieri.vinicius.turma926.bd.gerador.dados.service.DataFakerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/pedido")
@RequiredArgsConstructor
public class PedidoController {

    @PostMapping(value = "/lote/sql", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String criarLoteSql(@RequestBody PostPedidoSQLDTO entrada) {


        if (entrada.quantidade <= 0) {
            throw new ResponseStatusException(400, "quantidade deve ser maior que zero", null);
        }


        String pedidosSql = IntStream.range(0, entrada.quantidade)
                                     .mapToObj(i -> this.createPedido(entrada.clientes, entrada.cupons))
                                     .distinct()
                                     .map(Pedido::toValuesSql)
                                     .collect(Collectors.joining(", \n    "));

        StringBuilder insert = new StringBuilder();
        insert.append("insert into pedido(id_cliente, id_cupom, status, meio_pagamento, data_criacao, previsao_entrega) values \n    ");
        insert.append(pedidosSql);
        insert.append(";");
        return insert.toString();
    }

    private Pedido createPedido(PostClienteDTO clientes, PostCupomDTO cupons) {
        Pedido pedido = new Pedido();
        DataFakerFactory.FakerCustomizado faker = DataFakerFactory.INSTANCE;
        Cliente cliente = new Cliente();
        Integer clientID = faker.random().nextInt(clientes.initialIndex, clientes.finalIndex);
        cliente.setId(clientID);
        pedido.setCliente(cliente);

        boolean hasCupom = faker.random().nextBoolean();
        if (hasCupom) {
            Cupom cupom = new Cupom();
            Integer cupomId = faker.random().nextInt(cupons.initialIndex, cupons.finalIndex);
            cupom.setId(cupomId);
            pedido.setCupom(cupom);
        }

        pedido.setMeioPagamento(faker.nextMeioPagamentoPedido());
        LocalDateTime randomDate = faker.nextCustomRandomLocalDateTime();
        pedido.setDataCriacao(randomDate);
        pedido.setPrevisaoEntrega(randomDate.plusDays(faker.random().nextInt(5, 20)));
        pedido.setStatus(faker.nextStatusPedido());
        if (randomDate.isBefore(LocalDateTime.now().minusMonths(3))) {
            StatusPedido[] pedidosEncerrados = {StatusPedido.SUCESSO, StatusPedido.CANCELADO};
            pedido.setStatus(pedidosEncerrados[faker.random().nextInt(pedidosEncerrados.length)]);
        }
        return pedido;
    }

    private record PostClienteDTO(Integer initialIndex, Integer finalIndex){}
    private record PostCupomDTO(Integer initialIndex, Integer finalIndex){}
    private record PostPedidoSQLDTO(Integer quantidade, PostClienteDTO clientes, PostCupomDTO cupons){}
}
