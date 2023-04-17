/* ---------------------------------------------------------------
Práctica 1.
Código fuente: MsgRMI.java
Grau Informàtica
XXXXXXXXX Pol Escolà
49263877Q Antonio López Gómez
--------------------------------------------------------------- */
import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import static java.lang.System.exit;

public class DisSumMaster {
    public static void main(String[] args) throws IOException, InterruptedException, BrokenBarrierException {
        //Set parameters given
        if (args.length < 2){
            System.out.println("No s'han proveït suficients paràmetres");
            exit(-1);
        }
        long last = Integer.parseInt(args[0]); //numero final a sumar
        long jobs = Integer.parseInt(args[1]); //Nº tareas
        //Barrier to make sure workers have been created before starting to publish messages
        CyclicBarrier barrier = new CyclicBarrier((int)jobs+1);
        //Get default queues
        String reg = "localhost";
        if(args.length > 2){
            reg = args[2];
        }
        MsgQClient client = new MsgQClient();
        client.MsqQ_Init(reg);
        client.MsgQ_CreateTopic("Work", EPublishMode.RoundRobin); //usamos su método asociado para crear el Topic
        //client.MsgQ_CreateTopic("Log", EPublishMode.Broadcast); //usamos su método asociado para crear el Topic
        client.MsgQ_CreateQueue("Results"); //usamos otro método para crear una cola tipo P2P
        for (int i=0; i<jobs; i++){
            DisSumWorker w = new DisSumWorker(reg, barrier);
            Thread thread = new Thread(w);
            thread.start();
        }
        //Waiting in case all workers haven't subscribed yet
        barrier.await();
        //Distribute jobs
        int numeros_por_tarea = (int)(last / jobs); //proporción numeros a sumar por tarea
        String message;
        long last_sum = 0, first_sum;
        for(int i=0; i<jobs; i++){
            if (i == 0){  //first interval - treated differently
                last_sum = numeros_por_tarea;
                message = "1-" + last_sum;
            }else if (i == jobs-1){ //last interval - treated differently
                first_sum = last_sum;
                message = first_sum + "-" + last;
            } else { //center intervals - treated equally
                first_sum = last_sum;
                last_sum = last_sum + numeros_por_tarea;
                message = first_sum + "-" + last_sum;
            }
            client.MsgQ_Publish("Work", message, 1); //type = 1 -> intervals
        }
        //Wait for results (since it's not blocking, we need the counter and to check for not null)
        int jobs_done = 0;
        long res = 0;
        while (jobs_done < jobs){
            String msg = client.MsgQ_ReceiveMessage("Results", 2);
            if (msg != null){
                jobs_done++;
                long partialRes = processMessage(msg);
                res+=partialRes;
            }
        }
        System.out.println("S'ha acabat de sumar, resultat final: "+res);
        //Close queues after finishing job
        client.MsgQ_CloseTopic("Work");
        client.MsgQ_CloseQueue("Results");

        client.MsgQ_Disconnect();
        exit(0);
    }

    private static long processMessage(String msg) {
        return Long.parseLong(msg);
    }
}
