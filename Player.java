import java.io.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
    private static String broadcastAddress_string;
    private static int broadcastPort;
    static int TCP_port;
    
    static BattleShip gameboard;

    public Player(){
        this.foundPlayer = false;
        this.myPort = new Random().nextInt(1000) + 5000; // initial a random TCP port number
        byte[] buf = new byte[1024];
        this.received_packet = new DatagramPacket(buf, buf.length);
        this.broadcastAddress_string = "";

    }

    public static void main(String arg[]) throws Exception{

        Scanner reader = new Scanner(System.in); // reading from console: system.in

        Player newPlayer = new Player();
        //ask user input Broadcast address and Broadcast port
        //broadcastAddress = arg[0];
        //broadcastPort = Integer.parseInt(arg[1]);  

        broadcastAddress_string = "192.168.2.255";
        broadcastAddress = InetAddress.getByName("192.168.2.255");
        broadcastPort = 8888;

        newPlayer.broadcastAddress = broadcastAddress;

        newPlayer.createSocket(broadcastPort);       //initial a socket connection 
        
        newPlayer.socket.setSoTimeout(3000); //time out in 30 second  = 30000

        byte[] mybuf = new String("NEW PLAYER:" + newPlayer.getmyPort() ).getBytes();
        DatagramPacket selfpacket = new DatagramPacket(mybuf, mybuf.length, newPlayer.getBroadcastAddress(), newPlayer.getBroadcastPort());

                
        newPlayer.socket.send(selfpacket);
        System.out.println("Listening on UDP port " + broadcastPort);
    
        
        Thread Thread_Timeout = new Thread_Timeout(newPlayer);
        Thread Thread_listenner = new Thread_listenner(newPlayer);
        
        Thread_Timeout.start();
        Thread_listenner.start();
    
        //Thread_Timeout.join();
        Thread_listenner.join();
        

    }

    //this method is to start TCP connection 
    public static void initialTCP(int TCPport){
        Scanner scanner = new Scanner(System.in);
        String Enemy_msg = "";
        String my_moves = "";

        while (true) {

            try (
                    // first initial a serverSocket using port 4500
                    ServerSocket serversocket = new ServerSocket(TCPport);
                    Socket socket = serversocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);) {

                    System.out.println("TCP CONNECTION OPEN"); // notice when connection is ready
                    String msg = ""; // variable to hold Client's response
                    
                    BattleShip game_board = new BattleShip();
                    setgame_board(game_board);
                    System.out.println(game_board.printGraph());

                    BattleShip record_board = new BattleShip();
                    record_board.record_board();
                    System.out.println(record_board.printGraph());

                    //the player who initial the game get to go first (Player 1)
                    System.out.print("FIRE:");
                    String choose_plot = scanner.nextLine();
                    my_moves += "FIRE:"+ choose_plot + " "; 
                    out.println("FIRE:"+choose_plot);

                while ((msg = in.readLine()) != null) {

                    String[] plot = msg.split(":");
                    
                    // Revice Command--------------------------------------------------------------
                    if (msg.contains("MISS:") ) {

                        // update the record
                        record_board.update_record(plot[1], " .");
                        System.out.println(game_board.printGraph());
                        System.out.println(record_board.printGraph());
                        my_moves += "(" + msg + ")" + "    / "; // save the my_moves's msg

                        System.out.format("Enemy: %s\n", msg); // display server msg on console

                        System.out.println("Enemy moves:" + Enemy_msg);
                        System.out.println("My moves:" + my_moves);
                        

                    } else if (msg.contains("HIT:") ) {
                        // update the record
                        record_board.update_record(plot[1], " X");
                        System.out.println(game_board.printGraph());
                        System.out.println(record_board.printGraph());

                        my_moves += "(" + msg + ")" + "    / "; // save the my_moves's msg
                        System.out.format("Enemy: %s\n", msg); // display server msg on console

                        System.out.println("Enemy moves:" + Enemy_msg);
                        System.out.println("My moves:" + my_moves);

                    } else if (msg.contains("SUNK:") ) {
                        // update the record
                        record_board.update_record(plot[1], " X");
                        System.out.println(game_board.printGraph());
                        System.out.println(record_board.printGraph());

                        my_moves += "(" + msg + ")" + "    / "; // save the my_moves's msg
                        System.out.format("Enemy: %s\n", msg); // display server msg on console

                        System.out.println("Enemy moves:" + Enemy_msg);
                        System.out.println("My moves:" + my_moves);

                    } else if (msg.contains("GAME OVER:") ) {
                        // if received game over msg, the current player has now wont the game
                        record_board.update_record(plot[1], " X");

                        my_moves += "(" + msg + ")" + "    / "; // save the my_moves's msg
                        System.out.println(game_board.printGraph());
                        System.out.println(record_board.printGraph());
                        System.out.format("Enemy: %s\n", msg); // display server msg on console

                        System.out.println("Enemy moves:" + Enemy_msg);
                        System.out.println("My moves:" + my_moves);

                        
                        System.out.println("YOU NOW WON THE GAME !");

                        //inform other player that the game is finished
                        out.println("YOU HAVE LOST");
                        out.flush();
                        System.exit(0);

                    } else if (msg.contains("FIRE:") ) {

                        msg = game_board.fire(plot[1]); // this line will return the response of the fire plot

                        Enemy_msg += "(" + msg + ")" + "    / "; // save the enemy's msg

                        System.out.println(game_board.printGraph());
                        System.out.println(record_board.printGraph());
                        System.out.format("Enemy: %s\n", msg); // display server msg on console

                        System.out.println("Enemy moves:" + Enemy_msg);
                        System.out.println("My moves:" + my_moves);

                        if (game_board.isGameOver() == true) {
                            out.println(msg);
                            out.flush();

                        } else if (msg.contains("I don't know.")) {
                            out.println(msg);
                            out.flush();
                        } 
                        else {
                            out.println(msg);
                            out.flush();

                            System.out.print("FIRE:");
                            choose_plot = scanner.nextLine();

                            my_moves += "FIRE:" + choose_plot + " ";
                            out.println("FIRE:" + choose_plot);
                            out.flush();
                        }
                        
                        

                    } else if (msg.contains("YOU HAVE LOST")) {
                        System.out.println(game_board.printGraph());
                        System.out.println(record_board.printGraph());
                        System.out.format("Enemy: %s\n", msg); // display server msg on console

                        System.out.println("Enemy moves:" + Enemy_msg);
                        System.out.println("My moves:" + my_moves);

                        //print out in console log
                        System.out.println("YOU HAVE LOST");
                        System.exit(0);

                    } else if (msg.contains("I don't know.")) {
                        System.out.println(game_board.printGraph());
                        System.out.println(record_board.printGraph());
                        System.out.format("Enemy: %s\n", msg); // display server msg on console

                        System.out.println("Enemy moves:" + Enemy_msg);
                        System.out.println("My moves:" + my_moves);

                        if (game_board.isGameOver() == true) {
                            out.println(msg);
                            out.flush();

                        } 
                        else {
                           
                            System.out.print("FIRE:");
                            choose_plot = scanner.nextLine();

                            my_moves += "FIRE:" + choose_plot + " ";
                            out.println("FIRE:" + choose_plot);
                            out.flush();
                        }

                    }
                    else {

                        msg = "Player 1: I don't know.";
                        System.out.println(game_board.printGraph());
                        System.out.println(record_board.printGraph());
                        System.out.format("Enemy: %s\n", msg); // display server msg on console

                        System.out.println("Enemy moves:" + Enemy_msg);
                        System.out.println("My moves:" + my_moves);

                        // when received undefined msg, reply back with "I don't know"
                        out.println(msg); // reply
                        out.flush();
                    }


                }
                    

            } catch (IOException e) { // You should have some better exception handling
                e.printStackTrace();

            }

        }
    }

    public static void TCP_transmit(InetAddress broadcastAddress,int TCP_port){

        Scanner scanner = new Scanner(System.in);
        String Enemy_msg = "";
        String my_moves = "";

        try (
                Socket socket = new Socket(broadcastAddress.getLocalHost(), TCP_port);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);) {

                System.out.println("TCP CONNECTION CONNECTED"); // notice when connection is ready
                String msg = ""; // variable to hold responses

                BattleShip game_board = new BattleShip();
                setgame_board(game_board);
                System.out.println(game_board.printGraph());

                BattleShip record_board = new BattleShip();
                record_board.record_board();
                System.out.println(record_board.printGraph());

            // convert the msg from ASCII to string
            while ((msg =in.readLine()) != null) {

                String[] plot = msg.split(":");
                

                // Revice Command--------------------------------------------------------------
                if (msg.contains("MISS:") ) {

                        // update the record
                        record_board.update_record(plot[1], " .");
                        System.out.println(game_board.printGraph());
                        System.out.println(record_board.printGraph());

                        my_moves += "(" + msg + ")" + "    / "; // save the my_moves's msg
                        System.out.format("Enemy: %s\n", msg); // display server msg on console

                        System.out.println("Enemy moves:" + Enemy_msg);
                        System.out.println("My moves:" + my_moves);

                    } else if (msg.contains("HIT:") ) {

                        // update the record
                        record_board.update_record(plot[1], " X");
                        System.out.println(game_board.printGraph());
                        System.out.println(record_board.printGraph());
                        my_moves += "(" + msg + ")" + "    / "; // save the my_moves's msg
                        System.out.format("Enemy: %s\n", msg); // display server msg on console

                        System.out.println("Enemy moves:" + Enemy_msg);
                        System.out.println("My moves:" + my_moves);

                    } else if (msg.contains("SUNK:") ) {

                        // update the record
                        record_board.update_record(plot[1], " X");
                        System.out.println(game_board.printGraph());
                        System.out.println(record_board.printGraph());
                        my_moves += "(" + msg + ")" + "    / "; // save the my_moves's msg
                        System.out.format("Enemy: %s\n", msg); // display server msg on console

                        System.out.println("Enemy moves:" + Enemy_msg);
                        System.out.println("My moves:" + my_moves);

                    } else if (msg.contains("GAME OVER:") ) {

                        // if received game over msg, the current player has now wont the game
                        record_board.update_record(plot[1], " X");
                        System.out.println(game_board.printGraph());
                        System.out.println(record_board.printGraph());
                        my_moves += "(" + msg + ")" + "    / "; // save the my_moves's msg
                        System.out.format("Enemy: %s\n", msg); // display server msg on console

                        System.out.println("Enemy moves:" + Enemy_msg);
                        System.out.println("My moves:" + my_moves);

                        System.out.println("YOU NOW WON THE GAME !");

                        // inform other player that the game is finished
                        out.println("YOU HAVE LOST");
                        out.flush();
                        System.exit(0);

                    } 
                    else if(msg.contains("FIRE:") ){

                        msg = game_board.fire(plot[1]);     // this line will return String of reply on corresponding plot
                        
                        Enemy_msg += "("+msg +")"+ "    / "; // save the enemy's msg

                        
                        System.out.println(game_board.printGraph());
                        System.out.println(record_board.printGraph());
                        System.out.format("Enemy: %s\n", msg); // display server msg on console

                        System.out.println("Enemy moves:" + Enemy_msg);
                        System.out.println("My moves:" + my_moves);

                        if(game_board.isGameOver() == true){
                            out.println(msg);
                            out.flush();

                        } else if(msg.contains("I don't know.")){
                            out.println(msg);
                            out.flush();
                        }
                        else {
                            out.println(msg);
                            out.flush();

                            System.out.print("FIRE:");
                            String choose_plot = scanner.nextLine();
                            my_moves += "FIRE:" + choose_plot + " ";
                            out.println("FIRE:" + choose_plot);
                            out.flush();
                        }


                    } else if (msg.contains("YOU HAVE LOST")) {
                        System.out.println(game_board.printGraph());
                        System.out.println(record_board.printGraph());
                        System.out.format("Enemy: %s\n", msg); // display server msg on console

                        System.out.println("Enemy moves:" + Enemy_msg);
                        System.out.println("My moves:" + my_moves);

                        // print out in console log
                        System.out.println("YOU HAVE LOST");
                        System.exit(0);

                    } else if (msg.contains("I don't know.")) {
                        System.out.println(game_board.printGraph());
                        System.out.println(record_board.printGraph());
                        System.out.format("Enemy: %s\n", msg); // display server msg on console

                        System.out.println("Enemy moves:" + Enemy_msg);
                        System.out.println("My moves:" + my_moves);

                        if (game_board.isGameOver() == true) {
                            out.println(msg);
                            out.flush();

                        } else {
                           
                            System.out.print("FIRE:");
                            String choose_plot = scanner.nextLine();
                            my_moves += "FIRE:" + choose_plot + " ";
                            out.println("FIRE:" + choose_plot);
                            out.flush();
                        }


                    }
                    else {
                        System.out.println(game_board.printGraph());
                        System.out.println(record_board.printGraph());
                        System.out.format("Enemy: %s\n", msg); // display server msg on console

                        System.out.println("Enemy moves:" + Enemy_msg);
                        System.out.println("My moves:" + my_moves);

                        msg = "Player 2: I don't know.";
                        out.println(msg); // reply
                        out.flush();
                    }
                
                    

                    
                

            }

        } catch (Exception e) { // You should have some better exception handling
            
            e.printStackTrace();

        }

    }


    public void createSocket(int UDPPort){

        boolean successful = false;

        // this while loop will run until establish a socket
        if(successful == false){
            try {
                this.socket = new DatagramSocket(UDPPort); // initial a socket using user specified broadcast
                                                                 // Port.
                successful = true;  // when reach this line, the socket is created successfully                                                 
            } catch (SocketException e) {
                // when having a bindException, it could be a socket already created with the
                // same broadcastAddress and broadcast Port (when running on the same local
                // machine)
                createSocket(new Random().nextInt(1000)+5000);

            }
        }
        
    }


    public static byte[] trim(byte[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) {
            --i;
        }

        return Arrays.copyOf(bytes, i + 1);
    }

    public static BattleShip getgame_boarad(){
        return gameboard;
    }

    public static void setgame_board(BattleShip gameboard){
        gameboard = gameboard;
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
    
    public boolean getfoundPlayer(){
        return this.foundPlayer;
    }
    
    public void setfoundPlayer(boolean t) {
        this.foundPlayer = t;
    }
    
    public DatagramPacket getreceived_packet(){
        return this.received_packet;
    }   
    
    public void setTCP_port(int TCP_port){
        this.TCP_port = TCP_port;
    }
    
    public int getTCP_port() {
        return TCP_port;
    }

    public void closeUDP(DatagramSocket socket){
        socket.close();
    }

    public String get_broadcastAddress_string(){
        return this.broadcastAddress_string;
    }

}


