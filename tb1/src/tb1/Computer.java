import java.util.ArrayList;
import org.jcsp.lang.Guard;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Felipe
 */
public class Computer extends Node {


        /**
         * Canal que conecta o computador ao Console (via terminal);
         */
        public ComputerConsoleChannelWrapper chanConsole;

        /**
        * Buffer para armazenar as mensagens
        */
        private ArrayList<Packet> buffer;

        /**
        * Para criar um computador só é necessário informar o nome
        */
        public Computer(String nodeName) {
            super(nodeName);

        }

        /**
         * Seta o canal de comunicação com o terminal
         */
        public void  setChanConsole(ComputerConsoleChannelWrapper chanConsole){
            this.chanConsole = chanConsole;
        }

        /**
        * Retorna as interfaces de entradas que o computador tem
        * Interfaces de redes e terminais
        */
        protected Guard[] getInterfaces(){
            Guard[] guard = new Guard[interfaces.length+1];

            guard[0] = chanConsole.in;

            for(int i=0;i<this.interfaces.length;i++){
                guard[i+1] = this.interfaces[i].getIn();

            }
            return guard;
        }

        /**
         * Envia uma mensagem qualquer do tipo {@link String} para o endereco Ip
         * especificado via parametro, encapsulando os dados em um objeto
         * {@link Packet}.
         * 
         * @param dstMacAddr Endereco MAC destino da mensagem a ser enviada.
         * @param msg Mensagem a ser encapsulada no pacote.
         */
        private void sendMsg(String dstIpAddr, String msg) {
                Packet pkt = new Packet(dstIpAddr, this.interfaces[0].getIpAddr(), msg);
                this.forwardPkt(pkt, 0);

        }

        /**
         * Imprime o conteudo do pacote recebido.
         * 
         * @param pkt Objeto {@link Packet} com a mensagem a ser impressa.
         */
        private void printPkt(Packet pkt) {

                System.out.printf("[%s] recebeu : %s, de [%s]\n", this.nodeName, pkt.data, pkt.srcIpAddr);
        }

	/**
	 * Interpreta o comando que este nodo deve executar, realizando as operacoes
	 * apropriadas.
	 * 
	 * @param command
	 */
	private void runConsoleCommand(CommandWrapper command) {
		switch (command.cmd) {
		case SENDMSG:
			sendMsg(command.dstMacAddr, command.param);
			break;
		default:
			// NOP
			break;
		}
	}

	@Override
	public void run() {
            super.run();
	}
        /**
         * Processa uma entrada 
         * 
         * @param interfaceNumber Numero da interface {@link int} de entrada a ser lida
         */
        protected void processIn(int interfaceNumber){
       		
			switch (interfaceNumber) {
			// 0 Corresponde ao canal de entrada do console
			case 0:
				CommandWrapper cmdw = (CommandWrapper) chanConsole.in.read();
				runConsoleCommand(cmdw);
				break;
                        default:
                            super.processIn(interfaceNumber-1);
                        }
                        
        }
        
        /**
         * Encaminha o pacote para a porta de saida, se necessário faz a fragmentação
         * @param pkt Pacote {@link Packet} a ser encaminhado
         * @param srcPort Número da porta {@link int} que recebeu o pacote 
         */
        protected void forwardPkt(Packet pkt,int srcPort) {
                    NetworkInterface port = this.interfaces[0];
                    Packet[] pkts;
                    
                    // Testa se é necessário framgmentar o pacote 
                    if(pkt.data.length() > port.getMtu() ){
                        
                        // Fragmenta pacote
                        pkts = pkt.frament(port.getMtu());
                        System.out.println("O "+nodeName+" fragmentou o pacote em "+pkts.length+" partes");
                        // encaminha os pacotes
                        for (Packet pkt1 : pkts) {
                            port.getOut().write(pkt1);
                        }
                    }else{
                        // Como o pacote tem um tamanho menor ou = ao MTU ele pode ser encaminhado sem framgmentação
                        System.out.println("Nao foi necessario fragmentar o pacote");
                        port.getOut().write(pkt);
                    }
                    

                

        }
        
        protected void recivePkt(Packet pkt,int port) {

            if(pkt.offset == 0){
                buffer = new ArrayList<Packet>();
                buffer.add(pkt);
            }else{
                buffer.add(pkt);
            }


            if(!pkt.hasMoreFragment){
                 System.out.printf("[%s] Remontando pacote ...\n",this.nodeName);
                Packet pktRebuiled = pkt.rebuildPkt(buffer);
                this.printPkt(pktRebuiled);
            }else{
                System.out.printf("[%s] recebeu fragmento: [%s] e colocou no buffer...\n", this.nodeName, pkt.data);
            }
        }
    
}
