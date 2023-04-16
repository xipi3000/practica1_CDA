import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;

public class MsgQClient implements TopicListenerInterface {
    private static MsgQ msgQ;
    private static TopicListenerInterface msgQlistener;
    public MsgQClient() throws RemoteException{

    }
    public void MsqQ_Init(String ServerAddress) {

        // Registration format //registry_hostname (optional):port /service
        String registration = "rmi://" + ServerAddress + "/MOMYservice";

        // Lookup the service in the registry, and obtain a remote service
        Remote remoteService = null;
        try {
            remoteService = Naming.lookup ( registration );
            msgQ = (MsgQ) remoteService;
            MsgQClient clientMonitor = new MsgQClient();

            // Exportar el objeto de la clase de la implementación al stub del interfase.
            msgQlistener = (TopicListenerInterface) UnicastRemoteObject.exportObject(clientMonitor, 0);
            System.out.println("S'ha connectat");


        } catch (NotBoundException e) {
            System.out.println ("Bound Error - " + e);
        } catch (MalformedURLException e) {
            System.out.println ("Url Error - " + e);
        } catch (RemoteException e) {
            System.out.println ("RMI Error - " + e);
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
    public String MsgQ_ReceiveMessage(String msgqname, int type) throws RemoteException {
        return msgQ.MsgQ_ReceiveMessage(msgqname,type);
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
        MsgQClient client = new MsgQClient();
        if(Objects.equals(args[0], "1")){
            client.MsqQ_Init("localhost");
            client.MsgQ_CreateTopic("nig",EPublishMode.RoundRobin);
            client.MsgQ_Subscribe("nig", msgQlistener);
            while (true);
        }
        else if (Objects.equals(args[0], "2")) {
            client.MsqQ_Init("localhost");
            client.MsgQ_Publish("nig","nor",0);
            System.out.println("error5");
        }
    }
}
