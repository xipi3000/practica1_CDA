public class MsgQServant implements MsgQ {
    private static final long serialVersionUID = 1;



    public void MsqQ_Init(String ServerAddress){

    }
    public EMomError MsgQ_CreateQueue(String msgqname) {

        return null;
    }
    public EMomError MsgQ_CloseQueue(String msgqname){

        return null;
    }
    public EMomError MsgQ_SendMessage(String msgqname, String message, int type){

        return null;
    }
    public String MsgQ_ReceiveMessage(String msgqname, int type){

        return null;
    }
    public  EMomError MsgQ_CreateTopic(String topicname, EPublishMode mode){

        return null;
    }
    public EMomError MsgQ_CloseTopic(String topicname){

        return null;
    }
    public  EMomError MsgQ_Publish(String topic, String message, int type){

        return null;
    }
    public  EMomError MsgQ_Subscribe(String topic, TopicListenerInterface listener){

        return null;
    }
}
