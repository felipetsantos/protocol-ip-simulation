
import org.jcsp.lang.AltingChannelInput;
import org.jcsp.lang.ChannelOutput;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Felipe
 */
public class NetworkInterface {

    private AltingChannelInput in;

    // Canal de saida de dados do nodo;
    private ChannelOutput out;
    
    private int mtu;
    
    private String ipAddr;
    
    
    public String getIpAddr(){
        return ipAddr;
    }

    public void setIpAddr(String ip){
       this.ipAddr = ip;
    }
    
    public int getMtu(){
        return mtu;
    }

    public void setMtu(int mtu){
       this.mtu = mtu;
    }
        
    public void setIn(AltingChannelInput in){
        this.in = in;
    }
    public void setOut(ChannelOutput out){
         this.out  = out;
    }
    
    public AltingChannelInput getIn(){
        return this.in;
    }
    public ChannelOutput getOut(){
        return this.out;
    }
   
    
}
