
import java.util.ArrayList;
import java.util.UUID;

/**
 * Classe que define um pacote do trabalho de redes, utilizado para a
 * comunicacao entre processos.
 * 
 * @author Tiago Ferreto
 * @author Ramon Costi Fernandes <ramon.fernandes@acad.pucrs.br>
 */
public class Packet {


	public String data;
	public String dstIpAddr;
	public String srcIpAddr;
        public String identification;
        public int size;
        public int offset;
        public boolean hasMoreFragment;
	/**
	 * Cria um pacote com o endereco MAC destino e destino, assim como os dados
	 * que devem ser enviados.
	 * 
	 * @param dstIpAddr Endereco MAC destino a ser preenchido no pacote.
	 * @param srcIpAddr Endereco MAC origem a ser preenchido no pacote.
	 * @param data Dados que devem ser encapsulados no pacote.
	 */
	public Packet(String dstIpAddr, String srcIpAddr, String data) {
		super();
                
                this.identification = UUID.randomUUID().toString();
                this.size = data.length();
                this.hasMoreFragment = false;
                this.offset = 0;
		this.dstIpAddr = dstIpAddr;
		this.srcIpAddr = srcIpAddr;
		this.data = data;
	}
        
        public Packet[] frament(int mtu){
            
            int numberOfPackts = (int)Math.ceil(((double)this.data.length()/(double)mtu));
            Packet[] pkts = new Packet[numberOfPackts];
            int fragmentBegin = 0;
            int fragmentEnd = mtu;
            int localOffset = this.offset;
            for(int i=0;i<numberOfPackts;i++){
                
                String framgment = this.data.substring(fragmentBegin,fragmentEnd);
                pkts[i] = new Packet(this.dstIpAddr, this.srcIpAddr, framgment);
                pkts[i].size = this.size;
                pkts[i].offset = localOffset;
                pkts[i].identification = this.identification;
                
                if(localOffset+mtu > this.size){
                    localOffset = this.size;
                }else{
                    localOffset =localOffset+mtu;
                }
                
                if(this.hasMoreFragment){
                    pkts[i].hasMoreFragment = true;
                }else{
                    if(i== numberOfPackts-1){
                        pkts[i].hasMoreFragment = false;
                    }else{
                        pkts[i].hasMoreFragment = true;
                    }
                }
                
                
                fragmentBegin = fragmentBegin+mtu;
                fragmentEnd = fragmentEnd+mtu;
                if(fragmentEnd > this.data.length()){
                    fragmentEnd = this.data.length();
                }
            }
            
            return pkts;
        }
  
        public Packet rebuildPkt(ArrayList<Packet> pkts){
            String localdata = "";
            for (Packet pkt : pkts) {
                localdata += pkt.data;
            }
            Packet pkt = new Packet(this.dstIpAddr, this.srcIpAddr, localdata);
            pkt.size = localdata.length();
            pkt.offset = 0;
            pkt.identification = this.identification;
            pkt.hasMoreFragment =  false;
            
            return pkt;
        }
        
	@Override
	public String toString() {
		String str = "Packet [\n\t"
                        + "ID=" + identification + ", FLAG=" + hasMoreFragment + ", offset=" + offset + ", size=" + size + "\n\t]"
                        + "dstIpAddr=" + dstIpAddr + ", srcIpAddr=" + srcIpAddr + ", data=" + data + "\n]";
                return str;
	}

}
