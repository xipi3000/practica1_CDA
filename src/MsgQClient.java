import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Objects;

public class MsgQClient implements TopicListenerInterface {
    private static MsgQ msgQ;
    private static InitialContext ctx;
    private static TopicListenerInterface msgQlistener;
    public MsgQClient() throws RemoteException{

    }
    public void MsqQ_Init(String ServerAddress) {

        final Hashtable jndiProperties = new Hashtable();
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
        jndiProperties.put(Context.PROVIDER_URL,"rmi://localhost:6969");
        try{
            ctx = new InitialContext(jndiProperties);

            msgQ = (MsgQ) ctx.lookup("/jndi/MOMYservice");
            msgQ.MsgQ_Init();

            MsgQClient clientMonitor = new MsgQClient();

            // Exportar el objeto de la clase de la implementación al stub del interfase.
            msgQlistener = (TopicListenerInterface) UnicastRemoteObject.exportObject(clientMonitor, 0);
            System.out.println("S'ha connectat");




        } catch (RemoteException e) {
            System.out.println ("RMI Error - " + e);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
    public void MsgQ_Disconnect(){
        try {
            msgQ.MsgQ_Disconnect();
            ctx.close();

        } catch (NamingException e) {
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    public EMomError MsgQ_CreateQueue(String msgqname) throws RemoteException {
        return  msgQ.MsgQ_CreateQueue(msgqname);
    }
    public EMomError MsgQ_CloseQueue(String msgqname) throws RemoteException {
        return msgQ.MsgQ_CloseQueue(msgqname);
    }
    public EMomError MsgQ_SendMessage(String msgqname, String message, int type) throws RemoteException {
        return msgQ.MsgQ_SendMessage(msgqname,message,type);

    }
    public String MsgQ_ReceiveMessage(String msgqname, int type, Boolean bloqueante) throws RemoteException {
        return msgQ.MsgQ_ReceiveMessage(msgqname,type,bloqueante);
    }
    public EMomError MsgQ_CreateTopic(String topicname, EPublishMode mode) throws RemoteException {
        return msgQ.MsgQ_CreateTopic(topicname,mode);
    }
    public EMomError MsgQ_CloseTopic(String topicname) throws RemoteException {
        return msgQ.MsgQ_CloseTopic(topicname);
    }
    public EMomError MsgQ_Publish(String topic, String message, int type) throws RemoteException {
        return msgQ.MsgQ_Publish(topic,message,type);
    }
    public EMomError MsgQ_Subscribe(String topic, TopicListenerInterface listener) throws RemoteException  {
        return msgQ.MsgQ_Subscribe(topic,listener);
    }

    @Override
    public void onTopicMessage(String message,String topic) throws RemoteException {
        //System.out.println(message);
    }

    @Override
    public void onTopicClosed(String topic) throws RemoteException {
        System.out.println("S'ha tancat el topic: "+topic);

    }
    public static void main(String[] args) throws RemoteException{

    }
}
