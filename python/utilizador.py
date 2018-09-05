import MySQLdb
import os.path

def guardaRelatorio():
	db = MySQLdb.connect(host="localhost",user="root",passwd="1234",db="pedidos")
	cur = db.cursor()
	try:
		numPedido = input("Insira o número do pedido: ")
		cur.execute("select relatorio from pedido where numPedido=%s", [numPedido])
		relatorio = cur.fetchall()
		if relatorio:
			for row in relatorio:
				if row[0] :
					path = "./relatorios/"
					file_name = path + numPedido + ".txt"
					file = open(file_name, "w")
					pedido = "Pedido:" + numPedido + "\n"
					file.write(pedido)
					file.write(row[0])
					file.close()
				else :
					print("Não existe relatório")
		else:
			print("Não existe pedido com esse número")
	except(MySQLdb.Error, MySQLdb.Warning) as e:
		print(e)
	db.close()


def inserePedido():
	db = MySQLdb.connect(host="localhost",user="root",passwd="1234",db="pedidos")
	cur = db.cursor()
	try:
		data = input("Insira a data com o seguinte formato DDMMYYYY :")
		hora = input("Insira a hora com o seguinte formato HHMM :")
		numDoente = input("Insira o número do doente:")
		numProcesso = input("Insira o número do processo:")
		morada = input("Insira a morada:")
		telefone = input("Insira o número de telefone:")
		numEpisodio = input("Insira o número do episódio:")
		tipoEpisodio = input("Insira o tipo de episódio:")
		infoClinica = input("Insira, caso desejável, qualquer informação clínica:")
		if infoClinica == "":
			infoClinica = None
		query = "insert into pedido values (null,%s,%s,%s,%s,%s,%s,%s,%s, %s, null, 1)"
		cur.execute(query,(data, hora, numDoente, numProcesso, morada, telefone, numEpisodio, tipoEpisodio, infoClinica))
		db.commit()
		print("O pedido foi inserido com sucesso.")
	except(MySQLdb.Error, MySQLdb.Warning) as e:
		print(e)
	db.close()

def updatePedido():
	db = MySQLdb.connect(host="localhost",user="root",passwd="1234",db="pedidos")
	virgula = False
	contador = 0
	par = list()
	cur = db.cursor()
	try:
		query = "update pedido set "
		numPedido = input("Insira o número do pedido a modificar: ")
		cur.execute("select * from pedido where numPedido=%s", [numPedido])
		lista = cur.fetchall()
		if lista:
			print("De seguida, insira os parâmetros que deseja modificar, ou clique Enter se não quiser alterar esse parâmetro.")
			data = input("Insira a data com o seguinte formato DD-MM-YYYY :")
			hora = input("Insira a hora com o seguinte formato HH:MM :")
			morada = input("Insira a morada:")
			telefone = input("Insira o número de telefone:")
			tipoEpisodio = input("Insira o tipo de episódio:")
			infoClinica = input("Insira qualquer informação clínica:")
			if data:
				virgula = True
				contador = contador + 1
				query = query + " data=%s "
				par.insert(contador, data)
			if hora:
				contador = contador + 1
				if virgula == True:
					query = query + ","
				query = query + " hora=%s "
				virgula = True
				par.insert(contador, hora)
			if morada:
				contador = contador + 1
				if virgula == True:
					query = query + ","
				query = query + " morada=%s "
				virgula = True
				par.insert(contador, morada)
			if telefone:
				contador = contador + 1
				if virgula == True:
					query = query + ","
				query = query + " telefone=%s"
				virgula = True
				par.insert(contador, telefone)
			if tipoEpisodio:
				contador = contador + 1
				if virgula == True:
					query = query + ","
				query = query + " tipoEpisodio=%s"
				virgula = True
				par.insert(contador, tipoEpisodio)
			if infoClinica:
				contador = contador + 1
				if virgula == True:
					query = query + ","
				query = query + " infoClinica=%s"
				virgula = True
				par.insert(contador, infoClinica)
				
			query = query + " , estado = 2 where numPedido=%s"

			if contador == 1:
				cur.execute(query, (par[0], numPedido))
			elif contador == 2:
				cur.execute(query, (par[0], par[1], numPedido))
			elif contador == 3:
				cur.execute(query, (par[0], par[1], par[2], numPedido))
			elif contador == 4:
				cur.execute(query, (par[0], par[1], par[2], par[3], numPedido))
			elif contador == 5:
				cur.execute(query, (par[0], par[1], par[2], par[3], par[4], numPedido))
			elif contador == 6:
				cur.execute(query, (par[0], par[1], par[2], par[3], par[4], par[5], numPedido))
			elif contador == 0:
				print("Não foram efetuadas quaisquer alterações")
			if contador != 0 :
				print("Os parâmetros foram alterados com sucesso")
			db.commit()

			upd = "insert into listaenviar values(null, %s, %s) "
			cur.execute(upd, (numPedido, 2))
			db.commit()
		else:
			print("O número de pedido não existe.")
		
	except(MySQLdb.Error, MySQLdb.Warning) as e:
		print(e)
	db.close()

def cancelaPedido():
	db = MySQLdb.connect(host="localhost",user="root",passwd="1234",db="pedidos")
	cur = db.cursor()
	try:
		numPedido = input("Insira o número do pedido a cancelar: ")
		cur.execute("select * from pedido where numPedido=%s", [numPedido])
		lista = cur.fetchall()
		if lista:
			update = "update pedido set estado=3 where numPedido = %s"
			cur.execute(update,[numPedido])
			db.commit()
			upd = "insert into listaenviar values(null, %s, %s) "
			cur.execute(upd, (numPedido, 3))
			db.commit()
			print("O pedido foi cancelado com sucesso.")
		else:
			print("O número de pedido não existe.")
	except(MySQLdb.Error, MySQLdb.Warning) as e:
		print(e)
	db.close()

def main():
	while True:
		print("=========================")
		print("Insira a opção desejada:")
		print("1:Inserir pedido")
		print("2:Modificar pedido")
		print("3:Cancelar pedido")
		print("4:Guardar relatório")
		print("0:Sair")
		print("=========================")
		opcao = input("Opção:")

		if opcao == '0':
			break;
		if opcao == "1":
			inserePedido()
		elif opcao == "2":
			updatePedido()
		elif opcao == "3":
			cancelaPedido()
		elif opcao == "4":
			guardaRelatorio()
		elif True:
			print("Opção inválida")

if __name__ =="__main__":
	main()