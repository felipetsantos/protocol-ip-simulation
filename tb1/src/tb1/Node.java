import org.jcsp.lang.Alternative;
import org.jcsp.lang.AltingChannelInput;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.ChannelOutput;
import org.jcsp.lang.Guard;

/**
 * Classe que representa um Nodo (ou host) nas topologias de rede. Estes objetos
 * sao capazes de receber e enviar dados para rede.
 * 
 * @author Tiago Ferreto
 * @author Ramon Costi Fernandes <ramon.fernandes@acad.pucrs.br>
 */
public class Node implements CSProcess {

	// Nome do host/nodo;
	protected String nodeName;
        
   
        
        
        protected NetworkInterface interfaces[];
        
	public Node(String nodeName) {
		
		this.nodeName = nodeName;
	} 
	/**
	 * Retorna o nome deste host.
	 * 
	 * @return Retorna um objeto {@link String} com o nome deste host.
	 */
	public String getNodeName() {
		return this.nodeName;
	}
                
        protected Guard[] getInterfaces(){
            Guard[] guard = new Guard[interfaces.length];
            int i=0;
            for(NetworkInterface inter:interfaces){
                guard[i] = inter.getIn();
                i++;
            }
            return guard;
        }
        
        public void setNetworkConfig(NetworkConfig config){
            this.interfaces = config.interfaces;
        }
	/**
	 * Verifica se o pacote recebido esta destinado a este nodo.
	 * 
	 * @param pkt Objeto {@link Packet} recebido;
	 * @return <code>true</code> se o pacote foi destinado a este nodo;
	 *         <code>false</code> caso contrario;
	 */
	protected boolean checkPkt(Packet pkt) {
		
                if (pkt == null)
			return false;
		for(int i=0; i< this.interfaces.length;i++){
                    if (pkt.dstIpAddr.equalsIgnoreCase(this.interfaces[i].getIpAddr()))
                            return true;
                }
		//if (pkt.dstIpAddr.equalsIgnoreCase(Constants.macBcast))
		//	return true;
		return false;
	}


	@Override
	public void run() {
		System.out.println(this.nodeName + " Online.");
		
	
		final Guard[] guard = this.getInterfaces();
              
		final Alternative alt = new Alternative(guard);

		while (true) {
                        int numberInterface = alt.fairSelect();
                        this.processIn(numberInterface);
		}
	}
        
        
        protected void processIn(int interfaceNumber){
                                        
                            	int port = interfaceNumber;
                                Packet pkt;
                                pkt = (Packet) interfaces[port].getIn().read();
				//if (checkPkt(pkt)){
					this.recivePkt(pkt,port);
                                
                                //}
        }
        @Override
        public  String toString() {
            String str = String.format("[%s]", nodeName);
            for(int i=0; i< this.interfaces.length;i++){
                str += String.format("\n\teth%s:IP[%s]",i,this.interfaces[i].getIpAddr());
            }
            return str;
        }


    public void connect(int portA,Node nodeB, int portB, NodeNetworkChannelWrapper chan) {
        
        this.interfaces[portA].setIn(chan.chanA.in());
        this.interfaces[portA].setOut(chan.chanB.out());
        
        nodeB.interfaces[portB].setIn(chan.chanB.in());
        nodeB.interfaces[portB].setOut(chan.chanA.out());
        

    }


    public void addNetworkInterface(NetworkInterface port,int portNumber) {
        this.interfaces[portNumber] = port;
    }


    public NetworkInterface getNetworkInterface(int portNumber) {
       return this.interfaces[portNumber];
    }

  
    protected void forwardPkt(Packet pkt,int port) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    protected  void fragmentPkt(Packet pkt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    protected void recivePkt(Packet pkt,int port) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
