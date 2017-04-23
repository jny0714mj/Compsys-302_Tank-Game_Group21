# README Phase 1#
To Start the game, you have to go to Launcher Class which is in game package and press ctrl + F11 to compile and run the game.

This game has met all the minimum requirements.

This game has modern graphics with nice background music and smart AI which detects where the power up is and picks up. It also create log when you start the game and log every single key pressed you have made with the time stamp.
When the tank is moving, there are animations for tank track.
 
The final submission table is in the report.

#README Phase 2#
To Start the game, you have to open Java first and go to Launcher Class which is in game package and press ctrl+F11 to compile and run the game.
After the game starts, you choose "online multi-play" mode, then it will automatically runs Python script and opens the web-server with your default web-driver.

The HTML works with chrome but does not work with firefox (use chrome to test it)

The login server and java/python works fine but combining login server and java/python is not working. In our web server, we have friend list which can add people into user's friend list. We also have good operating P2P networking system which sends API to each other.

The final submissions (report + marking checklist) are in git.


....
Our login server works well (ie. threaded report, looks nice) and our inter-app comm works well (can play as master or slave from any location, uses low level socket connections), but they don't (yet) work together properly. Thus we present a slightly deprecated version of the code where each component can be tested separately.

To get running...
Run Launcher.java with system arguments "master/slave 12000 12001".
First argument is whether to set this game as a master or slave (note that other game modes work the same regardless of choice), second argument is a port to start the cherrypy server listening on, and third argument is a port to connect to the other cherrypy server (overridded when pressing challenge in browser).
May run two instances of java game on the same machine to test as follows...
on first instance, run "Launcher.java master 12000 12001"
on second instance, run "Launcher.java slave 12001 1200"
... so that the ports align. note that the java/python port assignment is done with OS system calls and is thus robust (I couldn't work out a way to get cherrypy to do this reliably)

Both python and java stdin, stdout, err streams should appear in java console, with a name becide the python ones
When browser opens on machine (or if you have to do it manually),
1) please log in before you play
2) please start the slave game slightly before the master game

Most of the interesting stuff I wrote (jmci873) is actually networking on the java side- see the package "ipc" for most of the work.
I pretty much used python to echo calls from java, but the java/ python IPC protocol I constructed was slightly simpler than the inter-app API.

Java/ python networking is handled using the lowest level socket interfaces available and threaded and multiprocessed to occur at speed simultaneously with game play.