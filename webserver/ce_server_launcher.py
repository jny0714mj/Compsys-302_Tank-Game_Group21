"""
    Starts a cherrypy server to communicate between games in 2016 compsys project
    Also implements our team's form of java/python IPC communication between the game and webserver
    Takes system arguments- run this from two separate terminals to get interapp communication
"""
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
print sys.argv
print "my local ip: " + listen_ip

# for testing
username = "jmci873"

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

print "http://"+str(listen_ip)+":"+str(c_listen_port) #prints out the url link so that java can open browser

print "cherrypy is listening on: " + str(c_listen_port)
print "i'll send methods to another cherrypy at: " + str(c_send_port)
print "java port to send to: " + str(j_send_port)
print "i dynamically allocate a port to listen to java on. wait to hear it"

class MainApp(object):

    #CherryPy Configu#ration
    _cp_config = {'tools.encode.on': True,
                  'tools.encode.encoding': 'utf-8',
                  'tools.sessions.on' : 'True',
                 }

    def hashword(self, password):
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
        Page += "Members in Group 21 : Jack McIvor & Nayoung(Monica) Jung </font><br>"
        Page += "<a href='main'>goback</a>.<br/>"
        return Page

    ####################################
    ## INTER-APP API METHODS
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
        # check I've received good input
        expectedParams = ("sender")
        inp = cherrypy.request.json

        if "sender" not in inp:
            print "Missing compulsory field"
            return '1'

        print "I've been pinged!"
        return '0'

    @cherrypy.tools.json_in()
    @cherrypy.expose
    def rules(self):
        # check I've received good input
        expectedParams = ("sender")
        inp = cherrypy.request.json

        if "sender" not in inp:
            print "Missing compulsory field"
            return '1'

        return "Tank rotates and shoots once per button press- so you can mash the keys old school \n" \
               "Bullets bounce off walls by rotating their hitbox at a complementary angle to incidence \n" \
               "10 powerups spawn at random times. they have the same icon, and do not affect the player's sprite \n" \
               "Walls spawn at the start of the game on loading, and no other time."

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

        # TODO:: input() used for teseting. should actually be html button
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

        print "start game, i'm the slave"
        sendMsgToJava("10?start&slave")
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
            print "start game, i'm the master"
            sendMsgToJava("10?start&master")
            return '0'  # success
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
        sendMsgToJava("0?" + inp['keyType'] + "&" + inp['value'])   #refuses connection
        return '0'  #success

    @cherrypy.expose
    @cherrypy.tools.json_in()
    def receiveObject(self):
        ''' master invokes this on slave
        '''

        expectedParams = ("sender", "objectID", "objType", "x", "y", "angle", "state", "alive")   #any iterable object
        # check input
        inp = cherrypy.request.json
        if not all (k in inp for k in expectedParams):
            print "Missing compulsory field"
            return '1'

        print "I've received an object, ID: " + str(inp['objectID']) + " type: " + str(inp['objType'])

        # do something with object
        msg = "1?" + inp['objectID']+'&' + str(inp['objType'])+'&' + inp['x']+'&' + inp['y']+'&' + inp['angle']+'&' + inp['state']+'&' + inp['alive']
        sendMsgToJava(msg)
        return '0'  #success

    @cherrypy.expose
    @cherrypy.tools.json_in()
    def getKey(self):
        ''' master invokes this on slave to force getting a key.
        '''
        expectedParams = ("sender", "keyType")   #any iterable object
        # check input
        inp = cherrypy.request.json
        if not all (k in inp for k in expectedParams):
            print "Missing compulsory field"
            return '1'

        print "I'm getting a key, ID: " + " type: " + str(inp['keyType'])

        # do something with object
        msg = "2?" + inp['keyType']
        sendMsgToJava(msg)
        # response from java is from invokeReceiveKey
        return '0'  #success

    @cherrypy.expose
    @cherrypy.tools.json_in()
    def getObject(self):
        ''' slave invokes this on master to force getting an object.
        '''
        expectedParams = ("sender", "objectID")   #any iterable object
        # check input
        inp = cherrypy.request.json
        if not all (k in inp for k in expectedParams):
            print "Missing compulsory field"
            return '1'

        print "I'm getting an object, ID: " + " type: " + str(inp['objectId'])

        # do something with object
        msg = "2?" + inp['objectID']
        sendMsgToJava(msg)
        # reponse from java is from invokeReceiveObject
        return '0'  #success

    @cherrypy.expose
    @cherrypy.tools.json_in()
    def getExistingObjects(self):
        ''' slave invokes this on master to force getting an object.
        '''
        expectedParams = ("sender")   #any iterable object
        # check input
        inp = cherrypy.request.json
        if not all (k in inp for k in expectedParams):
            print "Missing compulsory field"
            return '1'

        print "I'm getting objects, ID: " + " type: " + str(inp['sender'])

        # do something with object
        msg = "4?"
        sendMsgToJava(msg)
        # reponse from java is from invokeReceiveObject
        return '0'  #success

    @cherrypy.expose
    def timeLeft(self):
        ''' slave invokes this on master to force getting an object.
        '''
        expectedParams = ("sender")   #any iterable object
        # check input
        inp = cherrypy.request.json
        if not all (k in inp for k in expectedParams):
            print "Missing compulsory field"
            return '1'

        print "I'm getting time left, ID: " + " type: " + str(inp['keyType'])

        # do something with object
        msg = "5?"
        sendMsgToJava(msg)
        # reponse from java
        return '0'  #success

    @cherrypy.expose
    def timeLeft(self):
        ''' slave invokes this on master to force getting an object.
        '''
        expectedParams = ("sender")   #any iterable object
        # check input
        inp = cherrypy.request.json
        if not all (k in inp for k in expectedParams):
            print "Missing compulsory field"
            return '1'

        print "I'm getting time left"

        # do something with object
        msg = "5?"
        sendMsgToJava(msg)
        # reponse from java
        return '0'  #success

    @cherrypy.expose
    def gameState(self):
        ''' slave invokes this on master to force getting an object.
        '''
        expectedParams = ("sender")   #any iterable object
        # check input
        inp = cherrypy.request.json
        if not all (k in inp for k in expectedParams):
            print "Missing compulsory field"
            return '1'

        print "I'm getting gamestate"

        # do something with object
        msg = "6?"
        sendMsgToJava(msg)
        # reponse from java
        return '0'  #success

    @cherrypy.expose
    def setState(self):
        ''' slave invokes this on master to force getting an object.
        '''
        expectedParams = ("sender", "state")   #any iterable object
        # check input
        inp = cherrypy.request.json
        if not all (k in inp for k in expectedParams):
            print "Missing compulsory field"
            return '1'

        print "I'm setting gamestate"

        # do something with object
        msg = "7?"
        sendMsgToJava(msg)
        # reponse from java
        return '0'  #success

    @cherrypy.expose
    def setScore(self):
        ''' slave invokes this on master to force getting an object.
        '''
        expectedParams = ("sender", "masterScore")   #any iterable object
        # check input
        inp = cherrypy.request.json
        if not all (k in inp for k in expectedParams):
            print "Missing compulsory field"
            return '1'

        print "I'm setting the score"

        # do something with object
        msg = "9?"
        sendMsgToJava(msg)
        # reponse from java
        return '0'  #success

    ####################################
    ## SENDING INFORMATION TO JAVA
    # may be invoked internally, or with http get for testing purposes
    @cherrypy.expose
    def sendTestMsgToJava(self, msg):
        ''' tests sending a message to java on the same machine.
            must ensure that ports align
        '''

        print "sending message to java: " + msg
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.connect(("localhost", j_send_port))

        sock.sendall(msg+"\n")
        data = sock.recv(1024)

        return 'done'

    ####################################
    ## SOME METHODS FOR TESTING
    @cherrypy.expose
    def adder(self, a, b):
        return str(int(a) + int(b))

    @cherrypy.expose
    def showMsg(self, a="notworking"):
        return a

    @cherrypy.expose
    def generate(self):
        return ''.join(random.sample(string.hexdigits, 8))

    ####################################
    # PAGES
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

