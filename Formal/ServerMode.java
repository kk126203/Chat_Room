import java.io.*;
import java.net.*;
import java.util.*;


public class ServerMode{

    int ServerPort;
    DatagramSocket socket;
    encrypt e;
    public static void main(String[] args){
	ServerMode s = new ServerMode();
	if(args.length!=1){
	    System.out.println("Please specify server port");
	    return ;
	}
	s.Start_Server(Integer.parseInt(args[0]));
	s.Start_Listening();
    } 
    
    public void Start_Server(int Port){
	ServerPort = Port;
	e = new encrypt();
	try{
	    socket = new DatagramSocket(Port);
	}catch(Exception e){
            System.out.println("err");
        }
    }
 
    public void Start_Listening(){
	int count = 0;
	String[] table = new String[15];
	for(int i=0 ; i<table.length ; i++)
	    table[i] = "";
	try{
	    while(true){
		byte[] receiveData = new byte[1024];
		DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
		socket.receive(packet);

		InetAddress IPA = packet.getAddress();
            	String clientIP = IPA.getHostAddress() ;
		int client_port = packet.getPort();
		String clientPort = String.valueOf(client_port);
		String msg = new String(receiveData, 0, packet.getLength());

		if(msg.charAt(0)=='#'){
		    if(count>4){
		        send("##No user is available to register", IPA, client_port);
			continue;
		     }				
		    assert(count<5);			// check the upperbound of registered users
		    
		    String name = msg.substring(1, msg.length());
		    boolean exist = false;
		    for(int i=0 ; i<table.length ; i+=3){
			if(table[i].equals(name)){
			    exist = true;
			    break;
			}
		    }
		    if(exist){
			send("##Invalid Name or this name already been used", IPA, client_port);
			continue;
		    }
		    assert(!exist);			// check that there's no duplicated name exists
		    int index = 3*count;
		    table[index] = name;
		    table[index+1] = clientIP;
		    table[index+2] = clientPort;
		    count++;
 		    send("Registered!!", IPA, client_port);
	  	    broadcast(table); 	
   
		}else{
		    int len = Integer.parseInt(msg.substring(0,1));
		    int len2 = Integer.parseInt(msg.substring(len+1, len+2));
		    String cname = msg.substring(len+2, len+len2+2);
		    int index = -1;
		    for(int i=0 ; i<table.length ; i+=3){
			if(table[i].equals(cname)){
			    index = i;
			    break;	
			}
		    }
		    if(index==-1){
			send("^", IPA, client_port);
			continue;
		    }
		    assert(index!=-1);			// check the end user name exists before sending
		    InetAddress Addr = InetAddress.getByName(table[index+1]);
		    int potrr = Integer.parseInt(table[index+2]);
		    send(msg, Addr, potrr);

		}
	    }

	}catch(Exception e){
	    e.printStackTrace();
            System.out.println(e);
	}
    }

    public void send(String reply, InetAddress IPA, int client_port){
	byte[] sendData = new byte[1024];
	sendData = reply.getBytes();
	if(sendData==null)
	    return;
	DatagramPacket replyPacket = new DatagramPacket(sendData, sendData.length, IPA, client_port);
	try{
	    socket.send(replyPacket);
	}catch(Exception e){
	    System.out.println("socket fail");
	}
    }

    public void broadcast(String[] table){
	List<String> l = new ArrayList<String>();
	for(int i=0 ; i<table.length ; i+=3){
	    if(table[i]=="")
		break;
	    l.add(table[i]);
	}
	String s = e.encode(l);
	s = "!"+s;
	for(int i=0 ; i<table.length ; i+=3){
	    if(table[i]=="")
                break;
	    try{
	    	send(s, InetAddress.getByName(table[i+1]), Integer.parseInt(table[i+2]));
	    }catch(Exception e){
		System.out.println("err");
	    }
	}
    }
}
