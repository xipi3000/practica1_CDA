/* ---------------------------------------------------------------
Práctica 1.
Código fuente: MsgRMI.java
Grau Informàtica
48053151X Pol Escolà
49263877Q Antonio López Gómez
--------------------------------------------------------------- */
import java.rmi.RemoteException;

public interface TopicListenerInterface extends java.rmi.Remote{
    void onTopicMessage(String message,String topic) throws RemoteException;
    void onTopicClosed(String topic) throws RemoteException;
}
