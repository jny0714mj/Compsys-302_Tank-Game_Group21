import sys
import socket
from multiprocessing import Process

import jsender
import jlistener
import ceserver

if __name__ == "__main__":


    #my_ip = socket.gethostbyname(socket.gethostname())
    o_ip  = socket.gethostbyname(socket.gethostname())

    # c_listen_port    #used to listen for connections from outside cherry server
    # c_send_port     #used to connect/ send to outside cherry server
    # j_send_port       #used to connect/ send to java on same machine
    # j_listen_port     #used to lsiten for connections from java on same machine (OS chooses)

    #j_send_port = 14001 # must be the same in java
    #j_listen_port is allocated by OS

    # would normally get o_ip and o_cherry_port from the login list

    # The address we listen for connections on
    listen_ip = str(socket.gethostbyname(socket.gethostname()))   # listen on local ip address
    #listen_ip = "0.0.0.0"      # listen on all interfaces
    print "hello, i'm python script " + sys.argv
    print "my local ip: " + listen_ip

    username = "jmci873"

    print sys.argv

    if len(sys.argv) == 4:
        print "Good, i wanted 4 args"
        j_send_port = int(sys.argv[1])
        c_listen_port = int(sys.argv[2])
        c_send_port = int(sys.argv[3])
    elif len(sys.argv) == 2:
        print "I'll use def ports to listen and send to"
        j_send_port = int(sys.argv[1])
        c_listen_port = 12000
        c_send_port = 12001
    else:
        print "I don't like it, but I'll use default ports"
        j_send_port = 11000
        c_listen_port = 12000
        c_send_port = 12001

    print "http://"+str(listen_ip)+":"+str(c_listen_port) #prints out the url link

    print "cherrypy is listening on: " + str(c_listen_port)
    print "i'll send methods to another cherrypy at: " + str(c_send_port)
    print "java port to send to: " + str(j_send_port)
    print "i dynamically allocate a port to listen to java on. wait to hear it"


    jSender = jsender.JavaSender(j_send_port)
    jListener = jlistener.JavaListener(jsender)    # pass it jsender
    #jListener.run()
    #ceServer = ceserver.runCombatEvolvedServer()



    # run cherrypy server and java listener concurrently
    print "starting mainApp"
    Process(target = ceserver.runCombatEvolvedServer, args=(jsender, listen_ip, c_listen_port)).start()
    print "starting jListener"
    Process(target = jListener.run).start()
    print "processes started"
