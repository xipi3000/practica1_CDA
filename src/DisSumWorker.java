/* ---------------------------------------------------------------
Práctica 1.
Código fuente: MsgRMI.java
Grau Informàtica
XXXXXXXXX Pol Escolà
49263877Q Antonio López Gómez
--------------------------------------------------------------- */
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.StringTokenizer;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class DisSumWorker implements TopicListenerInterface, Runnable{
    long tareas_calculadas;
    boolean work_exists = false;
    static MsgQClient client;
    static String reg;
    CyclicBarrier barrier;
    public DisSumWorker(String serv, CyclicBarrier barr){
        reg = serv;
        barrier = barr;
    }
    @Override
    public void onTopicMessage(String message) throws RemoteException {
        StringTokenizer stok = new StringTokenizer(message, "-");
        long first = Long.parseLong((String) stok.nextElement());
        long last = Long.parseLong((String) stok.nextElement());
        long res = calcularSumaPrimos(first, last);
        if(client.MsgQ_SendMessage("Results", String.valueOf(res), 2)== EMomError.NoExisteixMsgQ) throw new RuntimeException("No existeix la cua");
        this.tareas_calculadas++;
    }

    @Override
    public void onTopicClosed(String topic) {
        System.out.println("Se ha terminado la ejecucion del programa. Se han calculado "+tareas_calculadas+" tareas.");
        work_exists=false;
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
            client = new MsgQClient();
            client.MsqQ_Init(reg);
            DisSumWorker listen = new DisSumWorker(reg, new CyclicBarrier(1));
            TopicListenerInterface listener = (TopicListenerInterface) UnicastRemoteObject.exportObject(listen, 0);
            if(client.MsgQ_Subscribe("Log", listener)==EMomError.NoExisteixTopicQ) throw new RuntimeException("No existeix una cua");
            if(client.MsgQ_Subscribe("Work", listener)==EMomError.NoExisteixTopicQ) throw new RuntimeException("No existeix una cua");
            work_exists = true;
            //Sync with master now that all have subscribed to Work queue
            barrier.await();
            while(work_exists);
            Thread.currentThread().join();
        } catch (RemoteException | BrokenBarrierException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
