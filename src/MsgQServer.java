/* ---------------------------------------------------------------
Práctica 1.
Código fuente: MsgRMI.java
Grau Informàtica
48053151X Pol Escolà
49263877Q Antonio López Gómez
--------------------------------------------------------------- */
import javax.naming.Context;
import javax.naming.InitialContext;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.util.Hashtable;
import java.util.Scanner;

import static java.lang.System.exit;

public class MsgQServer {
    public MsgQServer() {}
    private static InitialContext ctx;
    public static void main(String[] args)
    {
        System.out.println("Cargando Servicio RMI");
        try
        {
            System.setProperty("java.security.policy","server.policy");
            if (System.getSecurityManager() == null)
                System.setSecurityManager(new RMISecurityManager());
            String reg = "localhost";
            if (args.length > 0){
                reg = args[0];
            }
            // Cargar el servicio.
            MsgQServant serveiMsgQ = new MsgQServant();

            // Exportar el objeto de la clase de la implementación al stub del interfase.
            MsgQ msgQ = (MsgQ) UnicastRemoteObject.exportObject(serveiMsgQ, 0);

            // Enlazar el objeto remoto (stub) con el registro de RMI.
            Registry registry = LocateRegistry.createRegistry(6969);
            final Hashtable jndiProperties = new Hashtable();
            jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
            jndiProperties.put(Context.PROVIDER_URL, "rmi://"+reg+":6969");
            ctx = new InitialContext(jndiProperties);
            ctx.bind("/jndi/MOMYservice", msgQ);
            System.err.println("Server ready");

            Thread thread = new Thread (serveiMsgQ);
            thread.start();

            Scanner input= new Scanner(System.in);
            while(!input.nextLine().equals("exit"));
            ctx.unbind("/jndi/MOMYservice");
            ctx.close();
            exit(0);
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

