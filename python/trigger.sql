use mydb;

drop trigger updateEnviar;
drop trigger insertEnviar;

delimiter &

#create trigger updateEnviar
#after update on pedido
#for each row
#begin
#	insert into listaenviar values(new.numPedido, new.estado);
#end &

create trigger insertEnviar
after insert on pedido
for each row
begin
	insert into listaenviar values(new.numPedido, new.estado);
end &

#create trigger insertReceber
#after insert on listareceber
#for each row
#begin
#	update pedido set relatorio = new.relatorio where numPedido= new.numPedido;
#end &

delimiter ;
select *  from pedido;
select count(*) from listaenviar;


insert into pedido values (null, '16-02-2018', '15:34', 1,1,'ah beira mar', 919191435,1,'morreu', null, null, 1);

select pedido.numPedido, data, hora, numDoente, numProcesso, morada, telefone, numEpisodio, tipoEpisodio, infoClinica, estado from pedido 
		inner join listaenviar on listaenviar.numPedido = pedido.numPedido
		where listaenviar.idListaEnviar > 0