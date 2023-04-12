import java.rmi.RemoteException;

public interface TopicListenerInterface extends java.rmi.Remote{
    public void onTopicMessage(String message) throws RemoteException;
    public void onTopicClosed() throws RemoteException;
}
