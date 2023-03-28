package cornieri.vinicius.turma926.bd.gerador.dados.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "ecommerce926", name = "endereco")
@Data
@NoArgsConstructor
public class Endereco {
    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="endereco_sequence")
    @SequenceGenerator(name="endereco_sequence", sequenceName="endereco_id_seq", schema = "ecommerce926", allocationSize = 1)
    private Integer id;
    @Column
    private String cep;
    @Column
    private String logradouro;
    @Column
    private String numero;
    @Column
    private String cidade;
    @Column
    private String uf;

    public String toValuesSql() {
        return "(%s, '%s', '%s', '%s', '%s', '%s')".formatted(id, cep, logradouro, numero, cidade, uf);
    }


}
