import java.io.*;
import java.net.*;
import java.util.*;
import sun.misc.Signal;
import sun.misc.SignalHandler;
import java.lang.reflect.*;

public class UdpChat{
    int ClientPort, ServerPort;
    String name;
    InetAddress server;
    static String[] table ;
    Map<String,String> m = new HashMap<String,String>();
    public static void main(String args[]) throws Exception{
	DiagSignalHandler.install("INT");
    	UdpChat udp = new UdpChat();
	table = args;
	ServerMode server = new ServerMode();
	ClientMode client = new ClientMode();
        if(args[0].equals("-s")) server.CreateServer(args);
        else if(args[0].equals("-c")) client.Client(args);
	else System.out.println("Invalid Input");
    }
} 
    class DiagSignalHandler implements SignalHandler {
	int ClientPort, ServerPort;
    	String name;
    	InetAddress server;
	DatagramSocket socket;
    	private SignalHandler oldHandler;
    	public static DiagSignalHandler install(String signalName) {
        Signal diagSignal = new Signal(signalName);
        DiagSignalHandler diagHandler = new DiagSignalHandler();
        diagHandler.oldHandler = Signal.handle(diagSignal,diagHandler);
	        return diagHandler;
    }
    public void handle(Signal sig) {

	UdpChat u = new UdpChat();
        String[] table = u.table ;
	if(table[0].equals("-c")){
	System.out.println(table[2]);
	ServerPort = Integer.parseInt(table[3]);
	try{
	server = InetAddress.getByName(table[2]);
	socket = new DatagramSocket(1029);
	byte[] sendData = new byte[1024];
        String mess = "{"+name;
        sendData = mess.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
 server, ServerPort);
	socket.send(sendPacket);
        socket.close();	
	}catch(Exception e){System.out.println("Exc"); System.exit(0);}
	}
        System.out.println("Signal interrupt detected.... exit");
	System.exit(0);
    }	
}
