
/*  
    /============================\
    |  COMP2230 Assignment       | 
    |  Name : Ni Zeng            | 
    |  Student Number : c3238805 |
    \============================/   */

import java.util.Random;

public class BattleShip {
    
    private Node[][] graph;
    private int rows;
    private int collums;
    private Node foundNode;
    private Node [] Aircraft;
    private Node[] Battleship;
    private Node[] Cruiser;
    private Node[] Patrol;
    private Node[] Submarine;
    


    public BattleShip() {
        this.rows = 10;
        this.collums = 10;
        this.graph = new Node[rows][collums];
        GenerateGraph(rows, collums);
        SetShip();
    }

    public void record_board(){
        
        for(Node n:Aircraft){

            n.setShipdisplay("  ");
        }
        for (Node n : Battleship) {
            n.setShipdisplay("  ");
        }
        for (Node n : Cruiser) {

            n.setShipdisplay("  ");
        }
        for (Node n : Patrol) {

            n.setShipdisplay("  ");
        }
        for (Node n : Submarine) {

            n.setShipdisplay("  ");
        }

    }

    public void update_record(String nodeDisplay,String updateString){

        Node node = findNode(nodeDisplay);
        node.setShipdisplay(updateString);

    }


    public void GenerateGraph(int row, int collum){

        for(int i=0;i<row;i++){
            for(int j=0;j<collum;j++){
                graph[i][j] = new Node(i,j);   //initial new node 
            }
        }

    }

    // this method is to randomly set all ship into the graph
    public void SetShip(){

        // Aircraft carrier (5) 
        Aircraft = RandomPlaceShip(" A",5);
        
        // Battleship (4) 
        Battleship = RandomPlaceShip(" B", 4);
        
        // Cruiser (3) 
        Cruiser = RandomPlaceShip(" C", 3);

        //Patrol Boat (2) 
        Patrol = RandomPlaceShip(" P", 2);
        
        // Submarine (3)
        Submarine = RandomPlaceShip(" S", 3);
        

    }
    
    public Node[] RandomPlaceShip(String ship_display, int space){
       
        Node [] ship = new Node[space];
       
        // there are maximum 4 possible direction: up,down,left,right
        // first pick a random plot extend to random direction with required space
        ship = RandomDirection(pickRandomPlot(), space);    // this function will run until find a suitable place for a ship
        // update grid's information 
        for(Node node:ship){
            node.setShipdisplay(ship_display);  // update the ship display in node
            node.setisTaken(true);
        }

        return ship;
    }

    // this method will return random direction repesent by integer: 0=up , 1=down , 2=left, 3=right
    public Node[] RandomDirection(Node origin,int space){

        boolean directionClear = false;
        // there are maximum 4 possiable direction: up,down,left,right
        int rnd = new Random().nextInt(4);

        Node [] shipNode = new Node[space];
        Node currentNode = new Node();  //initial a node to hold the current node
        //check node to see if its avaliable to put down a ship
        switch(rnd){
            case 0:     //search upwards
                        shipNode[0] = origin; // add oringin node to shipNode

                        for(int i=0;i<space - 1;i++){
                            Node node = findNode(origin.getrow()-(i+1),origin.getcollum());
                            //find the node which on the top of current node
                            if(node != null && node.getisTaken()== false && node != currentNode){
                                shipNode[i+1] = node;
                                currentNode = node;
                                continue;
                            }else {
                                break;  // break out the loop, there is no enough space for a ship
                            }
                        }
                        //if all pass the loop without breaking off the loop, update directionClear
                        directionClear = true;
                        break;

            case 1:     // search downwards
                        shipNode[0] = origin; // add oringin node to shipNode
                        for (int i = 0; i < space - 1; i++) {
                            Node node = findNode(origin.getrow() + (i + 1), origin.getcollum());
                            // find the node which on the top of current node
                            if ( node != null && node.getisTaken() == false && node != currentNode) {
                                shipNode[i+1] = node;
                                currentNode = node;
                                continue;
                            } else {
                                break; // break out the loop, there is no enough space for a ship
                            }
                        }
                        // if all pass the loop without breaking off the loop, update directionClear
                        directionClear = true;
                        break;

            case 2:     // search left
                        shipNode[0] = origin; // add oringin node to shipNode
                        for (int i = 0; i < space - 1; i++) {
                            Node node = findNode(origin.getrow(), origin.getcollum() - (i + 1));
                            // find the node which on the top of current node
                            if (node != null && node.getisTaken() == false && node != currentNode) {
                                shipNode[i+1] = node;
                                currentNode = node;
                                continue;
                            } else {
                                break; // break out the loop, there is no enough space for a ship
                            }
                        }
                        // if all pass the loop without breaking off the loop, update directionClear
                        directionClear = true;
                        break;

            case 3:     // search right
                        shipNode[0] = origin; // add oringin node to shipNode
                        for (int i = 0; i < space-1; i++) {
                            Node node = findNode(origin.getrow() , origin.getcollum() + (i + 1));
                            // find the node which on the top of current node
                            if (node != null && node.getisTaken() ==false && node != currentNode) {
                                shipNode[i+1] = node;
                                currentNode = node;
                                continue;
                            } else {
                                break; // break out the loop, there is no enough space for a ship
                            }
                        }
                        // if all pass the loop without breaking off the loop, update directionClear
                        directionClear = true;
                        break;

        }

        for(Node node: shipNode){
            if(node == null){
                directionClear = false; //when there is a node = null, direction is not clear
                break;
            }
        }

        if(directionClear == false){
            // while loop run untill find a spot for the ship
            shipNode = RandomDirection(pickRandomPlot(), space);
        }

        
        return shipNode;
    }

