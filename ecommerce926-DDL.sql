

-- DDL - data definition language

drop schema ecommerce926 cascade;

create schema ecommerce926;

set search_path to ecommerce926;

create table produto(
    id serial primary key,
    descricao varchar(1000) not null,
    codigo_barras varchar(44) not null,
    valor numeric not null
);
/* Exemplo sem utilizar o id serial
create sequence tst_produto_id_seq;

create table produto(
    id int primary key default nextval('tst_produto_id_seq'),
    descricao varchar(1000) not null,
    codigo_barras varchar(44) not null,
    valor numeric not null
);

alter sequence tst_produto_id_seq owned by produto.id;
*/
-- drop table produto;

alter table produto add constraint 
  uk_produto_codigo_barras unique(codigo_barras);
 
create table endereco(
    id serial,
    cep char(8) not null,
    logradouro varchar(1000) not null,
    numero varchar(30) not null,
    cidade varchar(200) not null,
    uf char(2) not null,
    constraint pk_id_endereco primary key(id)
);

create table cliente(
	id serial primary key,
	nome varchar(895) not null,
	id_endereco int
);

alter table cliente 
  add column cpf char(11) unique;

 alter table cliente
 	add UNIQUE(id_endereco); 
 
 alter table cliente
	alter column id_endereco set not null; 

 alter table cliente
	alter column cpf set not null; 

alter table cliente
	add foreign key (id_endereco) references endereco(id);

create table pedido (
	id serial primary key,
	previsao_entrega date not null,
	meio_pagamento varchar(200) not null,
	status varchar(100) not null,
	id_cliente int not null,
	data_criacao timestamp not null,
	foreign key(id_cliente) references cliente
);

create table item_pedido(
	id_pedido int not null,
	id_produto int not null,
	quantidade int not null,
	valor numeric not null,
	primary key(id_pedido, id_produto),
	foreign key(id_pedido) references pedido(id),
	foreign key(id_produto) references produto
);


create table cupom ( 
	id serial primary key,
	descricao varchar(1000) not null,
	data_inicio timestamp not null,
	data_expiracao  timestamp  not null,
	valor numeric not null,
	limite_maximo_usos integer
 );

alter table pedido add column id_cupom integer;
alter table pedido add foreign key(id_cupom) references cupom;
alter table pedido add unique(id_cliente,id_cupom);
alter table pedido alter column data_criacao set default CURRENT_TIMESTAMP;

create table item_carrinho(
	id_cliente int not null,
	id_produto int not null,
	quantidade int not null,
	data_insercao timestamp default CURRENT_TIMESTAMP not null,
	foreign key(id_cliente) references cliente,
	foreign key(id_produto) references produto,
	primary key(id_cliente, id_produto)
);

create table fornecedor ( 
	id serial primary key ,
	id_endereco int not null unique references endereco,
	cnpj char(14) not null unique,
	nome varchar(895) not null
 );

create table estoque ( 
	id serial primary key ,
	id_endereco integer not null unique references endereco,
	descricao varchar(1000) not null
 );

create table item_fornecedor(
	id_fornecedor int not null references fornecedor,
	id_produto int not null references produto,
	primary key(id_fornecedor, id_produto)
);

create table item_estoque(
	id_estoque int not null references estoque,
	id_produto int not null references produto,
	quantidade int not null,
	primary key(id_estoque, id_produto)
);






