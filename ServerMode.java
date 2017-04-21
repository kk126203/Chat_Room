import java.io.*;
import java.net.*;
import java.util.*;

public class ServerMode {
    int ServerPort, ClientPort, Status;
    String name;
    InetAddress server;
    static Map<String,List<String>> m = new HashMap<String,List<String>>();
    Map<String,List<String>> off_m = new HashMap<String,List<String>>();
    DatagramSocket socket ;
    public void CreateServer(String[] args){
        if(args.length!=2){
             System.out.println("Invalid input");
             System.exit(1);
        }
        try{
        ServerPort = Integer.parseInt(args[1]);
        if(ServerPort>65535||ServerPort<1024){
             System.out.println("port number out of bound");
             System.exit(1);
        }
        for (int count = 0; ; count++) {
	    byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];
            DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
	    System.out.println("rec");
            socket = new DatagramSocket(ServerPort);
	    socket.receive(packet);
	    InetAddress IPA = packet.getAddress();
            int portt = packet.getPort();
            String msg = new String(receiveData, 0, packet.getLength());
	    if(msg.charAt(0)=='{'){
		String name1 = msg.substring(1,msg.length());
		if(m.get(name1).get(1).equals("0"))  m.get(name1).set(1,"1"); 
		else   m.get(name1).set(1,"0"); 	
		String hen = "{";
		sendData = hen.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPA, portt);
		socket.send(sendPacket);
		if(m.get(name1).get(1).equals("1")&&off_m.containsKey(name1)){
			byte[] sendData3 = new byte[1024];
			byte[] sendData2 = new byte[1024];
			String sen = "(";
			sendData3 = sen.getBytes();
			DatagramPacket sendPacket3 = new DatagramPacket(sendData3, sendData3.length, IPA, portt);
			socket.send(sendPacket3);
			Code c = new Code();
			List<String> temp = off_m.get(name1);
			for(String msge : temp){
			    String k = ")";
			    k = k + msge;
			    sendData2 = k.getBytes();
			    DatagramPacket sendPacket2 = new DatagramPacket(sendData2, sendData2.length, IPA, portt);
			    socket.send(sendPacket2);
			}
			off_m.remove(name1);
		}
		broadcast();
		socket.close();
		continue;
	    }
	    if(msg.charAt(0)=='+'){
		String cur_name = msg.substring(2,2+msg.charAt(1)-'0');
		String mesg = msg.substring(2+msg.charAt(1)-'0',msg.length());
		if(!off_m.containsKey(cur_name)){
			List<String> l1 = new ArrayList<String>();
			l1.add(mesg);
			off_m.put(cur_name,l1);
		}
		else{
			List<String> l1 = off_m.get(cur_name);
			l1.add(mesg);
		}
		String hen = "+";
                sendData = hen.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length
, IPA, portt);
                socket.send(sendPacket);
		socket.close();
		continue;
	    }
            if(!m.containsKey(msg)){
	    Status = 1;
	    List<String> l = new ArrayList<String>();
            l.add(String.valueOf(portt));
	    l.add(String.valueOf(Status));
	    String a = ""+IPA;
	    a = a.substring(1,a.length());
	    l.add(a);
	    m.put(msg,l);
            String temp = "[Welcome, You are registered.]";
            sendData = temp.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPA, portt);
	    socket.send(sendPacket);
	    broadcast();
            }
	    else{
                String hen = "}";
                sendData = hen.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPA, portt);
                socket.send(sendPacket);
	    }
            socket.close();
            }
        }
        catch(Exception e){
             System.out.println("Please input an integer for port number");
        }
    }
    
    public void broadcast(){
        String temp2 = helper2();
	try{
	for(String s : m.keySet()){
	if(m.get(s).get(1).equals("0")) continue;
	byte[] sendData2 = new byte[1024];
        sendData2 = temp2.getBytes();	
	String tem = m.get(s).get(2);
	InetAddress addr = InetAddress.getByName(tem);
        DatagramPacket sendPacket2 = new DatagramPacket(sendData2, sendData2.length, addr, Integer.parseInt(m.get(s).get(0)));
	socket.send(sendPacket2);
	}
	}
	catch(Exception e){System.out.println("IOException");}
    }
    

    public String helper2(){
	List<String> cur = new ArrayList<String>();
	for(String key : m.keySet()){
		cur.add(key);
		cur.add(m.get(key).get(0));
		cur.add(m.get(key).get(1));
		cur.add(m.get(key).get(2));
	}
	Code c = new Code();
	return c.encode(cur);
    }   
}
