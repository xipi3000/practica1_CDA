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
    }
    public void subscribe(TopicListenerInterface listener){
        listenerQueue.add(listener);
    }


}
