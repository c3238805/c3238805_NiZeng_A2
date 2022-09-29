/*  
    /============================\
    |  COMP2230 Assignment       | 
    |  Name : Ni Zeng            | 
    |  Student Number : c3238805 |
    \============================/   */
    

public class Node {
    private int row;
    private int collum;
    private String plotString;
    private String node_display;
    private boolean isTaken;
    private String ship_display;

    public Node(){
        
    }

    public Node(int row,int collum) throws Exception{
        this.node_display = "  ";
        this.ship_display = "  ";
        this.row = row;
        this.collum = collum;
        this.plotString = String.valueOf(int_to_char(row)) + collum;
        this.isTaken = false;
    }

    public int getrow(){
        return this.row;
    }

    public int getcollum() {
        return this.collum;
    }

    public char int_to_char(int i) throws Exception {

        char c;
        int counter = 0;
        boolean found = false;

        for (c = 'A'; c <= 'Z'; ++c){
            
            if(counter == i){
                //found the match char
                found = true;
                break;
            }

            counter++;
        }

        if(!found){
            throw new Exception("Unable to match integer with character");
        }
        return c;
    }

    public void setNodedisplay(String node_display){

        this.node_display = node_display;
    }
    public String getNodedisplay(){
        return this.node_display;
    }

    public void setShipdisplay(String ship_display){
        this.ship_display = ship_display;
    }  
    public String getShipdisplay() {
        return this.ship_display ;
    }


    public String getPlotString(){
            return this.plotString; 
    }

    public void setisTaken(boolean isTaken){
        this.isTaken = isTaken;
    }

    public boolean getisTaken(){
        return isTaken;
    }


    
}