    public Node pickRandomPlot(){

        Node pickPlot = new Node();

        int random_row = new Random().nextInt(rows);
        int random_collums = new Random().nextInt(collums);
        boolean found = false;
        for (Node[] node : graph) {
            for (Node n : node) {
                if (n.getrow() == random_row && n.getcollum() == random_collums && n.getisTaken() == false) {
                    // found the node
                    found = true;
                    pickPlot = n;
                    break;
                }
            }
        }

        if (!found) {
            pickPlot = pickRandomPlot();
        }

        return pickPlot;
    }

    public Node findNode(int row, int collum){

        //Node foundNode = new Node();
        for(Node[] node:graph){
            for(Node n:node){
                if(n.getrow() == row && n.getcollum() == collum){
                    foundNode = n;
                    break;
                }
            }
        }
        return foundNode;
    }

    public  String printGraph() {
        String mazeString = "_";

        for (int i = 0; i < graph.length; i++) {
            // this part to form a top bar of the graph
            if (i != 0) {
                mazeString += "\n";
            } else if (i == 0 || i == graph.length - 1) {
                for (int rowsCount = 0; rowsCount < rows; rowsCount++) {
                    mazeString += "_____";
                }
                mazeString += "\n";
                
            }
 
            // this part to form the rest part of the graph
            for (int j = 0; j < graph[i].length; j++) {
               if(j == 0){
                   mazeString += "|"+graph[i][j].getShipdisplay()+"  |";
               }   
               else {
                    mazeString += graph[i][j].getShipdisplay()+"  |";
               }
                

                if(j == graph[i].length-1){
                    mazeString += "\n";
                    //construct another line so the graph look better
                    for (int a = 0; a < graph[i].length; a++) {
                        if (a == 0) {
                            mazeString += "|____|";
                        } else {
                            mazeString += "____|";
                        }

                    }
                }
                
            }

            
            
        }

        return mazeString;
    }

