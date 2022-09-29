import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/*  
    /============================\
    |  COMP2230 Assignment       | 
    |  Name : Ni Zeng            | 
    |  Student Number : c3238805 |
    \============================/   */

public class Player{

    
    boolean foundPlayer;    //variable to hold if found a player
    DatagramPacket received_packet;

    int myPort;
    DatagramSocket socket;
    
    private static InetAddress broadcastAddress;
    private static int broadcastPort;

    public Player(){
        this.foundPlayer = false;
        this.myPort = new Random().nextInt(1000) + 5000; // initial a random TCP port number
        byte[] buf = new byte[new String("NEW PLAYER:" + myPort).getBytes().length];
        this.received_packet = new DatagramPacket(buf, buf.length);
    }

    public static void main(String arg[]) throws Exception{
        Player newPlayer = new Player();
        //ask user input Broadcast address and Broadcast port
        //broadcastAddress = arg[0];
        //broadcastPort = Integer.parseInt(arg[1]);  

        broadcastAddress = InetAddress.getByName("192.168.2.255");
        broadcastPort = 8888;

        newPlayer.broadcastAddress = broadcastAddress;
        newPlayer.socket = new DatagramSocket(broadcastPort); // initial a socket using user specified broadcast Port.
        newPlayer.socket.setSoTimeout(3000); //time out in 30 second  = 30000

        System.out.println("Listening on UDP port " + broadcastPort);
    
        
        Thread Thread_Timeout = new Thread_Timeout(newPlayer);
        Thread Thread_listenner = new Thread_listenner(newPlayer);
        
        
        Thread_Timeout.start();
        Thread_listenner.start();
        

        //check on the received msg to see if its valid


        // first boarcast 

        //start searching on different ports
        
        //if recive a response on the generated port, start TCP connection



        //TCP
       // BattleShip p1 = new BattleShip();

        //p1.printGraph();
        //System.out.println(p1.printGraph());

        


    }

/* 
    public static void serverTCP(){


        // establish a TCP connection
        Scanner reader = new Scanner(System.in); // reading from console: system.in

        System.out.println("SERVER: current port: " + myPort);

        String response = "";

        // while not recived a shut down command, server stay alive

        while (true) {

            try (
                    // first initial a serverSocket using port 4500
                    ServerSocket serversocket = new ServerSocket(myPort);
                    Socket socket = serversocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);) {
                System.out.println("CONNECTION OPEN"); // notice when connection is ready
                String msg = ""; // variable to hold Client's response

                // convert the msg from ASCII to string
                while ((msg = asciiToString(in.readLine())) != null) {

                    System.out.format("CLIENT: %s\n", msg); // display client msg on console

                    // Revice Command--------------------------------------------------------------
                    if (msg.equals("TAX" + "\n")) {

                        // print Server response msg in console
                        System.out.print("SERVER:" + "TAX: OK" + "\n");

                        // convert into ACSII and send to server
                        out.println(toAscii("TAX: OK" + "\n"));
                    }

                    else if (msg.contains("STORE" + "\n")) {
                        // first check if the Size of LinkedList<TaxNode> taxScale >=10
                        if (ts.taxScale.getSize() >= 10) {
                            response = "STORE: Fail (sufficient to store up to ten income ranges, Maximum ranges reached.)";
                            // print Server response msg in console
                            System.out.print("SERVER:" + response + "\n");

                            // convert into ACSII and send to server
                            out.println(toAscii("STORE:" + response + "\n"));

                        } else {

                            // first filter the data
                            String[] msg_array = msg.split("\n");

                            if (msg_array[2].isEmpty()) {
                                msg_array[2] = "-1";
                            }
                            // start_income , end_income, base_tax , tax_per_dollar
                            TaxNode newRange = new TaxNode(
                                    Integer.parseInt(msg_array[1]), Integer.parseInt(msg_array[2]),
                                    Integer.parseInt(msg_array[3]), Integer.parseInt(msg_array[4]));

                            // insert the TaxNode inorder
                            ts.taxScale.insertInOrder(newRange);

                            // send STORE: OK response back to client
                            response = "STORE: OK";
                            // print Server response msg in console
                            System.out.print("SERVER:" + response + "\n");

                            // convert into ACSII and send to server
                            out.println(toAscii(response + "\n"));

                        }

                    }

                    else if (msg.contains("QUERY" + "\n")) {

                        Iterator<TaxNode> listAllRange = ts.taxScale.iterator();
                        String queryOut = "";
                        while (listAllRange.hasNext()) {

                            TaxNode temp = listAllRange.next();
                            String emptyString = temp.getEnd_income().toString();
                            if (temp.getEnd_income().equals(-1)) {
                                emptyString = "~";
                            }

                            queryOut += temp.getStart_income() + "  " + emptyString + "  " + temp.getBase_tax()
                                    + "  " + temp.getTax_per_dollar() + "  " + "\n";

                        }

                        // send QUERY: OK response back to client
                        response = queryOut + "QUERY: OK";
                        // print Server response msg in console
                        System.out.print("SERVER:" + response + "\n");

                        // convert into ACSII and send to server
                        out.println(toAscii(response + "\n"));

                    }

                    else if (msg.contains("BYE" + "\n")) {
                        // send response msg: BYE: OK to Client

                        response = "BYE: OK";
                        // print Server response msg in console
                        System.out.print("SERVER:" + response + "\n");

                        // convert into ACSII and send to server
                        out.println(toAscii(response + "\n"));

                        break;

                    }

                    else if (msg.contains("END" + "\n")) {

                        response = "END:OK";
                        // print Server response msg in console
                        System.out.print("SERVER:" + response + "\n");

                        // convert into ACSII and send to server
                        out.println(toAscii(response + "\n"));

                        break;

                    }

                    else {

                        // check msg to see if its valid
                        if (checkInteger(msg)) {
                            msg = msg.replace("\n", "");
                            // run the calculator method and get response string
                            response = calculator(Integer.parseInt(msg));

                            System.out.print("SERVER:" + response + "\n");
                            // convert into ACSII and send to server
                            out.println(toAscii(response + "\n"));

                        } else {

                            // when recive a undefined command from client site, response with I dont Know
                            // msg
                            response = "I DON'T KNOW " + msg;
                            System.out.print("SERVER:" + response + "\n");
                            // convert into ACSII and send to server
                            out.println(toAscii(response + "\n"));

                        }

                    }

                }

                // when break out of the while (in.readline()) loop

                if (response.contains("END:OK")) {
                    // Server close all the connection
                    reader.close();
                    serversocket.close();
                    socket.close();
                    in.close();
                    out.close();
                    System.out.println("TaxServer Shutdown.");
                    // exit the application
                    System.exit(0);
                }

            } catch (IOException e) { // You should have some better exception handling
                e.printStackTrace();

            }

        }

        
    }
      */

