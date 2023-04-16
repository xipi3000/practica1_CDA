import java.lang.reflect.Array;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

public class MsgQServant implements MsgQ, Runnable {
    private static final long serialVersionUID = 1;

    private Hashtable<String,Vector<Message>> clientQueues = new Hashtable<>();
    private Hashtable<String, TopicQueue> topicQueues = new Hashtable<>();

    public MsgQServant() throws RemoteException{

    }

    private boolean existeixMsgQ(String msgqname){
        if(clientQueues.get(msgqname)!=null){
            return true;
        }
        return false;
    }
    private boolean existeixTopicQ(String topicname){
        if(topicQueues.get(topicname)!=null){
            return true;
        }
        return false;
    }
    public EMomError MsgQ_CreateQueue(String msgqname) throws RemoteException {
        return createQueue(msgqname);
    }

    private EMomError createQueue(String msgqname) {
        if(!existeixMsgQ(msgqname)) {
            clientQueues.put(msgqname, new Stack<>());
            return EMomError.NoError;
        }
        return EMomError.JaExisteixMsgQ;
    }

    public EMomError MsgQ_CloseQueue(String msgqname)throws RemoteException{
        return closeQueue(msgqname);
    }

    private EMomError closeQueue(String msgqname) {
        if(clientQueues.get(msgqname)!=null){
            clientQueues.remove(msgqname);
            return EMomError.NoError;
        }
        return EMomError.NoExisteixMsgQ;
    }

    public EMomError MsgQ_SendMessage(String msgqname, String message, int type) throws RemoteException{
        return sendMessage(msgqname,message,type);
    }

    private EMomError sendMessage(String msgqname, String message, int type) {
        if(existeixMsgQ(msgqname)){
            clientQueues.get(msgqname).add(new Message(message,type));
            return EMomError.NoError;
        }
        return EMomError.NoExisteixMsgQ;

    }

    public String MsgQ_ReceiveMessage(String msgqname,int type) throws RemoteException{
        return  receiveMessage(msgqname,type);
    }

    public String receiveMessage(String msgqname,int type) throws RemoteException{
        if(existeixMsgQ(msgqname)) {
            int it = FIFOSeach(clientQueues.get(msgqname), type);
            if (it != -1) {
                Message msg = clientQueues.get(msgqname).remove(it);
                return msg.message;
            }
            return "Error, no queden missatges!";
        }
        return "Error, no existeix la cua!";
    }
    private int FIFOSeach(Vector<Message> messages,int type){

        for(int i = 0;i<messages.size();i++){
            if(messages.get(i).type == type){
                return i;
            }
        }
        return -1;
    }

    public  EMomError MsgQ_CreateTopic(String topicname, EPublishMode mode) throws RemoteException{
        createTopic(topicname, mode);
        return null;
    }

    private EMomError createTopic(String topicname, EPublishMode mode){
        if(!existeixTopicQ(topicname)){
            topicQueues.put(topicname,new TopicQueue(mode));
            addToLog("Created new topic queue "+topicname+", with mode " + mode);
            return EMomError.NoError;
        }
        addToLog("Couldn't create new topic queue "+topicname);
        return EMomError.JaExisteixTopicQ;
    }

    public EMomError MsgQ_CloseTopic(String topicname) throws RemoteException{
        return closeTopic(topicname);
    }

    private EMomError closeTopic(String topicname) {
        if(existeixTopicQ(topicname)){
            topicQueues.get(topicname).remove(topicname);
            topicQueues.remove(topicname);
            addToLog("Topic "+topicname+" was closed");
            return EMomError.NoError;
        }
        addToLog("Couldn't close topic queue "+topicname);
        return EMomError.NoExisteixTopicQ;
    }

    public  EMomError MsgQ_Publish(String topic, String message, int type) throws RemoteException{
        return publish(topic,message,type);
    }
    public  EMomError publish(String topic, String message, int type){
        if(existeixTopicQ(topic)){
            topicQueues.get(topic).addMsg(new Message(message,type));
            addToLog("Client published message: "+message+", at topic: "+topic);
            return EMomError.NoError;
        }
        addToLog("Client couldn't publish at topic: "+topic);
        return EMomError.NoExisteixTopicQ;
    }

    public  EMomError MsgQ_Subscribe(String topic, TopicListenerInterface listener) throws RemoteException{
        return subscribe(topic,listener);
    }

    public  EMomError subscribe(String topic, TopicListenerInterface listener){
        if(existeixTopicQ(topic)) {
            topicQueues.get(topic).subscribe(listener);
            addToLog("Client subscribed at topic: "+topic);
            return EMomError.NoError;
        }
        addToLog("Client couldn't subscribed at topic: "+topic);
        return  EMomError.NoExisteixTopicQ;
    }

    private void addToLog(String logMsg){
        publish("Log",System.currentTimeMillis()/1000+logMsg,0);
    }

    @Override
    public void run() {
        createTopic("Log",EPublishMode.Broadcast);

    }
}

