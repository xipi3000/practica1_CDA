import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class MsgQClient implements TopicListenerInterface {
    private MsgQ msgQ;
    public void MsqQ_Init(String ServerAddress) {

        // Registration format //registry_hostname (optional):port /service
        String registration = "rmi://" + ServerAddress + "/BombillaRMICallbacks";

        // Lookup the service in the registry, and obtain a remote service
        Remote remoteService = null;
        try {
            remoteService = Naming.lookup ( registration );
            msgQ = (MsgQ) remoteService;



        } catch (NotBoundException e) {
            System.out.println ("Bound Error - " + e);
        } catch (MalformedURLException e) {
            System.out.println ("Url Error - " + e);
        } catch (RemoteException e) {
            System.out.println ("RMI Error - " + e);
        }
    }
    public EMomError MsgQ_CreateQueue(String msgqname) throws RemoteException {
        msgQ.MsgQ_CreateQueue(msgqname);
        return null;
    }
    public EMomError MsgQ_CloseQueue(String msgqname) throws RemoteException {
        msgQ.MsgQ_CloseQueue(msgqname);
        return null;
    }
    public EMomError MsgQ_SendMessage(String msgqname, String message, int type) throws RemoteException {
        msgQ.MsgQ_SendMessage(msgqname,message,type);
        return null;
    }
    public String MsgQ_ReceiveMessage(String msgqname, int type) throws RemoteException {
        return msgQ.MsgQ_ReceiveMessage(msgqname,type);
    }
    public  EMomError MsgQ_CreateTopic(String topicname, EPublishMode mode) throws RemoteException {

        return null;
    }
    public EMomError MsgQ_CloseTopic(String topicname) throws RemoteException {

        return null;
    }
    public  EMomError MsgQ_Publish(String topic, String message, int type) throws RemoteException {

        return null;
    }
    public  EMomError MsgQ_Subscribe(String topic, TopicListenerInterface listener) throws RemoteException  {

        return null;
    }

    @Override
    public void onTopicMessage(String message) throws RemoteException {
        System.out.println(message);
    }

    @Override
    public void onTopicClosed(String topic) throws RemoteException {
        System.out.println("S'ha tancat el topic: "+topic);

    }


}
