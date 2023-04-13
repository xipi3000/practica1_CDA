import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;

import static java.lang.Thread.sleep;

public class MsgQClient implements TopicListenerInterface {
    private static MsgQ msgQ;
    private static TopicListenerInterface msgQlistener;
    public MsgQClient() throws RemoteException{

    }
    public static void MsqQ_Init(String ServerAddress) {

        // Registration format //registry_hostname (optional):port /service
        String registration = "rmi://" + ServerAddress + "/MOMyservice";

        // Lookup the service in the registry, and obtain a remote service
        Remote remoteService = null;
        try {
            remoteService = Naming.lookup ( registration );
            msgQ = (MsgQ) remoteService;
            MsgQClient clientMonitor = new MsgQClient();

            // Exportar el objeto de la clase de la implementación al stub del interfase.
            msgQlistener = (TopicListenerInterface) UnicastRemoteObject.exportObject(clientMonitor, 0);



        } catch (NotBoundException e) {
            System.out.println ("Bound Error - " + e);
        } catch (MalformedURLException e) {
            System.out.println ("Url Error - " + e);
        } catch (RemoteException e) {
            System.out.println ("RMI Error - " + e);
        }
    }
    public static EMomError MsgQ_CreateQueue(String msgqname) throws RemoteException {
        return  msgQ.MsgQ_CreateQueue(msgqname);
    }
    public EMomError MsgQ_CloseQueue(String msgqname) throws RemoteException {
        return msgQ.MsgQ_CloseQueue(msgqname);
    }
    public static EMomError MsgQ_SendMessage(String msgqname, String message, int type) throws RemoteException {
        return msgQ.MsgQ_SendMessage(msgqname,message,type);

    }
    public static String MsgQ_ReceiveMessage(String msgqname, int type) throws RemoteException {
        return msgQ.MsgQ_ReceiveMessage(msgqname,type);
    }
    public static EMomError MsgQ_CreateTopic(String topicname, EPublishMode mode) throws RemoteException {
        return msgQ.MsgQ_CreateTopic(topicname,mode);
    }
    public EMomError MsgQ_CloseTopic(String topicname) throws RemoteException {
        return msgQ.MsgQ_CloseTopic(topicname);
    }
    public static EMomError MsgQ_Publish(String topic, String message, int type) throws RemoteException {
        return msgQ.MsgQ_Publish(topic,message,type);
    }
    public static EMomError MsgQ_Subscribe(String topic, TopicListenerInterface listener) throws RemoteException  {
        return msgQ.MsgQ_Subscribe(topic,listener);
    }

    @Override
    public void onTopicMessage(String message) throws RemoteException {
        System.out.println(message);
    }

    @Override
    public void onTopicClosed(String topic) throws RemoteException {
        System.out.println("S'ha tancat el topic: "+topic);

    }
    public static void main(String[] args) throws RemoteException{
        if(Objects.equals(args[0], "1")){
            MsqQ_Init("localhost");
            MsgQ_CreateTopic("nig",null);
            MsgQ_Subscribe("nig", msgQlistener);
            while (true);
        }
        else if (Objects.equals(args[0], "2")) {
            MsqQ_Init("localhost");
            MsgQ_Publish("nig","nor",0);
            System.out.println("error5");
        }
    }
    /*
        if(Objects.equals(args[0], "1")){
            MsqQ_Init("localhost");
            MsgQ_CreateTopic("nig",null);
            MsgQ_Subscribe("nig", msgQlistener);
            while (true);
        }
        else if (Objects.equals(args[0], "2")) {
            MsqQ_Init("localhost");
            MsgQ_Publish("nig","nor",0);
            System.out.println("error5");
        }
     */

}
