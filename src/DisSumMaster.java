import java.rmi.RemoteException;

public class DisSumMaster {
    public static void main(String args[]) throws RemoteException {
        /*
        Será el encargado de distribuir el problema en N tareas (intervalos de números a sumar) y enviar las tareas
        en forma de mensajes a una cola de mensajes de tipo topic denominada “Work”.
        */
        long last = Integer.parseInt(args[0]);
        long jobs = Integer.parseInt(args[1]);
        String ip_server = "localhost";
        if (args.length > 2){
            ip_server = args[3];
        }
        long numeros_por_tarea = last / jobs; //N tareas
        MsgQ queue = new MsgQServant();
        queue.MsgQ_CreateTopic("Work", EPublishMode.TOPIC);
        /*
        El programa máster también creará una cola de mensajes punto-a-punto denominada “Results” mediante la cual
        recibirá todos los resultados parciales calculados por los trabajadores.
        */
        queue.MsgQ_CreateQueue("Results");

        /*
        A medida que el máster vaya recibiendo los resultados parciales de las tareas, los sumará
        entre sí para obtener el resultado final.
        Una el máster haya recibido tantos resultados parciales como tareas ha generado significará que el trabajo
        se ha completado, mostrando el resultado final por pantalla.
        */

        /*
        Antes de finalizar el máster cerrará las dos colas de mensajes (Work y Results).
        */

        /*
        El programa máster recibirá como parámetros el intervalo final del sumatorio (M), el
        número de tareas a generar (N) y la dirección donde se ejecuta el servidor de RMI (por
        defecto será localhost).
        Sintaxis: DisSumMaster <intervalo_final_sum> <#trabajos> [<ip_servidor>]
        */
    }
}
