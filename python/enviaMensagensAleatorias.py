import MySQLdb
import time
import hl7
import sys
from socket import socket, AF_INET, SOCK_DGRAM, gethostbyname

SERVER_IP   = '25.63.143.128'
PORT_NUMBER = 5000
mySocket = socket( AF_INET, SOCK_DGRAM )

mensagensEnviadas = {}

def mensagemHL7(dataHora, numOrdem, numDoente, morada, telefone, numEpisodio, estado, numPedido, infoClinica, idMensagem):
	mensagem = "MSH|^~\&|BRUNO|BRUNO|MARIA|MARIA|" + str(dataHora) +"||ORM^O"+ str(numOrdem) +"|"+ str(idMensagem) +"|P|2.5|||AL|\r"
	mensagem += "PID|||"+ str(numDoente) +"||||||||"+ morada +"||"+ str(telefone) +"||||||\r"
	mensagem += "PV1||I|CON||||||||||||||||"+ str(numEpisodio) +"|\r"
	mensagem += "ORC|"+ estado +"|"+ str(numPedido)+"|"+ str(numPedido) +"||||||"+ str(dataHora) +"|\r"
	mensagem += "OBR|01|"+ str(numPedido)+"|"+ str(numPedido)+"||||||||||"+ infoClinica +"||||||||||||||||||||\r"
	return mensagem

def geraEnviaMensagensAleatorias(n):
	db = MySQLdb.connect(host="localhost",user="root",passwd="1234",db="pedidos")
	cur = db.cursor()
	try:
		conjuntoMensagens = mensagemHL7("21/04/2018~17:00", 1, 1, "rua desconhecida", 1, 1, "CA", 1, "", 1)
		mensagensEnviadas[str(1)] = conjuntoMensagens
		conjuntoMensagens = conjuntoMensagens + "@@@"
		query = "insert into pedido values (null,%s,%s,%s,%s,%s,%s,%s,%s, %s, null, 1)"
		cur.execute(query,("21/04/2018", "17:00", 1, 1, "rua desconhecida", 1, 1, "CA", ""))
		db.commit()

		i = 2
		while i <= n:
			mensagem = mensagemHL7("21/04/2018~18:00" , i, i, "rua desconhecida", i, i, "CA", i, "", i)
			mensagensEnviadas[str(i)] = mensagem
			conjuntoMensagens = conjuntoMensagens + mensagem
			conjuntoMensagens = conjuntoMensagens + "@@@"
			
			query = "insert into pedido values (null,%s,%s,%s,%s,%s,%s,%s,%s, %s, null, 1)"
			cur.execute(query,("21/04/2018", "18:00", i, i, "rua desconhecida", i, i, "CA", ""))
			db.commit()
			i = i + 1

		if n > 1: 
			time1 = time.time()
			print("tempo inicial:" + str(time1))
			conjuntoMensagens = conjuntoMensagens[:-3]
			mySocket.sendto(conjuntoMensagens.encode('utf-8'),(SERVER_IP,PORT_NUMBER))

	except(MySQLdb.Error, MySQLdb.Warning) as e:
		print(e)
	db.close()

	return time1

def recebeConfirmacao(n):
	print("pronto a receber as confirmações")
	PORT_NUMBE = 5001
	SIZE = 1000000
	hostName = gethostbyname( '0.0.0.0' )
	mySocketACK = socket( AF_INET, SOCK_DGRAM )
	mySocketACK.bind( (hostName, PORT_NUMBE) )
	(data,addr) = mySocketACK.recvfrom(SIZE)
	data = data.decode()
	file1 = open("fafa.txt", "w")
	file1.write(data)
	file1.close()
	msgs = data.split("@@@")
	for m in msgs:
		h = hl7.parse(m)
		msa = h.segment('MSA')
		if str(msa[1]) == "AA":
			mensagensEnviadas.pop(str(msa[2]))

def main():
	nMensagens = input("Insira o número de mensagens a enviar:")
	tempoInicial = geraEnviaMensagensAleatorias(int(nMensagens))
	recebeConfirmacao(nMensagens)
	tempoFinal = time.time()
	elapsed = tempoFinal -tempoInicial
	confirmadas = int(nMensagens) - len(mensagensEnviadas)
	print("Tempo decorrido a enviar e processar mensagens:" +  str(elapsed))
	print("Mensagens confirmadas:" + str(confirmadas))

if __name__ =="__main__":
	main()
