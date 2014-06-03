

import java.util.Hashtable;
import org.jcsp.lang.Alternative;
import org.jcsp.lang.AltingChannelInput;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.ChannelOutput;
import org.jcsp.lang.Guard;


public class Router extends Node {
    
    
    // tabela de rotas
    private Hashtable<String,TableLine> table;
    

    public Router(String nodeName,Hashtable<String,TableLine> table) {
            super(nodeName);
            this.table = table;
    }
    /**
     * Realiza o encaminhamento dos pacote recebidos pela porta
     * <code>srcPort</code>.
     * Quando o Ip de destino for encontrado na tabela do roteador o pacote é 
     * encaminhado somente para porta que está conectado ao segemnto de rede 
     * que contém o Ip.
     * Se o ip não existir na tabela do roteador o encaminhamento é feito para 
     * todas as portas, controlando para não enviar para a porta de 
     * origem do pacote. 
     * 
     * @param srcPort Porta origem, de onde o pacote foi recebido;
     * @param pkt Pacote a ser encaminhado;
     */
        protected void forwardPkt(Packet pkt,int srcPort) {
            
  
            String str[] = pkt.dstIpAddr.split("\\.");
            String ip = str[0]+"."+str[1]+"."+str[2]+".0";
            // Procura pelo ip de destino na tabela do roteador,
            TableLine line = table.get(ip);
            
            // testa para saber se a porta de destino foi encontrada
            if(line == null){
                // Não encontrou o ip
                System.out.println("Ip desconhecido.");
            }else{
                // encontrou o IP
                
                // manda somente para porta do ip correto
                
                Packet[] pkts;
                    
                    // Testa se é necessário framgmentar o pacote 
                    if(pkt.data.length() > this.interfaces[line.getPort()].getMtu() ){
                        
                        // Fragmenta pacote
                        pkts = pkt.frament(this.interfaces[line.getPort()].getMtu());
                        
                        System.out.println("O "+nodeName+" fragmentou o pacote em "+pkts.length+" partes");
                        // encaminha os pacotes
                        for (Packet pkt1 : pkts) {
                            System.out.println("["+this.nodeName+"] encaminha "+pkt1.toString()+" \n para [PORT="+line.getPort()+"] -[MTU="+interfaces[line.getPort()].getMtu()+"]- [IP="+line.getIpAddr()+"] - [GETWAY="+line.ipAddrGetway()+"]");
                            this.interfaces[line.getPort()].getOut().write(pkt1);
                        }
                    }else{
                        System.out.println("["+this.nodeName+"] encaminha "+pkt.toString()+" \n para [PORT="+line.getPort()+"] -[MTU="+interfaces[line.getPort()].getMtu()+"]- [IP="+line.getIpAddr()+"] - [GETWAY="+line.ipAddrGetway()+"]");
                        // Como o pacote tem um tamanho menor ou = ao MTU ele pode ser encaminhado sem framgmentação
                        this.interfaces[line.getPort()].getOut().write(pkt);
                    }
                
                
                
            }

            
    }
    

           protected void recivePkt(Packet pkt,int port) {
               //System.out.println("["+this.nodeName+"] recebeu: "+pkt.toString());
               this.forwardPkt(pkt, port);
           }

}
