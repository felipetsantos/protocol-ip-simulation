/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Hashtable;
import org.jcsp.lang.AltingChannelInput;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Channel;
import org.jcsp.lang.ChannelOutput;
import org.jcsp.lang.One2OneChannel;
import org.jcsp.lang.Parallel;

/**
 *
 * @author Felipe
 */
public  class Topology {
    
    private  CSProcess[] processes;
    private  Computer[] computers;
    
    
 
    public Computer[] getComputers(){
        return computers;
    }
    public CSProcess[] getProcesses(){
        return processes;
    }
    
    public void setComputers(Computer[] computers){
        this.computers = computers;
    }

    public void setProcesses(CSProcess[] processes){
        this.processes = processes;
    }
    private static NetworkConfig getComputerConfig(String ip,int mtu,NodeNetworkChannelWrapper chan){
        NetworkConfig configComputer = new NetworkConfig();
        configComputer.numberOfInterfaces = 1;
        NetworkInterface  eth0 = new NetworkInterface();
        eth0.setMtu(mtu);
        eth0.setIpAddr(ip);
        NetworkInterface[] confiComputer0Eths = {eth0}; 
        configComputer.interfaces = confiComputer0Eths;
        return configComputer;
        //computers[numberOfComputer].setNetworkConfig(configComputer);
    }
    
    private static NetworkConfig getRouterConfig(String[] ips,int[] mtus){

        NetworkConfig configRouter0 = new NetworkConfig();
        configRouter0.numberOfInterfaces = 3;
        NetworkInterface eth0 = new NetworkInterface();
        NetworkInterface  eth1 = new NetworkInterface();
        NetworkInterface  eth2 = new NetworkInterface();
        
        eth0.setMtu(mtus[0]);
        eth0.setIpAddr(ips[0]);
        
        eth1.setMtu(mtus[1]);
        eth1.setIpAddr(ips[1]);
        
        eth2.setMtu(mtus[2]);
        eth2.setIpAddr(ips[2]);

        NetworkInterface[] confiRouter0Eths = {eth0,eth1,eth2}; 
        configRouter0.interfaces = confiRouter0Eths;
        return configRouter0;
    }
  
    
    /**
     * Método que retorna a primeira topologia proposta no trabalho
     * @return Objeto {@link Topology} que representa a primeira topologia do trabalho
     */
    public static Topology getTopology1(){
        
        Computer computers[] = new Computer[4];
        final One2OneChannel cons[] = new One2OneChannel[4];
        ComputerConsoleChannelWrapper consoleChans[] = new ComputerConsoleChannelWrapper[4];
        
        Node routers[] = new Node[2];
        
        NodeNetworkChannelWrapper[] networkChans = new NodeNetworkChannelWrapper[5];
        
        // Cria os computadores e conecta eles aos consoles
        for(int i=0;i<4;i++){
            computers[i] = new Computer("Computador "+i);
            cons[i] = Channel.one2one();
            consoleChans[i] = new ComputerConsoleChannelWrapper(computers[i],cons[i].out(),cons[i].in());
            computers[i].setChanConsole(consoleChans[i]);
        }

        
            // Table R0
            TableLine rows[] = new TableLine[5];
            rows[0] = new TableLine("10.0.1.0",0,"0.0.0.0");
            rows[1] = new TableLine("10.0.2.0",1,"0.0.0.0");
            rows[2] = new TableLine("10.0.3.0",2,"10.0.10.2"); 
            rows[3] = new TableLine("10.0.4.0",2,"10.0.10.2"); 
            rows[4] = new TableLine("10.0.10.2",2,"0.0.0.0");
            Hashtable<String,TableLine> tableR0 = new Hashtable<String,TableLine>();
            
            for(int i=0;i<rows.length;i++){
                tableR0.put(rows[i].getIpAddr(), rows[i]);
            }
   
            // Table R2
            rows = new TableLine[5];
            rows[0] = new TableLine("10.0.3.0",0,"0.0.0.0");
            rows[1] = new TableLine("10.0.4.0",1,"0.0.0.0");
            rows[2] = new TableLine("10.0.1.0",2,"10.0.10.1"); 
            rows[3] = new TableLine("10.0.2.0",2,"10.0.10.1"); 
            rows[4] = new TableLine("10.0.10.1",2,"0.0.0.0");
            Hashtable<String,TableLine> tableR1 = new Hashtable<String,TableLine>();
            
            
            for(int i=0;i<rows.length;i++){
                tableR1.put(rows[i].getIpAddr(), rows[i]);
            }  
            Hashtable[] tables = {tableR0,tableR1};
           


        
            
        // Cria os roteadores
        for(int i=0;i<2;i++){
            routers[i] = new Router("Router "+i,tables[i]);
        }
 
        // Cria os Canais
        for(int i=0;i<5;i++){
            networkChans[i] = new NodeNetworkChannelWrapper();
        }
        
        // Computer0
        computers[0].setNetworkConfig(getComputerConfig("10.0.1.1",20,networkChans[0]));
        // Computer1
        computers[1].setNetworkConfig(getComputerConfig("10.0.2.1",10,networkChans[1]));
        // Computer2
        computers[2].setNetworkConfig(getComputerConfig("10.0.3.1",15,networkChans[3]));
        // Computer3
        computers[3].setNetworkConfig(getComputerConfig("10.0.4.1",5,networkChans[4]));
        
        //Router   
        String[] ips = {"10.0.1.2","10.0.2.2","10.0.10.1"};
        int[] mtus = {20,10,15};
        routers[0].setNetworkConfig(getRouterConfig(ips,mtus));
        //Router 1
        String[] ips1 = {"10.0.3.2","10.0.4.2","10.0.10.2"};
        int[] mtus1 = {20,10,15};
        routers[1].setNetworkConfig(getRouterConfig(ips1,mtus1));
        
        // Conecta a porta 0 do computador 0 na porta 0 do  roteador 0.
        //Utiliza o canal 0 para a conexão
        computers[0].connect(0, routers[0], 0,networkChans[0]);
        // Conecta a porta 0 do computador 1 na porta 1 do  roteador 0
        // Utiliza o canal 1 para a conexão
        computers[1].connect(0, routers[0], 1,networkChans[1]);
        // Conecta a porta 2 do roteador 0 na porta 2 do roteador 1
        // Utiliza o canal 2 para a conexão
        routers[0].connect(2, routers[1], 2, networkChans[2]);
        // Conecta a porta 0 do computador 2 na porta 0 do roteador 1
        // Utiliza o canal 3 para a conexão
        computers[2].connect(0, routers[1], 0,networkChans[3]);
        // Conecta a porta 0 do computador 3 na porta 1 do roteador 1
        // Utiliza o canal 4 para a conexão
        computers[3].connect(0, routers[1], 1,networkChans[4]);
        
        
        
        Topology tp = new Topology();
        tp.setComputers(computers);
        CSProcess[] processes = {computers[0],computers[1],computers[2],computers[3],routers[0],routers[1]};
        tp.setProcesses(processes);
        return tp;
    }
    
