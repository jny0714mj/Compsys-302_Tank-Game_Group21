import socket
import json
import urllib2


class JavaListener:
    ''' listens to connections from java and handles them by invoking functions in other's cherrypy server
        runs on it's own thread for concurrent outside facing server and inside facing java listener
    '''

    def __init__(self, jsender):
        self.jsender = jsender
        self.o_ip = Null
        self.c_send_port = null

    def sendListenPort(self, j_listen_port):
        print "telling java where we are listening, port: " + str(j_listen_port)
        msg = "9?" + str(j_listen_port)
        self.jsender.sendMsgToJava(msg)

    ## SENDING FUNCTIONS
    def invokeReceiveKey(self, msgBody):
        # sends a key, invokes /recieveKey on master
        # sender: username, keyType: keyType, value: value, /receiveKey

        # parse arguments and set up parameters for receiving end
        try:
            keyType, value = msgBody.split('&')
        except ValueError:
            print "You haven't given me the correct number of arguments!"
            raise

        params = {
            "sender" : str(info[0]),
            "keyType" : keyType,
            "value" : value
        }

        # send message
        print "sending key: " + str(keyType) + " to " + o_ip + str(c_send_port)

        paramJ = json.dumps(params)
        url = "http://" + o_ip + ":" + str(c_send_port) + "/receiveKey"
        req = urllib2.Request(url,
                              paramJ,
                              {'Content-Type': 'application/json'})
        resp = urllib2.urlopen(req)

    def invokeReceiveObject(self, msgBody):
        # sends a key, invokes /recieveKey on master
        # sender: username, keyType: keyType, value: value, /receiveKey

        # parse arguments and set up parameters for receiving end
        try:
            objectID, objType, x, y, angle, state, alive = msgBody.split('&')
        except ValueError:
            print "You haven't given me the correct number of arguments!"
            raise

        params = {
            "sender" : str(info[0]),
            "objectID" : objectID,
            "objType" : objType,
            "x" : x,
            "y" : y,
            "angle" : angle,
            "state" : state,
            "alive" : alive
        }

        # send message
        print "sending object: " + " to " + o_ip

        paramJ = json.dumps(params)
        url = "http://" + o_ip + ":" + str(c_send_port) + "/receiveObject"
        req = urllib2.Request(url,
                              paramJ,
                              {'Content-Type': 'application/json'})
        resp = urllib2.urlopen(req)

    def echoMessage(self, msgBody):
        msgBody = msgBody.rstrip()  #chomp EOL that comes in msgbody

        print "echoing"
        msg = "8?" + msgBody + "backend"
        self.jsender.sendMsgToJava(msg)

    def getOpposition(self, msgBody):
        try:
            self.o_ip, self.c_send_port = msgBody.split('&')
        except ValueError:
            print "You haven't given me the correct number of arguments!"
            raise

        print


    jMsgHead = {
        # used to invoke a function to deal with java messages
        # usage- jMsgHead[ number ]( function arguments ) eg. keyType
        0 : invokeReceiveKey,       #slave invokes receiveKey on master
        1 : invokeReceiveObject,     #master invokes receiveObject on slave
        8 : echoMessage
    }

    def run(self):
        print "running java listener"
        #listen to local connections from java
        j_listen_ip   = "localhost"
        username = "jmci873" # for testing

        print "setting up j_listen_ip"
        s_listener = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s_listener.bind((j_listen_ip, 0))   #let OS bind to any available port
        j_listen_port = s_listener.getsockname()[1]
        print "j_listen_port is:" + str(j_listen_port)

        # tell java what port we are listening on
        self.sendListenPort(j_listen_port)

        s_listener.listen(5)
        print "listening to java on " + s_listener.getsockname()[0] + ":" + str(j_listen_port)+"\n"

        # listen for connections
        while True:
            conn, addr = s_listener.accept()    #blocking
            print "Got connection from " + addr[0] + ":" + str(addr[1])

            try:
                msg = conn.recv(1024)
                print "Received message from java: " + msg

                try:
                    head, body = msg.split('?')
                except ValueError:
                    print "You haven't given me the correct number of arguments!"
                    raise

                print "recv head is " + head
                head = int(head)
                # head = int(msg[2])  # first char determines what sending function is called. first 2 bytes are useless
                # body = msg[3:]
                #args = string.split(body,'&')   #args are separated by '&' character

                # switch hack. send message to cherrpy server with correct function
                # usage- jMsgHead[ number ]( function arguments in '&' separated string )
                jMsgHead[head](body)

            finally:
                print "closing connection\n"
                conn.close()
                sys.stdout.flush()