    public String fire(String plot){

        String reply = "";
        Node findNode = new Node();

        if(validPlot(plot)){
            findNode = findNode(plot); // then find the coresponse node
            
            if(findNode.getShipdisplay().matches("  ") ){

                findNode.setNodedisplay(" X");

                reply = "MISS:" + findNode.getPlotString();

            }else if (findNode.getShipdisplay().matches(" A")){

                findNode.setShipdisplay(" X");
                if ((isSunk(Aircraft) == true) && isGameOver() == false) {

                    reply = "SUNK:" + findNode.getPlotString() + ":Aircraft carrier";
                }
                else if ((isSunk(Aircraft) == true) && isGameOver() == true) {

                    reply = "GAME OVER:" + findNode.getPlotString() + ":Aircraft carrier";
                }else{
                    reply = "HIT:"+findNode.getPlotString();
                }
                
            } else if (findNode.getShipdisplay().matches(" B")) {
                
                findNode.setShipdisplay(" X");
                if ((isSunk(Battleship) == true) && isGameOver() == false) {

                    reply = "SUNK:" + findNode.getPlotString() + ":Battleship";
                }
                else if ((isSunk(Battleship) == true) && isGameOver() == true) {

                    reply = "GAME OVER:" + findNode.getPlotString() + ":Battleship";
                } else {
                    reply = "HIT:" + findNode.getPlotString();
                }

            } else if (findNode.getShipdisplay().matches(" C")) {
                
                findNode.setShipdisplay(" X");
                if ((isSunk(Cruiser) == true) && isGameOver() == false) {

                    reply = "SUNK:" + findNode.getPlotString() + ":Cruiser";
                }
                else if ((isSunk(Cruiser) == true) && isGameOver() == true) {

                    reply = "GAME OVER:" + findNode.getPlotString() + ":Cruiser";
                } else {
                    reply = "HIT:" + findNode.getPlotString();
                }

            } else if (findNode.getShipdisplay().matches(" P")) {
                
                findNode.setShipdisplay(" X");
                if ((isSunk(Patrol) == true) && isGameOver() == false) {

                    reply = "SUNK:" + findNode.getPlotString() + ":Patrol";
                } else if ((isSunk(Patrol) == true) && isGameOver() == true) {

                    reply = "GAME OVER:" + findNode.getPlotString() + ":Patrol";
                } else {
                    reply = "HIT:" + findNode.getPlotString();
                }

            } else if (findNode.getShipdisplay().matches(" S")) {
                
                findNode.setShipdisplay(" X");
                if ((isSunk(Submarine) == true) && isGameOver() == false) {

                    reply = "SUNK:" + findNode.getPlotString() + ":Submarine";
                }
                else if ((isSunk(Submarine) == true) && isGameOver() == true) {

                    reply = "GAME OVER:" + findNode.getPlotString() + ":Submarine";
                } 
                else {
                    reply = "HIT:" + findNode.getPlotString();
                }
            } else if (findNode.getShipdisplay().matches(" .")) {
                reply = "MISS:" + findNode.getPlotString();
            } else if (findNode.getShipdisplay().matches(" X")) {
                reply = "HIT:" + findNode.getPlotString();
            } 

        }else {
            reply= "Invalid:I don't know.";
        }

        return reply;

    }

    public boolean isSunk(Node [] ship){
        boolean isSunk = true;

        for(Node n: ship){
            if(!n.getShipdisplay().matches(" X")){
                
                isSunk = false;
                break;
            }
        }

        return isSunk;

    }

    public boolean isGameOver(){
        boolean isGameOver = true;

        for (Node n : Aircraft) {
            if(!n.getShipdisplay().equals(" X")){
                isGameOver = false;
                return isGameOver;
            }
        }
        for (Node n : Battleship) {
            if (!n.getShipdisplay().equals(" X")) {
                isGameOver = false;
                return isGameOver;
            }
        }
        for (Node n : Cruiser) {
            if (!n.getShipdisplay().equals(" X")) {
                isGameOver = false;
                return isGameOver;
            }
        }
        for (Node n : Patrol) {
            if (!n.getShipdisplay().equals(" X")) {
                isGameOver = false;
                return isGameOver;
            }
        }
        for (Node n : Submarine) {
            if (!n.getShipdisplay().equals(" X")) {
                isGameOver = false;
                return isGameOver;
            }
        }

        return isGameOver;
    }

    // this method is to check if the user entered plot is valid
    public boolean validPlot(String plot){

        boolean validPlot =false;

        for (Node[] nodes : graph) {
            for (Node n : nodes) {
                if (n.getPlotString().equals(plot)) {
                    // node found
                    validPlot = true;
                    return validPlot;
                }
            }
        }

        return validPlot;

    }

    public Node findNode(String plot){

        Node findNode = new Node();

        for (Node[] nodes : graph) {
            for (Node n : nodes) {
                if (n.getPlotString().equals(plot)) {
                    // node found
                    findNode = n;
                    break;
                }
            }
        }

        return findNode;

    }



}
