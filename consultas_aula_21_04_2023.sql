
 -- 1. Listar todos os cliente que tem o nome 'ANA'.> Dica: Buscar sobre função Like
select c.*, lower(c.nome) from 
	cliente c 
	where c.nome ilike 'ANA%Lo%';
	
-- 2. Pedidos feitos em 2023
select * from pedido p 
	where p.data_criacao >= '01-01-2023'
		and p.data_criacao < '01-01-2024';
	
select p.*, p.id_cliente from pedido p 
	where p.data_criacao between to_date('01-01-2023', 'DD-MM-YYYY') and to_date('31-12-2023', 'DD-MM-YYYY');

select * from endereco;

-- Listar o nome e cpf dos clientes com o cep, logradouro, numero e cidade de seu endereço;
select c.nome, c.cpf, e.cep, e.logradouro, e.numero, e.cidade, c.id_endereco, e.id from cliente c
	inner join endereco e on c.id_endereco = e.id;

select * from pedido p
	full join cupom c on c.id = p.id_cupom
	where p.id in (945, 946, 947, 949) 
		or p.id is null; 
	

select * from pedido p
	inner join cupom c on c.id = p.id_cupom
	where c.id in (95); 

select * from cupom c, pedido p
	where c.id = p.id_cupom;
-- TOP 10 cupons mais utilizados

select c.descricao, p.id_cupom, count(p.id_cupom) qtd_utilizada
	from pedido p, cupom c
	where
		p.id_cupom = c.id  
	group by p.id_cupom, c.descricao
	order by qtd_utilizada desc limit 10;
	
-- TOP 10 cupons que deram mais descontos
select c.descricao, p.id_cupom, sum(c.valor) "valor total descontado"
	from pedido p, cupom c
	where
		p.id_cupom = c.id  
	group by p.id_cupom, c.descricao 
	order by "valor total descontado" desc limit 10;

-- Listar os cupons que deram mais de 5000 de desconto

select * from (select c.descricao, p.id_cupom, sum(c.valor) "valor total descontado"
	from pedido p, cupom c
	where
		p.id_cupom = c.id  
	group by p.id_cupom, c.descricao 
	order by "valor total descontado" desc) A
	where A."valor total descontado" > 5000;


select c.descricao, p.id_cupom, sum(c.valor) "valor total descontado"
	from pedido p, cupom c
	where
		p.id_cupom = c.id
	group by p.id_cupom, c.descricao 
	having sum(c.valor) > 5000
	order by "valor total descontado" desc;
-- Listagem de fornecedores da Bahia que fornecem o produto com id 15
select f.nome nome_fornecedor, f.cnpj, e.* from fornecedor f
	inner join item_fornecedor ifor on ifor.id_fornecedor = f.id
	inner join produto prod on ifor.id_produto = prod.id 
	inner join endereco e on e.id = f.id_endereco 
	where prod.id = 15 and e.uf = 'BA';

select 'CLIENTE' as tipo, c.nome as responsável, en.* from cliente c
	join endereco en on c.id_endereco = en.id  
	where 
		en.uf = 'ES'
union 		
select 'ESTOQUE' as tipo, est.descricao as responsável, en.* from estoque est
	join endereco en on est.id_endereco = en.id  
	where 
		en.uf = 'ES'
union	
select 'FORNECEDOR' as tipo, f.nome as responsável, en.* from fornecedor f
	join endereco en on f.id_endereco = en.id  
	where 
		en.uf = 'ES';
	