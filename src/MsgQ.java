import java.rmi.RemoteException;

public interface MsgQ extends java.rmi.Remote
{
    public EMomError MsgQ_CreateQueue(String msgqname) throws RemoteException;
    EMomError MsgQ_CloseQueue(String msgqname)throws RemoteException;
    EMomError MsgQ_SendMessage(String msgqname, String message, int type) throws RemoteException;
    String MsgQ_ReceiveMessage(String msgqname, int type) throws RemoteException;
    EMomError MsgQ_CreateTopic(String topicname, EPublishMode mode) throws RemoteException;
    EMomError MsgQ_CloseTopic(String topicname) throws RemoteException;
    EMomError MsgQ_Publish(String topic, String message, int type) throws RemoteException;
    EMomError MsgQ_Subscribe(String topic, TopicListenerInterface listener) throws RemoteException;
    void MsgQ_Disconnect() throws RemoteException;
    void MsgQ_Init()throws RemoteException;
}
