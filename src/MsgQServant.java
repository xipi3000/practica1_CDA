import java.lang.reflect.Array;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

public class MsgQServant implements MsgQ, Runnable {
    private static final long serialVersionUID = 1;

    private Hashtable<String,Vector<Message>> clientQueues;
    private Hashtable<String, TopicQueue> topicQueues;

    public MsgQServant() throws RemoteException{

    }
    public EMomError MsgQ_CreateQueue(String msgqname) throws RemoteException {
        CreateQueue(msgqname);
        return null;
    }

    private void CreateQueue(String msgqname) {
        clientQueues.put(msgqname, new Stack<Message>());
    }

    public EMomError MsgQ_CloseQueue(String msgqname)throws RemoteException{
        CloseQueue(msgqname);
        return null;
    }

    private void CloseQueue(String msgqname) {
        clientQueues.remove(msgqname);
    }

    public EMomError MsgQ_SendMessage(String msgqname, String message, int type) throws RemoteException{
        sendMessage(msgqname,message,type);
        return null;
    }

    private void sendMessage(String msgqname, String message, int type) {
        clientQueues.get(msgqname).add(new Message(message,type));
    }

    private int FIFOSeach(Vector<Message> messages,int type){

        for(int i = 0;i<messages.size();i++){
            if(messages.get(i).type == type){
                return i;
            }

        }
        return -1;

    }

    public String MsgQ_ReceiveMessage(String msgqname,int type) throws RemoteException{
        int it = FIFOSeach(clientQueues.get(msgqname),type);
        if (it != -1){
            Message msg = clientQueues.get(msgqname).remove(it);
            return msg.message;
        }
        return null;


    }
    private void createTopic(String topicname, EPublishMode mode){
        topicQueues.put(topicname,new TopicQueue(mode));
    }
    public  EMomError MsgQ_CreateTopic(String topicname, EPublishMode mode) throws RemoteException{
        createTopic(topicname, mode);
        return null;
    }
    public EMomError MsgQ_CloseTopic(String topicname) throws RemoteException{
        closeTopic(topicname);
        return null;
    }

    private void closeTopic(String topicname) {
        topicQueues.remove(topicname);
    }

    public  EMomError MsgQ_Publish(String topic, String message, int type) throws RemoteException{
        topicQueues.get(topic).addMsg(new Message(message,type));


        return null;
    }
    public  EMomError MsgQ_Subscribe(String topic, TopicListenerInterface listener) throws RemoteException{
        topicQueues.get(topic).subscribe(listener);
        return null;
    }

    @Override
    public void run() {

    }
}
