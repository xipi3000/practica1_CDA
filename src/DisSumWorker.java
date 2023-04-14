import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.StringTokenizer;

public class DisSumWorker implements TopicListenerInterface{
    long tareas_calculadas;
    MsgQClient client;
    public DisSumWorker(){}
    //FALTARÀ EL TEMA DE XML/JSON
    public void main(String[] args) throws RemoteException {
        //get queue from client implementation
        String reg = "localhost";
        if(args.length > 0){
            reg = args[0];
        }
        client = new MsgQClient();
        client.MsqQ_Init(reg);
        //sub to Log
        DisSumWorker listen = new DisSumWorker();
        TopicListenerInterface listener = (TopicListenerInterface) UnicastRemoteObject.exportObject(listen, 0);
        client.MsgQ_Subscribe("Log", listener);
        //sub to Work
        DisSumWorker listen2 = new DisSumWorker();
        client.MsgQ_Subscribe("Work", listen2); //ENLLOC DE LISTENER, LA FUNCIÓ DE CALCUL DE PRIMERS
    }

    //S'haurà d'assegurar que es treballa en RoundRobin per a que això funcioni
    @Override
    public void onTopicMessage(String message) throws RemoteException {
        StringTokenizer stok = new StringTokenizer(message, "-");
        long first = (long) stok.nextElement();
        long last = (long) stok.nextElement();
        long res = calcularSumaPrimos(first, last);
        client.MsgQ_SendMessage("Results", String.valueOf(res), 2);
        this.tareas_calculadas++;
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

    public static boolean esPrimo(long numero) {
        for (long i = 3; i * i <= numero; i += 2) {
            if (numero % i == 0) {
                return false;
            }
        }
        return true;
    }
}
