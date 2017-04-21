Name : Kuan-Sheng Chen 
UNI : Kc3051

There's 4 classes in file: UdpChat.java(main class),  ClientMode.java, ServerMode.java, Code.java

Command for compiling : javac ClientMode.java ServerMode.java UdpChat.java Code.java
                     => this would have warings appear, that's fine.

Command for running : java UdpChat -s 3000                         => opening a new port(3000) for server
                      java UdpChat -c Jim 127.0.0.1 3000 4008      => open a bew port(4008) for user Jim  
                      java UdpChat -c Jason 127.0.0.1 3000 4009    => open a new port(4009) for user Jason
                      java UdpChat -c Peter 127.0.0.1 3000 4010    => open a new port(4010) for user Peter

                      then you can go any test case as the professor requires (send, reg ,dereg and so on)

Algorithm used : Convert String Array to string for sending using encode function in Code.class and, then after recieve it, use decode fuction in Code.class to covert it to String Array for readible output.

Data structure used : Hash table, Arraylsit

Bug : user cannot log back after exiting (not deregistering) since if he type -c plus his user name, the system woud say this user name had been used, there's no plausible command for user to reenter the system.


test.txt : 
*** every body is registered ***

>>> [Welcome, You are registered.]
>>> Client Table update
>>> On-Line User Status Update:
Jason is on-line   
Marry is on-line   
Tim is on-line   
>>> 



*** send message to each other (including off line message)  ***

>>> Jason : Yes                                     2017/03/06 00:27:18
>>> dereg Tim
sending off-line request
>>> You are Offline. Bye.
>>> reg Tim
sending on-line request
You have off-line Messages
Jason : 2017/03/06 00:28:06       Hi
Marry : 2017/03/06 00:28:17       GOGO
>>> On-Line User Status Update:
Jason is on-line   
Marry is on-line   
Tim is on-line   
>>> Welcome Back!
>>> 



*** sending message to off line user when server is exiting ***
(first log out user when server is exiting)
sending off-line request
sending off-line request
sending off-line request
sending off-line request
sending off-line request
>>> Server not responding
>>> Exiting]

(since the server is exiting, the message got lost and user exits automatically)
>>> send Jason Hi
No ACK from Jason, message sent to server.
Wait for responding...
Wait for responding...
Wait for responding...
Wait for responding...
Wait for responding...
Server no responing either,system exit...


