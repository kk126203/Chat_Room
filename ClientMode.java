import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class ClientMode extends Thread {
    int ClientPort; 
    static int cur_port,ServerPort;
    static String name;
    static InetAddress server ;
    static InetAddress cur_addr;
    List<String> l = new ArrayList<String>();
    static DatagramSocket socket;
    static Map<String,List<String>> m = new HashMap<String,List<String>>();
    static boolean status = true, target = false, open = true, wait = true;

    public void Client(String[] args){
        if(args.length!=5){
             System.out.println("Invalid input");
             System.exit(1);
        }
        try{
        ClientPort = Integer.parseInt(args[4]);
        ServerPort = Integer.parseInt(args[3]);
        server = InetAddress.getByName(args[2]);
        name = args[1];
        if(ClientPort>65535||ClientPort<1024){
             System.out.println("port number out of bound");
             System.exit(1);
             }
        }
        catch(Exception e){
             System.out.println("Please input an integer for port number");
        }
	byte sendData[] = new byte[1024];
        byte receiveData[] = new byte[1024];
        try{
	sendData = name.getBytes();
        DatagramPacket packet = new DatagramPacket(sendData, sendData.length, server, ServerPort);
        socket = new DatagramSocket(ClientPort);
        socket.send(packet);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	socket.receive(receivePacket);
        String modifiedSentence = new String(receivePacket.getData());
	if(modifiedSentence.charAt(0)=='}'){
		System.out.println("Name already exists");
		System.exit(0);
	}
        System.out.println(">>> " + modifiedSentence);
        System.out.println(">>> Client Table update");
	Chat_start();
        socket.close();
        }
        catch(Exception e){
        	System.out.println("SocketExcption");
        	System.exit(1);
        }
    }

    public void Chat_start(){
	Code c = new Code();
	ClientMode cli = new ClientMode();
	cli.start();
	
	try{
	while(true){	
		byte receiveData2[] = new byte[1024];
		DatagramPacket receivePacket2 = new DatagramPacket(receiveData2, receiveData2.length);
		socket.receive(receivePacket2);
		if(receivePacket2.getPort()==ServerPort){
			String modifiedSentence2 = new String(receivePacket2.getData());
			if(modifiedSentence2.charAt(0)=='+'){
				wait = false;
				System.out.println("Messages received by the server and saved");
				continue;
			}
			if(modifiedSentence2.charAt(0)=='{'){/*status switch permitted by server*/
				target = true;
				if(!open) open = true;
				continue;
			}
			if(modifiedSentence2.charAt(0)=='('){
				System.out.println("You have off-line Messages");
                                continue;
			}
			if(modifiedSentence2.charAt(0)==')'){
				c = new Code();
				String temp = modifiedSentence2.substring(1,modifiedSentence2.length());
				List<String> k = c.decode(temp);	
				System.out.println(k.get(0)+" : "+k.get(1)+"       "+k.get(2));	
				continue;
			}
			if(!open) continue;
	 		update(modifiedSentence2);
			System.out.println(">>> On-Line User Status Update:");
			for(String s : m.keySet()){
					if(m.get(s).get(1).equals("1"))	System.out.println(s+" is on-line   ");
					else System.out.println(s+" is off-line  ");
				}
			System.out.print(">>> ");
			}
		else{	
			if(!open) continue;		
			int cli_port = receivePacket2.getPort();
			InetAddress Back_Addr = receivePacket2.getAddress();
			String s1 = ""+Back_Addr; String s2 = ""+cur_addr; String s3 = ""+server;
			if(s1.equals(s3)&&cli_port==ClientPort){
				System.out.println("You sent message to your ownself");
				continue;
			}
			if(s1.equals(s2)&&cli_port==cur_port)
			{
				String ackw = "";
				if(cli_port==cur_port&&s1.equals(s2))
				ackw = new String(receivePacket2.getData());
				System.out.println(ackw);
				status = false;
				continue;
			}
			String cli_name = "";
			for(String ss : m.keySet()){
				if (Integer.parseInt(m.get(ss).get(0))==cli_port) cli_name = ss;
			}
			String cli_msg = new String(receivePacket2.getData());
			List<String> outp = c.decode(cli_msg);
			System.out.println(cli_name+" : "+outp.get(1)+"                                     "+outp.get(0));
			System.out.print(">>> ");
			byte[] sendData = new byte[1024];
			String ack = "Message received by "+ cli_name;
			sendData = ack.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, Back_Addr, cli_port);
			socket.send(sendPacket);
		    }	
		}	
	}
	catch(Exception e) {
		System.out.println("Listening Thread Exception");
	}
    }  


    public void run(){   /*input*/
	Code c = new Code();
	boolean tt = false;
	try{		
	while(true){
	    if(!open){
		System.out.print(">>> ");
		BufferedReader inFromUser1 = new BufferedReader(new InputStreamReader(System.in));
            	String inp1  = inFromUser1.readLine();
		String[] inp_arr1 = inp1.split(" ");
		if(inp_arr1.length==2&&inp_arr1[0].equals("reg"))
		{
			if(!inp_arr1[1].equals(name)){
                                System.out.println("Not yout name!"); continue;
				}
			byte[] sendData = new byte[1024];
                	String mess = "{"+name;
                	sendData = mess.getBytes();
                	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, server, ServerPort);
			for(int i=0 ; i<5 ; i++){
			    System.out.println("sending on-line request");
                 	    socket.send(sendPacket);
                     	    Thread.sleep(500);
                     	    if(target){
                        	System.out.println("Welcome Back!");
                        	open = true;
				target = false;
                        	break;
                     		}
                	}	

		}
		continue;
	    }

	    if(tt)  System.out.print(">>> "); 
	    tt = true;
	    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
	    String inp  = inFromUser.readLine();
	    String[] inp_arr = inp.split(" ");
	    if(inp_arr[0].equals("dereg")){
		if(inp_arr.length!=2) {System.out.println("Client Invalid Input"); continue;}
	    	if(!inp_arr[1].equals(name)) {System.out.println("Not your name!"); continue;}
	        byte[] sendData = new byte[1024];
		String mess = "{"+name;	
		sendData = mess.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, server, ServerPort);
		for(int i=0 ; i<5 ; i++){
		     socket.send(sendPacket);
		     System.out.println("sending off-line request");
		     Thread.sleep(500);
		     if(target){
			System.out.println(">>> You are Offline. Bye.");
			open = false;
			target = false;
			break;
		     }		
		}
		if(open){
		System.out.println(">>> Server not responding");
		System.out.println(">>> Exiting]");
		System.exit(0);}
		continue;
	    }

	    if(!inp_arr[0].equals("send")&&!inp_arr[0].equals("dereg")){
			System.out.println("Client Invalid Input!");
			continue;
		}
	    else if (inp_arr[0].equals("send")){
		    long ts = System.currentTimeMillis();
	    	    Date localTime = new Date(ts);
	    	    String format = "yyyy/MM/dd HH:mm:ss";
	    	    SimpleDateFormat sdf = new SimpleDateFormat(format);
	    	    String date = sdf.format(new Date());
		    String name1 = inp_arr[1];
		    if(!m.containsKey(name1)){
				System.out.println("no such user!");
				continue;
			}
		    if(name1.equals(name))
		    {
			System.out.println("You sent the message to your own self!");	
			continue;
		    }	
		    byte[] sendData = new byte[1024];
		    InetAddress Address = InetAddress.getByName(m.get(name1).get(2));
		    int portx = Integer.parseInt(m.get(name1).get(0));
		    String msg = "";
		    for(int i = 2 ; i<inp_arr.length ; i++){
			msg = msg+inp_arr[i];    
			msg = msg+" ";
		    }
		    msg = msg.substring(0,msg.length()-1);
		    List<String> msg_l = new ArrayList<String>();
		    msg_l.add(date);
		    msg_l.add(msg);
		    msg = c.encode(msg_l);
		    sendData = msg.getBytes();
		    cur_port = portx;
		    cur_addr = Address;
		    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, Address, portx);
		    socket.send(sendPacket);
		    try{  
			Thread.sleep(500);
                    }catch(Exception e) {System.out.println("Thread errpr");}
		    if(status){
         	        System.out.println("No ACK from "+name1+", message sent to server.");
	                msg = "+"+name1.length()+name1+name.length()+"#"+name+msg;
			sendData = msg.getBytes();
			DatagramPacket sendPacket2 = new DatagramPacket(sendData, sendData.length, server, ServerPort); 
			socket.send(sendPacket2);
			for(int i=0 ; i<5 ; i++){
				if(wait){
					System.out.println("Wait for responding...");
					try{Thread.sleep(1000);}catch(Exception e){}
					
				}
			}
			if(wait){
				System.out.println("Server no responing either,system exit...");
				System.exit(0);
			}
			wait = true;
		    }	    
		    status = true;
		    cur_port = 0;
		}	
       	    }   
	}
	catch(Exception e){
		System.out.println("Sending Thread Exception");
		}
	}


    public void update(String s){
	Code c = new Code();
	int i = 0;
        List<String> l = new ArrayList<String>(); 
	l = c.decode(s);
	List<String> ll = new ArrayList<String>();	
	m.clear();
        while(i<l.size()){
		String s1 = l.get(i++);
		ll.add(l.get(i++));
		ll.add(l.get(i++));
		ll.add(l.get(i++));
		m.put(s1,new ArrayList<String>(ll));
		ll.clear();	
	}
    }

}
