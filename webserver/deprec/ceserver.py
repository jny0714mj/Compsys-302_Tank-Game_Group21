import cherrypy
import random
import string
import urllib2
import json
import sys
import socket
import time
import hashlib
import threading
from multiprocessing import Process


class CombatEvolvedServer(object):

    def __init__(self, jsender, c_listen_ip, c_listen_port):
        self.jsender = jsender
        self.c_listen_ip = c_listen_ip
        self.c_listen_port = c_listen_port



    #CherryPy Configuration
    _cp_config = {'tools.encode.on': True,
                  'tools.encode.encoding': 'utf-8',
                  'tools.sessions.on' : 'True',
                 }

    ########### API methods ###########
    ###################################
    @cherrypy.expose
    def makePing(self):
        print "pinging other server"
        url = "http://" + o_ip + ":" + str(c_send_port) + "/ping?sender=" + username
        resp = urllib2.urlopen(url)
        print url
        print username

        raise cherrypy.HTTPRedirect('/main')


    @cherrypy.expose
    def ping(self, sender):
        print "I've been pinged!"
        return '0'

    @cherrypy.expose
    def makeChallenge(self):
        params = {
            "sender" : username
        }
        paramJ = json.dumps(params)
        print "making challenge"
        url = "http://" + o_ip + ":" + str(c_send_port) + "/challenge"
        print url
        req = urllib2.Request(url,    #note that localhost doesn't work with urllib2!!
                          paramJ,
                          {'Content-Type':'application/json'})
        urllib2.urlopen(req)

        raise cherrypy.HTTPRedirect('/main')

    @cherrypy.expose
    @cherrypy.tools.json_in()
    def challenge(self):
        """ master invokes this method on the slave server
        """
        expectedParams = ("sender")

        # check input
        # use try except when testing
        try:
            inp = cherrypy.request.json
            print "challenge got good input"
        except:
            inp = {
                "sender" : username
            }

        if "sender" not in inp:
            print "Missing compulsory field"
            return '1'
        print "challenge began by " + inp["sender"]

        # invoke response on master server, with accept or not
        print "you've been challenged! press y to accept"
        raw_input() # wait for input

        ## GOOD CODE FOR REDIRECT
        url = "http://" + o_ip + ":" + str(c_send_port) + "/respond"   #note that localhost doesn't work with urllib2!!
        respondDict = {
            "sender" : username,        #can also try cherrypy.session['username']
            "accept" : 1
        }
        respondJson = json.dumps(respondDict)
        req = urllib2.Request(url,
                              respondJson,
                              {'Content-Type':'application/json'})
        response = urllib2.urlopen(req)

        self.jsender.sendMsgToJava("start game, I'm the slave")
        raise cherrypy.HTTPRedirect('/main')



    @cherrypy.expose
    @cherrypy.tools.json_in()
    def respond(self):
        """ slave (respondent) invokes this method on the master server
        """

        ## GOOD CODE FOR PARAM CHECKING
        expectedParams = ("sender", "accept")   #any iterable object
        # check input
        inp = cherrypy.request.json
        if not all (k in inp for k in expectedParams):
            print "Missing compulsory field"
            return '1'

        print "response received. sent by " + inp["sender"]

        if inp['accept'] == 1:
            print 'response was acceptance!'
            self.jsender.sendMsgToJava("start game, I'm the master")
            return '0'
        else:
            "the slave turned you down, what you gonna do about it"
            return '3' #??

    @cherrypy.expose
    @cherrypy.tools.json_in()
    def receiveKey(self):
        ''' slave invokes this on master if there is a keychange
        '''
        ## GOOD CODE FOR PARAM CHECKING
        expectedParams = ("sender", "keyType", "value")   #any iterable object
        # check input
        inp = cherrypy.request.json
        if not all (k in inp for k in expectedParams):
            print "Missing compulsory field"
            return '1'

        print "I've received key: " + str(inp['keyType']) + " with value: " + inp['value']

        # do something with key
        print "trying to send message"
        self.jsender.sendMsgToJava("0?" + inp['keyType'] + "&" + inp['value'])   #refuses connection

    @cherrypy.expose
    @cherrypy.tools.json_in()
    @cherrypy.tools.json_out()
    def getKey(self):
        ''' master invokes this on slave to force getting a key.
        '''
        return 0

    @cherrypy.expose
    @cherrypy.tools.json_in()
    def receiveObject(self):
        ''' master invokes this on slave
        '''
        ## GOOD CODE FOR PARAM CHECKING
        expectedParams = ("sender", "objectID", "objType", "x", "y", "angle", "state", "alive")   #any iterable object
        # check input
        inp = cherrypy.request.json
        if not all (k in inp for k in expectedParams):
            print "Missing compulsory field"
            return '1'

        print "I've received an object, ID: " + str(inp['objectID']) + " type: " + str(inp['objType'])

        # do something with object
        msg = "1?" + inp['objectID']+'&' + str(inp['objType'])+'&' + inp['x']+'&' + inp['y']+'&' + inp['angle']+'&' + inp['state']+'&' + inp['alive']
        self.jsender.sendMsgToJava(msg)


    ############# TESTING #############
    ###################################
    ## SENDING INFORMATION TO JAVA
    # may be invoked internally, or with http get for testing purposes
    # @cherrypy.expose
    # def sendTestMsgToJava(self, msg):
    #     ''' tests sending a message to java on the same machine.
    #         must ensure that ports align
    #     '''
    #
    #     print "sending message to java: " + msg
    #     sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    #     sock.connect(("localhost", j_send_port))
    #
    #     sock.sendall(msg+"\n")
    #     data = sock.recv(1024)
    #
    #     return 'done'

    @cherrypy.expose
    def showMsg(self, a="notworking"):
        return a

    ############## PAGES ##############
    ###################################
    def hashword(password):
        SALT = "COMPSYS302-2016"
        password = str(password)+SALT
        tohash = hashlib.sha256(password.encode())
        hashes = tohash.hexdigest()
        return (hashes)

    @cherrypy.expose
    def info(self, username, password):
        user = username
        password = self.hashword(password)
        if "10.104" in listen_ip or "10.103" in listen_ip:
            # UG desktop
            location = 0
        elif "172.23" in listen_ip or "127.0" in listen_ip:
            # uni wireless
            location = 1
        else:
            # some other pirvate ip address
            location = 2

        print "I'll report my location as: " + str(location)
        pubkey = 0
        enc = 0
        return username, password, location, pubkey, enc

    @cherrypy.expose
    def listAPI(self):
        #returns the list of API's in the protocol
        Page = '<!DOCTYPE html><html><body background="https://images2.alphacoders.com/248/248300.jpg"></body></html>'
        # menu button
        Page +='<center><html><head><style>.button1 {background-color: white;border: none;color: black; padding:10px 20px;text-align: center;text-decoration: none;display: inline-block;font-size: 15px;cursor: pointer;}.button1:hover {background-color: lightblue;}</style></head><body><button class="button1"><a href="main">Main</a></button><button class="button1"><a href="listAPI">List API</a></button><button class="button1"><a href="AboutUs">About Us</a></button><button class="button1"><a href="FriendList">FriendList</a></button><button class="button1"><a href="signout">LOG OUT</a></button></body></html></center>'
        response = urllib2.urlopen("http://cs302.pythonanywhere.com/listAPI")
        reading = response.read()
        Page += reading
        Page += "<a href='main'>goback</a>.<br/>"
        return Page

    @cherrypy.expose
    def AboutUs(self):
        Page = '<center><!DOCTYPE html><html><body background="https://images2.alphacoders.com/248/248300.jpg"></body></html>'
        Page +='<center><html><head><style>.button1 {background-color: white;border: none;color: black; padding:10px 20px;text-align: center;text-decoration: none;display: inline-block;font-size: 15px;cursor: pointer;}.button1:hover {background-color: lightblue;}</style></head><body><button class="button1"><a href="main">Main</a></button><button class="button1"><a href="listAPI">List API</a></button><button class="button1"><a href="AboutUs">About Us</a></button><button class="button1"><a href="FriendList">FriendList</a></button><button class="button1"><a href="signout">LOG OUT</a></button></body></html></center>'
        Page += '<font size="6" face="calibri">We are Group 21.<br>'
        Page += "Member in Group 21 : Jack McIvor & Nayoung(Monica) Jung </font><br>"
        Page += "<a href='main'>goback</a>.<br/>"
        return Page

    # If they try somewhere we don't know, catch it here and send them to the right place.
    @cherrypy.expose
    def default(self, *args, **kwargs):
        """The default page, given when we don't recognise where the request is for."""
        Page = "I don't know where you're trying to go, so have a 404 Error."
        cherrypy.response.status = 404
        return Page

    ## PAGES (which return HTML that can be viewed in browser)
    @cherrypy.expose
    def index(self):
        Page = '<!DOCTYPE html><html><body background="https://images2.alphacoders.com/248/248300.jpg"></body></html>' #set the background
        Page += '<center><!DOCTYPE html><html><head><style>h1{font-size: 250%;color:white;}</style></head><body><h1>WELCOME TO COMBAT EVOLVED</h1></body></html>'
        Page += '<img src="http://vignette1.wikia.nocookie.net/metalslug/images/e/e0/Darkmetalslug.gif/revision/latest?cb=20111129203504" alt="nyan"width="140" height="140"></img>' #animated tank
        Page += '<center><!DOCTYPE html><html><style>.button{border: none;color: white;padding: 16px 32px;text-align: center;text-decoration: none;display:inline-block;font-family: verdana;font-size: 20px;margin: 4px 2px;-webkit-transition-duration: 0.4s;}.button{background-color: white;color: black;border-radius:12px;border: 2px solid #555555;}.button:hover{background-color:#555555;color: white;}</style><button class="button button"><a href="login">LOG IN</a></button></html>' #created a button that hovers and to login

        #Page += "Click here to <a href='test'>test</a>."
        return Page

    #if log in, get to main page
    @cherrypy.expose
    def main(self):
        Page = '<html><script type="text/JavaScript">function timedRefresh(timeoutPeriod) {setTimeout("location.reload(true);",timeoutPeriod);}window.onload = timedRefresh(10000);</script><body></body></html>' #refresh the main page every 10 seconds
        Page = '<!DOCTYPE html><html><body background="https://images2.alphacoders.com/248/248300.jpg"></body></html>' #set the background image
        Page += '<center><font size="20" color="white">'
        Page += "Hello " + cherrypy.session['username'] + "!<br></font>" # welcoming the user

        #created a menu button
        Page +='<center><html><head><style>.button1 {background-color: white;border: none;color: black; padding:10px 20px;text-align: center;text-decoration: none;display: inline-block;font-size: 15px;cursor: pointer;}.button1:hover {background-color: lightblue;}</style></head><body><button class="button1"><a href="main">Main</a></button><button class="button1"><a href="listAPI">List API</a></button><button class="button1"><a href="AboutUs">About Us</a></button><button class="button1"><a href="FriendList">FriendList</a></button><button class="button1"><a href="signout">LOG OUT</a></button></body></html></center>'

        Page +='<html><head><style>img {    position: absolute;    right: 10px;    bottom: 10px;    z-index: -1;}</style></head><body><img src="http://vignette1.wikia.nocookie.net/metalslug/images/c/c0/Metal_Slug_Tank.gif/revision/latest?cb=20151229141810" width="210" height="158"></body></html>' #has animated tank right bottom of the page

        global oUSERS, oLocation, oIP, oPORT, lenUSERS,o_ip,c_send_port,username

        oUSERS, oLocation, oIP, oPORT, lenUSERS = self.getList() #get information from getList
        global no #number

        #Display the Online User List
        Page += '<br>''<br>''<br>''<br>'
        Page += "<b>Online User List </br><b>"
        Page += '<br>'

        Page +='<!DOCTYPE html><html><style>.button2 {  display: inline-block;  padding: 10px 10px;  font-size: 10px;cursor: pointer;  text-align: center;  text-decoration: none;  outline: none;  color: black;background-color: #e5e5e5; border: none;  border-radius: 15px;  box-shadow: 0 4px #999;  face: arial; margin-left: 10px;  font-family: "Arial";}.button2:hover {background-color: #cccccc}.button2:active {  background-color: #b2b2b2;  box-shadow: 0 5px #666;  transform: translateY(4px);}</style>' #sets the style of button for challenge and ping

        for x in range(0,lenUSERS):
                #Showing user's location
                if(oLocation[x] == "0"):
                    location = "University Desktop"
                elif(oLocation[x] == "1"):
                    location = "University Wireless"
                else:
                    location = "Rest of the world"

                        #Show user their UPI
                Page += '<font size="4" color="white"><br>'+str(oUSERS[x])+'<span></span>'+'IP='+str(oIP[x])+'Port='+str(oPORT[x])+'->'
                Page += '<font size="3" face="calibri" color = "#003366">' + str(location) + '</font>'
                #click buttom to challenge AND ping
                no = x
                Page += '<button class="button2"><a href="makeChallenge" class=button>Challenge</a></button>'
                Page += '<button class="button2"><a href="makePing" class=button>Ping</a></button>'
                Page += '<a href="addF">add Friend</a>'


        o_ip = oIP[no]
        c_send_port = oPORT[no]
        username = oUSERS[no]
        return Page

    @cherrypy.expose
    def addF(self):
        with open("friend.txt","a+") as myfile:
            f_list = myfile.read()
            if (oUSERS[no] not in f_list) and (str(oUSERS[no]) != str(info[0])):
                myfile.write(oUSERS[no]+',')
                print "added friend!"

        raise cherrypy.HTTPRedirect('/main')

    @cherrypy.expose
    def FriendList(self):
        with open("friend.txt","r") as myfile:
            f_list = myfile.read().split(',')
            Page = '<!DOCTYPE html><html><body background="https://images2.alphacoders.com/248/248300.jpg"></body></html>'
            #created a menu button
            Page +='<center><html><head><style>.button1 {background-color: white;border: none;color: black; padding:10px 20px;text-align: center;text-decoration: none;display: inline-block;font-size: 15px;cursor: pointer;}.button1:hover {background-color: lightblue;}</style></head><body><button class="button1"><a href="main">Main</a></button><button class="button1"><a href="listAPI">List API</a></button><button class="button1"><a href="AboutUs">About Us</a></button><button class="button1"><a href="FriendList">FriendList</a></button><button class="button1"><a href="signout">LOG OUT</a></button></body></html></center>'
            Page += '<b> My Friend List<br><br>'
            print oUSERS
            for a in range (0,(len(f_list)-1)):

                if (f_list[a] in oUSERS):
                    Page += '<font size="4"><br>'+str(f_list[a]) + '<span style="color: GREEN">ONLINE</span>'
                else:
                    Page += '<font size="4"><br>'+str(f_list[a]) + '<span style="color: RED">OFFLINE</span>'

            return Page

    @cherrypy.expose
    def test(self): #testing page for refreshing

        a = random.randint(1,100)
        Page = '<html><script type="text/JavaScript">function timedRefresh(timeoutPeriod) {setTimeout("location.reload(true);",timeoutPeriod);}window.onload = timedRefresh(10000);</script><body></body></html>'
        Page += str(a)
        return Page

    @cherrypy.expose
    def getList(self):

        #access to andrew's server and get list of users
        response = urllib2.urlopen("http://cs302.pythonanywhere.com/getList?username=" + str(info[0]) + "&password=" + str(info[1]))

        reading = response.read()
        lists = reading.split()
        myLists = lists[5:]

        #define lists to add
        USERS = []
        LOCATION = []
        IP = []
        PORT = []
        time = []
        howmany = len(myLists)
        #split the components and add it to correct list
        for i in range (0, howmany):
            infos = myLists[i].split(',')
            USERS.append(infos[0])
            LOCATION.append(infos[1])
            IP.append(infos[2])
            PORT.append(infos[3])
        #	whattime = time.strftime('%Y-%m-%d %H:%M:%S', time.gmtime(infos[4]))
        #	time.append(whattime)

        lenUSERS = len(USERS)        #Define lenghts of the UPI list

        return (USERS, LOCATION, IP, PORT, lenUSERS)


    @cherrypy.expose
    def login(self):
        Page = '<!DOCTYPE html><html><body background="https://images2.alphacoders.com/248/248300.jpg"></body></html>'
        Page += '<center><form action="/signin" method="post" enctype="multipart/form-data">'
        Page += '<center><font size ="5">Username: <input type="text" name="username"/><br/>'
        Page += '<center><font size ="5">Password: <input type="password" name="password"/>'
        Page += '<center><input type="submit" value="Login"/></form>'
        return Page

    # LOGGING IN AND OUT
    @cherrypy.expose
    def signin(self, username=None, password=None):
        """Check their name and password and send them either to the main page, or back to the main login screen."""
        global info
        info = self.info(username, password)
        error = self.authoriseUserLogin()
        if (error == 0):
            cherrypy.session['username'] = username
            raise cherrypy.HTTPRedirect('/main')
        else:
            print "bad login details"
            raise cherrypy.HTTPRedirect('/login')

    @cherrypy.expose
    def signout(self):
        """Logs the current user out, expires their session"""
        #log off current user
        response = urllib2.urlopen("http://cs302.pythonanywhere.com/logoff?username=" + str(info[0]) + "&password=" + str(info[1]) + "&location=" + str(info[2]) + "&ip=" + listen_ip + "&port=" + str(c_listen_port) + "&pubkey=" + str(info[3]) + "&enc=" + str(info[4]))
        reading = response.read()
        if(response == 0, "Logged off successfully"):
            print reading
            t.cancel()
            cherrypy.lib.sessions.expire()
        raise cherrypy.HTTPRedirect('/')

    def authoriseUserLogin(self):
        #get info from andrew's site (new one)
        response = urllib2.urlopen("http://cs302.pythonanywhere.com/report?username=" + str(info[0]) + "&password=" + str(info[1]) + "&location=" + str(info[2]) + "&ip=" + listen_ip + "&port=" + str(c_listen_port))

        reading = response.read()
        print reading
        #print "http://cs302.pythonanywhere.com/report?username=" + str(info[0]) + "&password=" + str(info[1]) + "&location=" + str(info[2]) + "&ip=" + listen_ip + "&port=" + str(listen_port) + "&pubkey=" + str(info[3]) + "&enc=" + str(info[4])
        global t
        t = threading.Timer(30.0, self.authoriseUserLogin) # ping every 30 seconds
        t.start()
        #if successfully logged in, return 0 else return 1
        if reading == "0, User and IP logged":
            return 0
        else:
            return 1

def runCombatEvolvedServer(jsender, c_listen_ip, c_listen_port):
    # Create an instance of MainApp and tell Cherrypy to send all requests under / to it. (ie all of them)
    cherrypy.tree.mount(CombatEvolvedServer(jsender, c_listen_ip, c_listen_port), "/")

    # Tell Cherrypy to listen for connections on the configured address and port.
    cherrypy.config.update({'server.socket_host': "0.0.0.0",        #bind to all interfaces
                            'server.socket_port': c_listen_port,    #c_listen_port
                            'engine.autoreload.on': True,
                           })

    # Start the web server
    cherrypy.engine.start()

    # And stop doing anything else. Let the web server take over.
    cherrypy.engine.block()

