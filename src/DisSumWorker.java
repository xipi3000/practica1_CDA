import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.StringTokenizer;

public class DisSumWorker implements TopicListenerInterface{
    long tareas_calculadas;
    public DisSumWorker(){}
    public static void main(String[] args) throws RemoteException {
        //get queue from client implementation
        String reg = "localhost";
        if(args.length > 0){
            reg = args[0];
        }
        MsgQClient client = new MsgQClient();
        client.MsqQ_Init(reg);
        //sub to Log
        DisSumWorker listen = new DisSumWorker();
        TopicListenerInterface listener = (TopicListenerInterface) UnicastRemoteObject.exportObject(listen, 0);
        client.MsgQ_Subscribe("Log", listener);
        //retrieve interval, calculate and send to "Results" queue
        String msg = client.MsgQ_ReceiveMessage("Work", 1);
        StringTokenizer stok = new StringTokenizer(msg, "-");
        long first = (long) stok.nextElement();
        long last = (long) stok.nextElement();
        long res = 0;
        for (long i=first; i<last; i++){
            res+=i;
        }
        client.MsgQ_SendMessage("Results", String.valueOf(res), 2);
        listen.tareas_calculadas++;
    }

    @Override
    public void onTopicMessage(String message) throws RemoteException {
        System.out.println(message);
    }

    @Override
    public void onTopicClosed(String topic) throws RemoteException {
        /*
        Una vez el trabajador sea notificado que la cola “Work” ha sido cerrada, el programa finalizará mostrando por
        pantalla el número de tareas que ha procesado.
        */
        System.out.println("Se ha terminado la ejecución del programa.");
        System.out.println("Tareas calculadas: "+tareas_calculadas);
    }
}
