

/**
 *
 * @author Felipe
 */
public class TableLine {
    
    private String ipAddr;
    private int port;
    private String ipAddrGetway;
    
    public TableLine(String ipAddr,int port,String ipAddrGetway){
        this.ipAddr  = ipAddr;
        this.port  = port;
        this.ipAddrGetway  = ipAddrGetway;
    }
    
    public String getIpAddr(){
        return this.ipAddr;
    }

    public int getPort(){
        return this.port;
    }

    public String ipAddrGetway(){
        return this.ipAddr;
    }
}
