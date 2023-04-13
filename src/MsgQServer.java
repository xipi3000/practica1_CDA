import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class MsgQServer {

    public MsgQServer() throws RemoteException {};

    public static void main(String args[])
    {
        System.out.println("Cargando Servicio RMI");

        try
        {
            // Cargar el servicio.
            MsgQServant serveiMsgQ = new MsgQServant();

            // Exportar el objeto de la clase de la implementación al stub del interfase.
            MsgQ msgQ = (MsgQ) UnicastRemoteObject.exportObject(serveiMsgQ, 0);

            // Enlazar el objeto remoto (stub) con el registro de RMI.
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("MOMyservice", msgQ);
            System.err.println("Server ready");

            // Create a thread, and pass the sensor server.
            // This will activate the run() method, and trigger
            // regular temperature changes.
            Thread thread = new Thread (serveiMsgQ);
            thread.start();
        }
        catch (RemoteException re)
        {
            System.err.println("Remote Error - " + re);
        }
        catch (Exception e)
        {
            System.err.println("Error - " + e);
        }
    }
}

