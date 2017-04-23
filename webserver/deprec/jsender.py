import socket

class JavaSender:

    def __init__(self, j_send_port):
        self.j_send_port = j_send_port

    def sendMsgToJava(self, msg):
            print "sending message to java: " + msg
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.connect(("localhost", self.j_send_port))

            sock.sendall(msg+"\n")
            data = sock.recv(1024)
