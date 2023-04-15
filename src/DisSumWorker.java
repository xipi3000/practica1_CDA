import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.StringTokenizer;

public class DisSumWorker implements TopicListenerInterface, Runnable{
    long tareas_calculadas;
    static MsgQClient client;
    static String reg;
    public DisSumWorker(String serv){
        reg = serv;
    }
    //FALTARÀ EL TEMA DE XML/JSON
    //S'haurà d'assegurar que es treballa en RoundRobin per a que això funcioni
    @Override
    public void onTopicMessage(String message) throws RemoteException {
        System.out.println("S'ESTÀ EXECUTANT LA DE WORKER");
        System.out.println("Listener recieved: " + message);
        StringTokenizer stok = new StringTokenizer(message, "-");
        long first = Long.parseLong((String) stok.nextElement());
        long last = Long.parseLong((String) stok.nextElement());
        long res = calcularSumaPrimos(first, last);
        System.out.println("Res listener: "+res);
        if(client.MsgQ_SendMessage("Results", String.valueOf(res), 2)== EMomError.NoExisteixMsgQ) throw new RuntimeException("No existeix la cua");
        this.tareas_calculadas++;
    }

    @Override
    public void onTopicClosed(String topic) throws RemoteException {
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

    @Override
    public void run() {
        try {
            System.out.println("Listener creat");
            client = new MsgQClient();
            client.MsqQ_Init(reg);
            //sub to Log
            DisSumWorker listen = new DisSumWorker(reg);
            TopicListenerInterface listener = (TopicListenerInterface) UnicastRemoteObject.exportObject(listen, 0);
            if(client.MsgQ_Subscribe("Log", listener)==EMomError.NoExisteixTopicQ) throw new RuntimeException("No existeix una cua");
            if(client.MsgQ_Subscribe("Work", listener)==EMomError.NoExisteixTopicQ) throw new RuntimeException("No existeix una cua");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
