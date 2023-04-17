/* ---------------------------------------------------------------
Práctica 1.
Código fuente: MsgRMI.java
Grau Informàtica
48053151X Pol Escolà
49263877Q Antonio López Gómez
--------------------------------------------------------------- */
import java.io.IOException;
import java.rmi.RMISecurityManager;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import static java.lang.System.exit;

public class DisSumMaster {
    public static void main(String[] args) throws IOException, InterruptedException, BrokenBarrierException {
        System.setProperty("java.security.policy","client.policy");
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new RMISecurityManager());
        //Set parameters given
        if (args.length < 2){
            System.out.println("No s'han proveït suficients paràmetres: <Num a calcular> <Num threads> [<ip servidor>]");
            exit(-1);
        }
        long last = Integer.parseInt(args[0]); //numero final a sumar
        long jobs = Integer.parseInt(args[1]); //Nº tareas
        String reg = "localhost";
        if(args.length > 2){
            reg = args[2];
        }
        //Get distributed object
        MsgQClient client = new MsgQClient();
        client.MsqQ_Init(reg);
        //Create needed queues
        client.MsgQ_CreateTopic("Work", EPublishMode.RoundRobin); //usamos su método asociado para crear el Topic
        client.MsgQ_CreateQueue("Results"); //usamos otro método para crear una cola tipo P2P
        //Waiting in case all workers haven't subscribed yet
        Scanner in = new Scanner(System.in);
        in.nextLine();
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
        //Wait for results (since it can be not blocking, we need the counter and to check for not null)
        int jobs_done = 0;
        long res = 0;
        while (jobs_done < jobs){
            String msg = client.MsgQ_ReceiveMessage("Results", 2,false);
            if (msg != null){
                jobs_done++;
                long partialRes = processMessage(msg);
                res+=partialRes;
            }
        }
        System.out.println("S'ha acabat de sumar, resultat final: "+res);
        //Close queues after finishing job, end program
        client.MsgQ_CloseTopic("Work");
        client.MsgQ_CloseQueue("Results");
        client.MsgQ_Disconnect();
        exit(0);
    }

    private static long processMessage(String msg) {
        return Long.parseLong(msg);
    }
}
