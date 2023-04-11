import java.util.Hashtable;
import java.util.Vector;

public class TopicQueues {
    Hashtable<String, Vector<Message>> topicQueues;
    EPublishMode publishMode;
    public TopicQueues(Hashtable<String, Vector<Message>> clientQueues,EPublishMode publishMode){
        this.topicQueues = clientQueues;
        this.publishMode = publishMode;
    }


}