    //this method is to start TCP connection 
    public static void startTCP(){

    }

    public DatagramSocket getSocket(){
        return this.socket;
    }
    public int getBroadcastPort(){
        return this.broadcastPort;
    }
    
    public InetAddress getBroadcastAddress() {
        return this.broadcastAddress;
    }

    public int getmyPort(){
        return this.myPort;
    }
    public boolean foundPlayer(){
        return this.foundPlayer;
    }
    public DatagramPacket getreceived_packet(){
        return this.received_packet;
    }

    

}

// this thread handle send self msg to broadcast address
class Thread_Timeout extends Thread{

    private Player newPlayer;

    public Thread_Timeout(Player newPlayer){
          
        this.newPlayer = newPlayer;
    }
    
    @Override
    public void run(){
        
        byte[] mybuf = new String("NEW PLAYER:" + newPlayer.getmyPort()).getBytes();
        DatagramPacket selfpacket = new DatagramPacket(mybuf, mybuf.length,newPlayer.getBroadcastAddress(),8888);

        while(newPlayer.foundPlayer == false ){
            try {
                newPlayer.socket.receive(newPlayer.getreceived_packet());
            } catch (SocketTimeoutException e) {
                // if time out , send a new msg to the same port
                try {
                    newPlayer.socket.send(selfpacket);
                } catch (IOException e1) {
                    
                    e1.printStackTrace();
                } // send a new msg to the same port

            } catch (IOException e) {
                
                e.printStackTrace();
            }
        }
        


    }
}

class Thread_listenner extends Thread {

    private Player newPlayer;

    public Thread_listenner(Player newPlayer){
        
        this.newPlayer = newPlayer;

    }
    
    @Override
    public void run() {

        

        // this listenner will loop until found a player
        while(newPlayer.foundPlayer == false){
            DatagramPacket received_packet = newPlayer.getreceived_packet();

            String receivedMsg = new String(received_packet.getData(), StandardCharsets.UTF_8);
            if (receivedMsg != null) {

                // check if the msg is from a different program
                String[] split = receivedMsg.split(":");

                // validate the received msg
                if (split.length == 2 && (!split[1].equals(String.valueOf(newPlayer.getmyPort())))) {

                    // when the received port number is different , establish a TCP connection and
                    // send response back.
                    System.out.println("Received: " + receivedMsg);

                    // serverTCP(); //initial aa server TCP connection

                }

            }



        }

   

    }

}