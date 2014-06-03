/**
 * Classe para representar as constantes utilizadas no simulador.
 * 
 * @author Tiago Ferreto
 * @author Ramon Costi Fernandes <ramon.fernandes@acad.pucrs.br>
 */
public class Constants {

	// Endereco Ip para broadcast.
	public static final String ipBcast = "10.0.0.0";

	// Comando SENDMSG
	public static final int sendMsgCmd = 0;


	// Lista com os possiveis comandos a serem executados
	public static final Commands[] cmds = { Commands.SENDMSG};

	// Tempo de sleep para os prints (em milisegundos)
	public static final int sleepTime = 250;
}
