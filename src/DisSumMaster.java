import java.rmi.RemoteException;
import java.util.StringTokenizer;

public class DisSumMaster {
    public static void main(String[] args) throws RemoteException {
        /*
        El programa máster recibirá como parámetros el intervalo final del sumatorio (M), el número de tareas a generar
        (N) y la dirección donde se ejecuta el servidor de RMI (por defecto será localhost).
        Sintaxis: DisSumMaster <intervalo_final_sum> <#trabajos> [<ip_servidor>]
        */
        //Set parameters given
        long last = Integer.parseInt(args[0]); //numero final a sumar
        long jobs = Integer.parseInt(args[1]); //Nº tareas
        String ip_server = "localhost";
        if (args.length > 2){
            ip_server = args[3]; //ip servidor [localhost si no existe]
        }
        //Create default queues
        MsgQ queue = new MsgQServant(); //creamos un objeto tipo cola de mensajes
        queue.MsgQ_CreateTopic("Work", EPublishMode.TOPIC); //usamos su método asociado para crear el Topic
        queue.MsgQ_CreateQueue("Results"); //usamos otro método para crear una cola tipo P2P
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
            } else { //center intervales - treated equally
                first_sum = last_sum + 1;
                last_sum = last_sum + numeros_por_tarea;
                message = first_sum + "-" + last_sum;
            }
            queue.MsgQ_Publish("Work", message, 1); //type = 1 -> intervals
        }
        //Wait for results (since it's not blocking, we need the counter and to check for not null)
        int jobs_done = 0;
        long res = 0;
        while (jobs_done < jobs){
            String msg = queue.MsgQ_ReceiveMessage("Results", 0); //Type s'haurà de mirar
            if (msg !=null){
                jobs_done++;
                long partialRes = processMessage(msg);
                res+=partialRes;
            }
        }
        //Close queues after finishing job
        queue.MsgQ_CloseTopic("Work");
        queue.MsgQ_CloseQueue("Results");
    }

    private static long processMessage(String msg) {
        StringTokenizer stok = new StringTokenizer(msg, "-");
        long first = (long) stok.nextElement();
        long last = (long) stok.nextElement();
        long res = 0;
        for (long i=first; i<last; i++){
            res+=i;
        }
        return res;
    }
}
