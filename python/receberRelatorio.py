from socket import socket, gethostbyname, AF_INET, SOCK_DGRAM
import sys
import MySQLdb
import hl7

def enviaConfirmacao(msh):
	server_ip = '25.63.143.128'
	port_number = 5004

	replySocket = socket(AF_INET, SOCK_DGRAM)
	print(msh[10])
	idMensagem = msh[10]
	msh1 = "MSH|^~\&|BRUNO|BRUNO|MARIA|MARIA|" + str(msh[6]) +"||ORM^O"+ str(msh[8]) +"|"+ str(msh[10]) +"|P|2.5|||AL|\r"
	msa = "MSA|AA|"+ str(idMensagem) +"\r"
	myMessage = msh1 + msa
	replySocket.sendto(myMessage.encode('utf-8'), (server_ip, port_number))

def recebeRelatorio():
	hostName = gethostbyname( '0.0.0.0' )
	PORT_NUMBER = 5003
	SIZE = 10000

	mySocket = socket( AF_INET, SOCK_DGRAM )
	mySocket.bind( (hostName, PORT_NUMBER) )

	print ("Test server listening on port {0}\n".format(PORT_NUMBER))

	while True:
		(data,addr) = mySocket.recvfrom(SIZE)
		data = data.decode()
		print(data)
		h = hl7.parse(data)
		file =open("novo.txt", "w")
		file.write(data)
		file.close()
		msh = h.segment('MSH')
		enviaConfirmacao(msh)
		numPedido = int(str(h[3][2]))
		estado = str(h[3][1])
		if(estado == "NW"):
			estado = '1'
		elif estado == "XO":
			estado = '2'
		elif estado == "CA":
			estado = '3'


		db = MySQLdb.connect(host="localhost", user="root", passwd="1234", db="pedidos")
		cur = db.cursor()
		try:
			cur.execute("select * from pedido where numPedido=%s", [numPedido])
			lista = cur.fetchall()
			if lista:
				upd = "insert into listareceber values(null, %s, %s, %s) "
				cur.execute(upd, (numPedido, estado, data))
				db.commit()
				update = "update pedido set relatorio = %s where numPedido=%s"
				cur.execute(update, (data, numPedido))
				db.commit()
			else:
				print("O pedido associado a esse número de pedido não existe")
		except(MySQLdb.Error, MySQLdb.Warning) as e:
			print(e)
		db.close()		
	sys.exit()

def main():
	recebeRelatorio()

if __name__=="__main__":
	main()