     /**
     * Método que retorna a segunda topologia proposta no trabalho
     * @return Objeto {@link Topology} que representa a segunda topologia do trabalho
     */
    public static Topology getTopology2(){
        Computer computers[] = new Computer[4];
        final One2OneChannel cons[] = new One2OneChannel[4];
        ComputerConsoleChannelWrapper consoleChans[] = new ComputerConsoleChannelWrapper[4];
        
        Node routers[] = new Node[4];
        
        NodeNetworkChannelWrapper[] networkChans = new NodeNetworkChannelWrapper[8];
        
        // Cria os computadores e conecta eles aos consoles
        for(int i=0;i<4;i++){
            computers[i] = new Computer("Computador "+i);
            cons[i] = Channel.one2one();
            consoleChans[i] = new ComputerConsoleChannelWrapper(computers[i],cons[i].out(),cons[i].in());
            computers[i].setChanConsole(consoleChans[i]);
        }

        
            // Table R0 (R1 - modelo)
            TableLine rows[] = new TableLine[6];
            rows[0] = new TableLine("10.0.1.0",0,"0.0.0.0");
            rows[1] = new TableLine("10.0.2.0",1,"10.0.10.2");
            rows[2] = new TableLine("10.0.4.0",1,"10.0.10.2"); 
            rows[3] = new TableLine("10.0.3.0",2,"10.0.30.2");
            rows[4] = new TableLine("10.0.10.2",1,"0.0.0.0");
            rows[5] = new TableLine("10.0.30.2",2,"0.0.0.0");
            Hashtable<String,TableLine> tableR0 = new Hashtable<String,TableLine>();
            
            for(int i=0;i<rows.length;i++){
                tableR0.put(rows[i].getIpAddr(), rows[i]);
            }
   
            // Table R1 (R2 - modelo)
            rows = new TableLine[6];
            rows[0] = new TableLine("10.0.2.0",0,"0.0.0.0");
            rows[1] = new TableLine("10.0.1.0",1,"10.0.10.1");
            rows[2] = new TableLine("10.0.4.0",2,"10.0.20.2"); 
            rows[3] = new TableLine("10.0.3.0",1,"10.0.10.1"); 
            rows[4] = new TableLine("10.0.10.1",1,"0.0.0.0");
            rows[5] = new TableLine("10.0.20.2",2,"0.0.0.0");
            
            Hashtable<String,TableLine> tableR1 = new Hashtable<String,TableLine>();
            
            
            for(int i=0;i<rows.length;i++){
                tableR1.put(rows[i].getIpAddr(), rows[i]);
            }  
            
            
            // Table R2 (R3 - modelo)
            rows = new TableLine[6];
            rows[0] = new TableLine("10.0.3.0",0,"0.0.0.0");
            rows[1] = new TableLine("10.0.1.0",1,"10.0.30.1");
            rows[2] = new TableLine("10.0.4.0",2,"10.0.40.1"); 
            rows[3] = new TableLine("10.0.2.0",2,"10.0.40.1");
            rows[4] = new TableLine("10.0.30.1",1,"0.0.0.0");
            rows[5] = new TableLine("10.0.40.1",2,"0.0.0.0");
            
            Hashtable<String,TableLine> tableR2 = new Hashtable<String,TableLine>();
            
            for(int i=0;i<rows.length;i++){
                tableR2.put(rows[i].getIpAddr(), rows[i]);
            }
            
                        
            // Table R3 (R4 - modelo)
            rows = new TableLine[6];
            rows[0] = new TableLine("10.0.4.0",0,"0.0.0.0");
            rows[1] = new TableLine("10.0.2.0",1,"10.0.20.1");
            rows[2] = new TableLine("10.0.3.0",2,"10.0.40.2"); 
            rows[3] = new TableLine("10.0.1.0",2,"10.0.40.2");
            rows[4] = new TableLine("10.0.20.1",1,"0.0.0.0");
            rows[5] = new TableLine("10.0.40.2",2,"0.0.0.0");
            
            Hashtable<String,TableLine> tableR3 = new Hashtable<String,TableLine>();
            
            for(int i=0;i<rows.length;i++){
                tableR3.put(rows[i].getIpAddr(), rows[i]);
            }
            
            
            Hashtable[] tables = {tableR0,tableR1,tableR2,tableR3};
           
            
        // Cria os roteadores
        for(int i=0;i<4;i++){
            routers[i] = new Router("Router "+i,tables[i]);
        }
 
        // Cria os Canais
        for(int i=0;i<8;i++){
            networkChans[i] = new NodeNetworkChannelWrapper();
        }
        
        // Computer0
        computers[0].setNetworkConfig(getComputerConfig("10.0.1.1",20,networkChans[0]));
        // Computer1
        computers[1].setNetworkConfig(getComputerConfig("10.0.2.1",10,networkChans[2]));
        // Computer2
        computers[2].setNetworkConfig(getComputerConfig("10.0.3.1",10,networkChans[6]));
        // Computer3
        computers[3].setNetworkConfig(getComputerConfig("10.0.4.1",15,networkChans[4]));
        
        //Router (R1 - Modelo)
        String[] ips = {"10.0.1.2","10.0.10.1","10.0.30.1"};
        int[] mtus = {20,15,5};
        routers[0].setNetworkConfig(getRouterConfig(ips,mtus));
        //Router 1 (R2 - Modelo)
        String[] ips1 = {"10.0.2.2","10.0.10.2","10.0.20.1"};
        int[] mtus1 = {10,15,10};
        routers[1].setNetworkConfig(getRouterConfig(ips1,mtus1));
        //Router 2 (R3 - Modelo)
        String[] ips2 = {"10.0.3.2","10.0.30.2","10.0.40.2"};
        int[] mtus2 = {10,5,20};
        routers[2].setNetworkConfig(getRouterConfig(ips2,mtus2));
        //Router 3 (R4 - Modelo)
        String[] ips3 = {"10.0.4.2","10.0.20.2","10.0.40.1"};
        int[] mtus3 = {15,10,20};
        routers[3].setNetworkConfig(getRouterConfig(ips3,mtus3));
        
        
        // Conecta a porta 0 do computador 0 na porta 0 do  roteador 0.
        //Utiliza o canal 0 para a conexão
        computers[0].connect(0,routers[0], 0,networkChans[0]);
        
        computers[1].connect(0,routers[1], 0,networkChans[2]);
        computers[2].connect(0,routers[2], 0,networkChans[6]);
        computers[3].connect(0,routers[3], 0,networkChans[4]);
       
        routers[0].connect(1,routers[1], 1, networkChans[1]);
        routers[0].connect(2,routers[2], 1, networkChans[7]);
        
        routers[1].connect(2,routers[3], 1, networkChans[3]);
        routers[2].connect(2,routers[3], 2, networkChans[5]);
        
        
        
        Topology tp = new Topology();
        tp.setComputers(computers);
        CSProcess[] processes = {computers[0],computers[1],computers[2],computers[3],routers[0],routers[1],routers[2],routers[3]};
        tp.setProcesses(processes);
        return tp;
    }
    
    
    
}