class Thread_Timeout extends Thread{
    // this thread handle send self msg to broadcast address
    private Player newPlayer;

    public Thread_Timeout(Player newPlayer){
          
        this.newPlayer = newPlayer;
    }
    
    @Override
    public void run(){
        
        byte[] mybuf = new String("NEW PLAYER:" + newPlayer.getmyPort()).getBytes();
        DatagramPacket selfpacket = new DatagramPacket(mybuf, mybuf.length,newPlayer.getBroadcastAddress(),newPlayer.getBroadcastPort());

        while(newPlayer.getfoundPlayer() == false ){
            try {
                newPlayer.socket.receive(newPlayer.getreceived_packet());
            } catch (SocketTimeoutException e) {
                String receivedMsg = new String(newPlayer.getreceived_packet().getData(), StandardCharsets.UTF_8);
                byte[] trimmed = Player.trim(newPlayer.getreceived_packet().getData());
                receivedMsg = new String(trimmed);
                // check if the msg is from a different program
                String[] split = receivedMsg.split(":");

                if(split.length>1 && newPlayer.getfoundPlayer() == false){
                    // if time out , send a new msg to the same port
                    try {
                        newPlayer.socket.send(selfpacket);
                    } catch (IOException e1) {
                        
                        e1.printStackTrace();
                    } // send a new msg to the same port
                    }

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
            
            String receivedMsg = new String(newPlayer.getreceived_packet().getData(), StandardCharsets.UTF_8);
            byte[] trimmed = Player.trim(newPlayer.getreceived_packet().getData());
            receivedMsg = new String(trimmed);
            // check if the msg is from a different program
            String[] split = receivedMsg.split(":");

            // validate the received msg
            if ( split.length > 1 ) {

                int tcpport = Integer.parseInt(split[1]);

                if(tcpport != newPlayer.myPort && !split[0].contains("PLAYER FOUND")){
                    // when the received port number is different , establish a TCP connection and
                    // send response back.
                    System.out.println("Received: " + receivedMsg);

                    // player is now found
                    // send udp packet to the sender (so the sender know connection has been found)
                    try {
                        DatagramPacket self_packet = new DatagramPacket(
                                new String("PLAYER FOUND:" + newPlayer.myPort).getBytes(),
                                new String("PLAYER FOUND:" + newPlayer.myPort).getBytes().length,
                                newPlayer.getreceived_packet().getAddress(), newPlayer.getreceived_packet().getPort());

                        newPlayer.socket.send(self_packet);

                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                    newPlayer.setfoundPlayer(true);
                    newPlayer.setTCP_port(newPlayer.myPort);

                    // initial a TCP connection using current myPort
                    Player.initialTCP(newPlayer.myPort);

                    break;

                }else if(tcpport != newPlayer.myPort && split[0].contains("PLAYER FOUND")){
                    newPlayer.setfoundPlayer(true);
                    Player.TCP_transmit(newPlayer.getBroadcastAddress(), tcpport);
                }


              
                

            }

            



        }

   

    }

}