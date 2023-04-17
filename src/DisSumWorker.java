/* ---------------------------------------------------------------
Práctica 1.
Código fuente: MsgRMI.java
Grau Informàtica
48053151X Pol Escolà
49263877Q Antonio López Gómez
--------------------------------------------------------------- */
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.StringTokenizer;
import java.util.concurrent.CyclicBarrier;
import static java.lang.System.exit;

public class DisSumWorker implements TopicListenerInterface{
    long tareas_calculadas;
    static MsgQClient client;
    static volatile boolean done=false;
    public DisSumWorker(){}

    public static void main(String[] args){
        try {
            String reg = "localhost";
            if (args.length > 0){
                reg = args[0];
            }
            //Get distributed object
            client = new MsgQClient();
            client.MsqQ_Init(reg);
            //Export so server can use callbacks
            DisSumWorker listen = new DisSumWorker();
            TopicListenerInterface listener = (TopicListenerInterface) UnicastRemoteObject.exportObject(listen, 0);
            //Sub to topics
            if(client.MsgQ_Subscribe("Log", listener)==EMomError.NoExisteixTopicQ) throw new RuntimeException("No existeix una cua");
            if(client.MsgQ_Subscribe("Work", listener)==EMomError.NoExisteixTopicQ) throw new RuntimeException("No existeix una cua");
            while (!done) Thread.onSpinWait();
            exit(0);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onTopicMessage(String message,String topic) throws RemoteException {
        StringTokenizer stok = new StringTokenizer(message, "-");
        try {
            //Get interval and do calculations
            long first = Long.parseLong((String) stok.nextElement());
            long last = Long.parseLong((String) stok.nextElement());
            long res = calcularSumaPrimos(first, last);
            //Send result
            if (client.MsgQ_SendMessage("Results", String.valueOf(res), 2) == EMomError.NoExisteixMsgQ)
                throw new RuntimeException("No existeix la cua");
            this.tareas_calculadas++;
        } catch (NumberFormatException e) {
            //For when we recieve messages from other queues that are not intervals
            System.out.println(message);
        }
    }

    @Override
    public void onTopicClosed(String topic) {
        if(topic.equals("Work")){
            System.out.println("Se ha terminado la ejecucion del programa. Se han calculado "+tareas_calculadas+" tareas.");
            client.MsgQ_Disconnect();
            done=true;
            System.out.println("Done");
        }
    }

    /* FUNCTION GIVE TO DO THE NEEDED CALCULATIONS */
    public static long calcularSumaPrimos(long begin,long end) {
        long sumaPrimos = 0;
        long numero;
        for (numero=begin; numero<end; numero++)
        {
            if (numero % 2 != 0) {
                if (esPrimo(numero)) {
                    sumaPrimos += numero;
                }
            }
        }
        return sumaPrimos;
    }

    /* FUNCTION GIVE TO CHECK IF A NUMBER IS PRIME */
    public static boolean esPrimo(long numero) {
        for (long i = 3; i * i <= numero; i += 2) {
            if (numero % i == 0) {
                return false;
            }
        }
        return true;
    }
}
