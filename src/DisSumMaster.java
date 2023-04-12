
import java.rmi.RemoteException;

public class DisSumMaster {
    /*
    El programa máster recibirá como parámetros el intervalo final del sumatorio (M), el número de tareas a generar
    (N) y la dirección donde se ejecuta el servidor de RMI (por defecto será localhost).
    Sintaxis: DisSumMaster <intervalo_final_sum> <#trabajos> [<ip_servidor>]
    */
    public static void main(String[] args) throws RemoteException{
        //Set parameters given
        long last = Integer.parseInt(args[0]); //numero final a sumar
        long jobs = Integer.parseInt(args[1]); //Nº tareas
        //Get default queues
        String reg = "localhost";
        if(args.length > 2){
            reg = args[0];
        }
        MsgQClient client = new MsgQClient();
        client.MsqQ_Init(reg);
        client.MsgQ_CreateTopic("Work", EPublishMode.TOPIC); //usamos su método asociado para crear el Topic
        client.MsgQ_CreateQueue("Results"); //usamos otro método para crear una cola tipo P2P
        //Distribute jobs
        long numeros_por_tarea = last / jobs; //proporción numeros a sumar por tarea
        String message;
        long last_sum = 0, first_sum;
        for(int i=0; i<jobs; i++){
            if (i == 0){  //first interval - treated differently
                last_sum = numeros_por_tarea;
                message = "1-" + numeros_por_tarea;
            }else if (i == jobs-1){ //last interval - treated differently
                first_sum = last_sum+1;
                message = first_sum + "-" + last;
            } else { //center intervals - treated equally
                first_sum = last_sum + 1;
                last_sum = last_sum + numeros_por_tarea;
                message = first_sum + "-" + last_sum;
            }
            client.MsgQ_Publish("Work", message, 1); //type = 1 -> intervals
        }
        //Wait for results (since it's not blocking, we need the counter and to check for not null)
        int jobs_done = 0;
        long res = 0;
        while (jobs_done < jobs){
            String msg = client.MsgQ_ReceiveMessage("Results", 0); //Type s'haurà de mirar
            if (msg !=null){
                jobs_done++;
                long partialRes = processMessage(msg);
                res+=partialRes;
            }
        }
        System.out.println("S'ha acabat de sumar, resultat final: "+res);
        //Close queues after finishing job
        client.MsgQ_CloseTopic("Work");
        client.MsgQ_CloseQueue("Results");
    }

    private static long processMessage(String msg) {
        return Long.parseLong(msg);
    }
}
