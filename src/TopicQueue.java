import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

public class TopicQueue {
    Vector<Message> topicQueue;
    EPublishMode publishMode;
    Vector<TopicListenerInterface> listenerQueue;
    public TopicQueue(EPublishMode publishMode){
        this.topicQueue = new Vector<>();
        this.publishMode = publishMode;
        this.listenerQueue = new Vector<>();
    }
    public void addMsg(Message message){
        topicQueue.add(message);
        for(Enumeration e = listenerQueue.elements(); e.hasMoreElements();){
            TopicListenerInterface listener = (TopicListenerInterface) e.nextElement();
            try {
                listener.onTopicMessage(message.message);
            }
            catch (RemoteException re)
                {
                    System.out.println (" Listener not accessible, removing listener -" + listener);
                    // Remote the listener
                    listenerQueue.remove( listener );
                }
        }
    }
    public void subscribe(TopicListenerInterface listener){
        listenerQueue.add(listener);
    }
    public void remove(String topic){
        for(Enumeration e = listenerQueue.elements(); e.hasMoreElements();){
            TopicListenerInterface listener = (TopicListenerInterface) e.nextElement();
            try {
                listener.onTopicClosed(topic);
                listenerQueue.remove( listener );
            }
            catch (RemoteException re)
            {
                System.out.println (" Listener not accessible, removing listener -" + listener);
                // Remote the listener
                listenerQueue.remove( listener );
            }
        }
    }


}
