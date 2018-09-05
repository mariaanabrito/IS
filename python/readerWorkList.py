import MySQLdb
import time
import hl7
import sys
from socket import socket, AF_INET, SOCK_DGRAM, gethostbyname

mensagensEnviadas = {}

SERVER_IP   = '25.74.213.245'
PORT_NUMBER = 5000
mySocket = socket( AF_INET, SOCK_DGRAM )

def mensagemHL7(dataHora, numOrdem, numDoente, morada, telefone, numEpisodio, estado, numPedido, infoClinica, idMensagem):
	mensagem = "MSH|^~\&|BRUNO|BRUNO|MARIA|MARIA|" + dataHora +"||ORM^O"+ str(numOrdem) +"|"+ str(idMensagem) +"|P|2.5|||AL|\r"
	mensagem += "PID|||"+ str(numDoente) +"||||||||"+ morada +"||"+ str(telefone) +"||||||\r"
	mensagem += "PV1||I|CON||||||||||||||||"+ str(numEpisodio) +"|\r"
	mensagem += "ORC|"+ estado +"|"+ str(numPedido)+"|"+ str(numPedido) +"||||||"+ dataHora +"|\r"
	mensagem += "OBR|01|"+ str(numPedido)+"|"+ str(numPedido)+"||||||||||"+ infoClinica +"||||||||||||||||||||\r"
	return mensagem

def leMensagens(idMensagensAEnviar):
	db = MySQLdb.connect(host="localhost",user="root",passwd="1234",db="pedidos")
	cur = db.cursor()
	try:
		select = "select pedido.numPedido, data, hora, numDoente, numProcesso, morada, telefone, numEpisodio, tipoEpisodio, infoClinica, listaenviar.estado from pedido "
		select += " inner join listaenviar on listaenviar.numPedido = pedido.numPedido"
		select += " where listaenviar.idListaEnviar > %s"
		
		cur.execute(select, [idMensagensAEnviar])
		lista = cur.fetchall()
		if cur.rowcount > 0:
			for row in lista:
				estado = ""
				infoClinica = row[9]
				if infoClinica is None:
					infoClinica = ""
				idMensagensAEnviar+=1
				if row[10] == 1:
					estado= "NW"
				elif row[10] == 2:
					estado= "XO"
				elif row[10] == 3:
					estado= "CA"
				if row[9] is not None:
					infoClinica = row[9]
				mensagem = mensagemHL7(row[1]+"~"+row[2], idMensagensAEnviar, row[3],row[5],row[6],row[7],estado, row[0], infoClinica, idMensagensAEnviar)
				mySocket.sendto(mensagem.encode('utf-8'),(SERVER_IP,PORT_NUMBER))

				mensagensEnviadas[str(idMensagensAEnviar)] = str(row[0]) #row[0] é o numPedido

				recebeConfirmacao()
	except(MySQLdb.Error, MySQLdb.Warning) as e:
		print(e)
	db.close()
	return idMensagensAEnviar

def recebeConfirmacao():
	PORT_NUMBE = 5001
	SIZE = 1024
	hostName = gethostbyname( '0.0.0.0' )
	mySocketACK = socket( AF_INET, SOCK_DGRAM )
	mySocketACK.bind( (hostName, PORT_NUMBE) )
	(data,addr) = mySocketACK.recvfrom(SIZE)
	data = data.decode()
	print(data)
	h = hl7.parse(data)
	msa = h.segment('MSA')
	print(mensagensEnviadas)
	if str(msa[1]) == "AA":
		print("recebi")
		mensagensEnviadas.pop(str(msa[2]))
	print(mensagensEnviadas)

def idMinimo():
	db = MySQLdb.connect(host="localhost",user="root",passwd="1234",db="pedidos") 
	idMinimo = 0
	try:
		cur = db.cursor()
		query = "select min(idListaEnviar) from listaEnviar"
		cur.execute(query)
		idMin = cur.fetchall()
		if idMin:
			for row in idMin:
				if row[0]:
					idMinimo = row[0]
	except(MySQLdb.Error, MySQLdb.Warning) as e:
		print(e)
	
	db.close()
	return idMinimo;

def main():
	idMensagensAEnviar = 0
	while idMensagensAEnviar == 0:
		idMensagensAEnviar = idMinimo()

	idMensagensAEnviar -= 1
	
	while True:
		idMensagensAEnviar = leMensagens(idMensagensAEnviar)
		print(idMensagensAEnviar)
		time.sleep(5)
	sys.exit()

if __name__ =="__main__":
	main()
