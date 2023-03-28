package cornieri.vinicius.turma926.bd.gerador.dados.service;

import cornieri.vinicius.turma926.bd.gerador.dados.model.MeioPagamento;
import cornieri.vinicius.turma926.bd.gerador.dados.model.StatusPedido;
import net.datafaker.Faker;
import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;

public class DataFakerFactory {

    public static final FakerCustomizado INSTANCE = new FakerCustomizado(new Locale("pt-BR"));
    public static class Produtos extends AbstractProvider<BaseProviders> {
        private static final String[] ADJETIVOS = new String[]{"pequeno", "ergonômico", "rústico", "inteligente",
                "lindo", "incrível", "fantástico", "prático", "elegante",
                "impressionante", "enorme", "medíocre", "metafórico", "resistente",
                "leve", "aerodinâmico", "durável", "sensacional", "supimpa", "meia boca", "quebrado",
                "indispensável", "invisível"};
        private static final String[] MATERIAIS = new String[]{"aço", "madeira", "concreto", "plástico",
                "algodão", "granito", "borracha", "couro", "seda", "lã", "linho", "mármore",
                "ferro", "bronze", "cobre", "alumínio", "papel", "kriptonita", "adamantium", "meteorito",
                "chocolate", "vento", "Grayskull", "Mordor"};
        private static final String[] PRODUCTS = new String[]{"Cadeira", "Carro", "Computador",
                "Caneta", "Mouse", "Camisa", "Mesa", "Celular", "Chapéu", "Prato",
                "Faca", "Garrafa", "Casaco", "Lâmpada", "Teclado", "Bolsa",
                "Banco", "Relógio", "Livro", "Carteira", "Anel", "Espada"};

        public Produtos(BaseProviders faker) {
            super(faker);
        }

        public String nextProductName() {
            String produto = PRODUCTS[faker.random().nextInt(PRODUCTS.length)];
            String adjetivo = ADJETIVOS[faker.random().nextInt(ADJETIVOS.length)];
            String material = MATERIAIS[faker.random().nextInt(MATERIAIS.length)];

            if (produto.endsWith("a") && adjetivo.endsWith("o")) {
                adjetivo = adjetivo.replaceAll("o$", "a");
            }
            return "%s %s de %s".formatted(produto, adjetivo, material);
        }
    }

    public static class FakerCustomizado extends Faker {

        public FakerCustomizado(Locale locale) {
            super(locale);
        }

        public Produtos produtos() {
            return getProvider(Produtos.class, Produtos::new, this);
        }

        public LocalDateTime nextCustomRandomLocalDateTime() {
            Date limiteInferior = Date.from(ZonedDateTime.now().minusYears(1L).toInstant());
            Date limiteSuperior = Date.from(ZonedDateTime.now().plusMonths(1L).toInstant());
            return this.date().between(limiteInferior, limiteSuperior)
                                            .toInstant()
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDateTime();
        }

        public StatusPedido nextStatusPedido() {
            return StatusPedido.values()[this.random().nextInt(StatusPedido.values().length)];
        }

        public MeioPagamento nextMeioPagamentoPedido() {
            return MeioPagamento.values()[this.random().nextInt(MeioPagamento.values().length)];
        }
    }
}
