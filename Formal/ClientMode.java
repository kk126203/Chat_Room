import java.io.*;
import java.net.*;
import java.util.*;


public class ClientMode extends Thread{
   
    encrypt e;
    String name;
    int port, serverPort;
    InetAddress serverAddr;	
    DatagramSocket socket;
    public static void main(String[] args){
        ClientMode c = new ClientMode();
        if(args.length!=4){
            return ;
        }
        int status = c.Start_Client(args[0], args[1], args[2], args[3]);
	if(status!=0){
	    System.out.println("Client startup fail, plz restart");
	    return;
	}
	System.out.print("Client registered sucess!");
	c.start();
	c.Start_sending();
    }

    public int Start_Client(String mypor, String name, String Addr, String servpor){
	try{
	    e = new encrypt();
	    port = Integer.parseInt(mypor);
	    this.name = name;
	    serverAddr = InetAddress.getByName(Addr);
	    serverPort = Integer.parseInt(servpor);
            socket = new DatagramSocket(port);
	    String handshake = "#"+name;
	    byte[] sendData = handshake.getBytes();
	    if(sendData==null)
		return -1;
	    DatagramPacket packet = new DatagramPacket(sendData, sendData.length, serverAddr, serverPort);
            socket.send(packet);
		
	    byte[] receiveData = new byte[1024];
            DatagramPacket packet1 = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(packet1);
	    String msg = new String(packet1.getData());
	    if(msg.charAt(0)=='#'&&msg.charAt(1)=='#'){
		System.out.println(msg.substring(2, msg.length()));
		return -1;
	    }
	    return 0;
	}catch(Exception e){
	    System.out.println("Err");
	    return -1;
	}
    }

    public void run(){
	try{
	    boolean first = true;
	    while(true){
		byte receiveData[] = new byte[1024];
	        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		socket.receive(receivePacket);
		String msg = new String(receivePacket.getData());
		if(msg.charAt(0)=='!'){
		    msg = msg.substring(1, msg.length());
		    List<String> l = e.decode(msg);
		    System.out.println(" ");
		    System.out.println("Status update, current registered clients :");
		    System.out.println(l);
		    if(first){
			first = false;
			continue;
		    }
		    System.out.print(">>>");
		}else if(msg.charAt(0)=='^'){
		    System.out.println("No such person");
		    System.out.print(">>>");
	        }else{
		    int len = Integer.parseInt(msg.substring(0,1));
		    String source = msg.substring(1, 1+len);
		    int len2 = Integer.parseInt(msg.substring(1+len, 2+len));
		    String cname = msg.substring(2+len, 2+len+len2);
		    if(!cname.equals(name)){
			continue;
		    }     
		    assert(cname.equals(name));						//check that client name in the packet is this user's name
		    String s = msg.substring(3+len+len2, msg.length());
		    System.out.println(" ");
		    System.out.println("message received from "+source+" :");
		    System.out.println(s);
		    System.out.print(">>>");
		}
	    }
        }catch(Exception e){
	    e.printStackTrace();
            System.out.println(e);
	}
    }

    public void Start_sending(){
	try{
	    Thread.sleep(500);
	    while(true){	
	    	System.out.print(">>> ");	
	    	BufferedReader inFromUser1 = new BufferedReader(new InputStreamReader(System.in));
            	String inp1  = inFromUser1.readLine();
	    	int index = -1;
	    	for(int i=0 ; i<inp1.length() ; i++){
	  	    if(inp1.charAt(i)==' '){
		    	index = i;
		    	break;
		    }
	        }
	        if(index==-1){
		    System.out.println("Please specify the person that you wish to talk to");
		    continue;
	        }

		String cname = inp1.substring(0, index);
		if(cname.equals(name)){
		    System.out.println("You are sending message to yourself!");
		    continue;
		}
		assert(!cname.equals(name));			//check that users won't send message to themselves

	        String s = ""+this.name.length()+this.name+index+inp1;  
	        byte[] sendData = s.getBytes();
                if(sendData==null){
                    continue;
	    	}
                DatagramPacket packet = new DatagramPacket(sendData, sendData.length, serverAddr, serverPort);
                socket.send(packet);
	    }

	}catch(Exception e){
	    e.printStackTrace();
            System.out.println(e);
	}
    }
}  
