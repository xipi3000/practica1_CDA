import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.StringTokenizer;

public class DisSumWorker implements TopicListenerInterface{
    public DisSumWorker(){}
    public static void main(String[] args) throws MalformedURLException, NotBoundException, RemoteException {
        /*
        El programa trabajador recibirá un único parámetro, la dirección del servidor de RMI (por defecto será localhost).
        Sintaxis: DisSumWorker [<ip_servidor>]
        */
        MsgQ queue;
        String reg = "localhost";
        if(args.length > 0){
            reg = args[0];
        }
        String registration = "rmi://" + reg + "/prac1";
        Remote remoteService = Naming.lookup(registration);
        queue = (MsgQ) remoteService;
        /*
        El programa trabajador al principio de la ejecución se tendrá que subscribir al tema de “Log” y mostrar los
        mensajes recibidos por pantalla.
        */
        DisSumWorker listen = new DisSumWorker();
        TopicListenerInterface listener = (TopicListenerInterface) UnicastRemoteObject.exportObject(listen, 0);
        queue.MsgQ_Subscribe("Log", listener);
        /*
        Sera el programa encargado de realizar las tareas de sumatorio de un intervalo de números.
        El trabajador se suscribirá a la cola de mensajes “Work” y a partir de ese momento, cada vez que el listener
        reciba un mensaje, calculará el sumatorio del intervalo de números asociado con la tarea y enviará el resultado
        parcial al máster mediante el envío de un mensaje a la cola “Results”.
        */
        String msg = queue.MsgQ_ReceiveMessage("Work", 1);
        StringTokenizer stok = new StringTokenizer(msg, "-");
        long first = (long) stok.nextElement();
        long last = (long) stok.nextElement();
        long res = 0;
        for (long i=first; i<last; i++){
            res+=i;
        }
        queue.MsgQ_SendMessage("Results", String.valueOf(res), 2);
        /*
        Una vez el trabajador sea notificado que la cola “Work” ha sido cerrada, el programa finalizará mostrando por
        pantalla el número de tareas que ha procesado.
        */
    }

    @Override
    public void onTopicMessage(String message) throws RemoteException {
        System.out.println(message);
    }

    @Override
    public void onTopicClosed(String topic) throws RemoteException {

    }
}
