import java.util.Hashtable;
import org.jcsp.lang.AltingChannelInput;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Channel;
import org.jcsp.lang.ChannelOutput;
import org.jcsp.lang.One2OneChannel;
import org.jcsp.lang.Parallel;

/**
 * 
 * @author Tiago Ferreto
 * @author Ramon Costi Fernandes <ramon.fernandes@acad.pucrs.br>
 */
public class Main {

	public static void main(String[] args) {

            CommandConsole console = new CommandConsole(); 
            Topology tp = console.selectTopology();
            CSProcess[] TPprocesses = tp.getProcesses();
            
            CSProcess[] processes = new CSProcess[TPprocesses.length+1];
            for(int i =0;i<TPprocesses.length;i++){
                processes[i] = TPprocesses[i];
            }
            processes[TPprocesses.length] = console;
            CSProcess simulator = new Parallel(processes);
            simulator.run();  
            
            
        }
        
}
