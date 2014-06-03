
import org.jcsp.lang.Channel;
import org.jcsp.lang.One2OneChannel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

   

/**
 *
 * @author Felipe
 */
public class NodeNetworkChannelWrapper {
          public  One2OneChannel chanA;
          public   One2OneChannel chanB;
        
      public NodeNetworkChannelWrapper(){
              this.chanA = Channel.one2one();
              this.chanB = Channel.one2one();
      }
}