def sendMsgToJava(msg):
        print "sending message to java: " + msg
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.connect(("localhost", j_send_port))

        sock.sendall(msg+"\n")
        data = sock.recv(1024)


def runMainApp():
    # Create an instance of MainApp and tell Cherrypy to send all requests under / to it. (ie all of them)
    cherrypy.tree.mount(MainApp(), "/")

    # Tell Cherrypy to listen for connections on the configured address and port.
    cherrypy.config.update({'server.socket_host': "0.0.0.0",        #bind to all interfaces
                            'server.socket_port': c_listen_port,    #c_listen_port
                            'engine.autoreload.on': True,
                           })
    
    # Start the web server
    cherrypy.engine.start()

    # And stop doing anything else. Let the web server take over.
    cherrypy.engine.block()

def javaListener():
    ''' listens to connections from java and handles them by invoking functions in other's cherrypy server
        runs on it's own thread for concurrent outside facing server and inside facing java listener
    '''

    print "running java listener"
    #listen to local connections from java
    j_listen_ip   = "localhost"
    username = "jmci873" # for testing

    ## INVOKE METHODS- CALLS EXPOSED URL ON OTHER CHERRYPY SERVER
    def invokePing():
        print "pinging client"
        url = "http://" + o_ip + ":" + str(c_send_port) + "/ping?sender=" + "user at " + listen_ip + ":" + str(c_listen_port)
        resp = urllib2.urlopen(url)

    def invokeReceiveKey(msgBody):
        # sends a key, invokes /recieveKey on master
        # sender: username, keyType: keyType, value: value, /receiveKey

        # parse arguments and set up parameters for receiving end
        try:
            keyType, value = msgBody.split('&')
        except ValueError:
            print "You haven't given me the correct number of arguments!"
            raise

        params = {
            "sender" : "user at " + listen_ip + ":" + str(c_listen_port),
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

    def invokeReceiveObject(msgBody):
        # sends an object, invokes /receiveObject on slave
        # sender: username, keyType: keyType, value: value, /receiveKey

        # parse arguments and set up parameters for receiving end
        try:
            objectID, objType, x, y, angle, state, alive = msgBody.split('&')
        except ValueError:
            print "You haven't given me the correct number of arguments!"
            raise

        params = {
            "sender" : "user at " + listen_ip + ":" + str(c_listen_port),
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

    def invokeGetKey(msgBody):
        # runs /getKey on slave

        # parse arguments and set up parameters for receiving end
        keyType = msgBody

        params = {
            "sender" : "user at " + listen_ip + ":" + str(c_listen_port),
            "keyType" : keyType,
        }

        # send message
        print "getting key: " + str(keyType) + " to " + o_ip + str(c_send_port)

        paramJ = json.dumps(params)
        url = "http://" + o_ip + ":" + str(c_send_port) + "/getKey"
        req = urllib2.Request(url,
                              paramJ,
                              {'Content-Type': 'application/json'})
        resp = urllib2.urlopen(req)

    def invokeGetObject(msgBody):
        # runs /getObject on client

        # parse arguments and set up parameters for receiving end
        objectId = msgBody

        params = {
            "sender" : "user at " + listen_ip + ":" + str(c_listen_port),
            "objectID" : objectId,
        }

        # send message
        print "getting object: " + " from " + o_ip

        paramJ = json.dumps(params)
        url = "http://" + o_ip + ":" + str(c_send_port) + "/getObject"
        req = urllib2.Request(url,
                              paramJ,
                              {'Content-Type': 'application/json'})
        resp = urllib2.urlopen(req)

    def invokeGetExistingObjects():
        # runs /getExistingObjects on client

        url = "http://" + o_ip + ":" + str(c_send_port) + "/getExistingObjects?sender=" + "user at " + listen_ip + ":" + str(c_listen_port)
        resp = urllib2.urlopen(url)

    def invokeTimeLeft():
        # runs /timeLeft on client

        url = "http://" + o_ip + ":" + str(c_send_port) + "/timeLeft?sender=" + "user at " + listen_ip + ":" + str(c_listen_port)
        resp = urllib2.urlopen(url)

    def invokeGameState():
        # runs /gameState on client

        url = "http://" + o_ip + ":" + str(c_send_port) + "/gameState?sender=" + "user at " + listen_ip + ":" + str(c_listen_port)
        resp = urllib2.urlopen(url)

    def invokeSetState(msgBody):
        # runs /getObject on client

        # parse arguments and set up parameters for receiving end
        state = msgBody

        params = {
            "sender" : "user at " + listen_ip + ":" + str(c_listen_port),
            "state" : state,
        }

        # send message
        print "setting state: " + " at " + o_ip

        paramJ = json.dumps(params)
        url = "http://" + o_ip + ":" + str(c_send_port) + "/setState"
        req = urllib2.Request(url,
                              paramJ,
                              {'Content-Type': 'application/json'})
        resp = urllib2.urlopen(req)

    def invokeSetScore(msgBody):
        # runs /getObject on client

        # parse arguments and set up parameters for receiving end
        masterScore = msgBody

        params = {
            "sender" : "user at " + listen_ip + ":" + str(c_listen_port),
            "masterScore" : masterScore,
        }

        # send message
        print "setting score: " + " at " + o_ip

        paramJ = json.dumps(params)
        url = "http://" + o_ip + ":" + str(c_send_port) + "/setScore"
        req = urllib2.Request(url,
                              paramJ,
                              {'Content-Type': 'application/json'})
        resp = urllib2.urlopen(req)


    ## OTHER FUNCTIONS JAVA MAY WANT TO CALL
    def sendListenPort(j_listen_port):
        print "telling java where we are listening, port: " + str(j_listen_port)
        msg = "9?" + str(j_listen_port)
        sendMsgToJava(msg)

    def echoMessage(msgBody):
        msgBody = msgBody.rstrip()  #chomp EOL that comes in msgbody

        print "echoing"
        msg = "8?" + msgBody + "backend"
        sendMsgToJava(msg)

    def getOpposition(msgBody):
        try:
            ip, port = msgBody.split('&')
        except ValueError:
            print "You haven't given me the correct number of arguments!"
            raise

    # message format as per agreed open protocol with java
    jMsgHead = {
        # used to invoke a function to deal with java messages
        # usage- jMsgHead[ number ]( function arguments ) eg. keyType
        0 : invokeReceiveKey,        #slave invokes receiveKey on master
        1 : invokeReceiveObject,     #master invokes receiveObject on slave
        2 : invokeGetKey,
        3 : invokeGetObject,
        4 : invokeGetExistingObjects,
        5 : invokeTimeLeft,
        6 : invokeGameState,
        7 : invokeSetState,
        9 : invokeSetScore,
        8 : echoMessage,
        99 : getOpposition
    }

    print "setting up j_listen_ip"
    s_listener = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s_listener.bind((j_listen_ip, 0))   #let OS bind to any available port
    j_listen_port = s_listener.getsockname()[1]
    print "j_listen_port is:" + str(j_listen_port)

    # tell java what port we are listening on
    sendListenPort(j_listen_port)

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

 
#Run the function to start everything
if __name__ == "__main__":

    # run cherrypy server and java listener concurrently
    print "starting mainApp"
    Process(target=runMainApp).start()
    print "starting jListener"
    Process(target=javaListener).start()
    print "processes started"
    #sys.stdout.flush()
