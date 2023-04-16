import java.rmi.RemoteException;

public interface TopicListenerInterface extends java.rmi.Remote{
    public void onTopicMessage(String message,String topic) throws RemoteException;
    public void onTopicClosed(String topic) throws RemoteException;
}
