/* ---------------------------------------------------------------
Práctica 1.
Código fuente: MsgRMI.java
Grau Informàtica
48053151X Pol Escolà
49263877Q Antonio López Gómez
--------------------------------------------------------------- */
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Vector;

public class MsgQServant implements MsgQ, Runnable {
    private static final long serialVersionUID = 1;
    private Hashtable<String,Vector<Message>> clientQueues = new Hashtable<>();
    private Hashtable<String, TopicQueue> topicQueues = new Hashtable<>();
    public MsgQServant() throws RemoteException{}

    private boolean existeixMsgQ(String msgqname){
        return clientQueues.get(msgqname) != null;
    }
    private boolean existeixTopicQ(String topicname){
        return topicQueues.get(topicname) != null;
    }
    public void MsgQ_Init()throws RemoteException{
        System.out.println("Client connected");
    }
    public void MsgQ_Disconnect()throws RemoteException{
        System.out.println("Client disconnected");
    }
    @Override
    public EMomError MsgQ_CreateQueue(String msgqname) throws RemoteException {
        return createQueue(msgqname);
    }

    private EMomError createQueue(String msgqname) {
        if(!existeixMsgQ(msgqname)) {
            clientQueues.put(msgqname, new Vector<>());
            addToLog("Message queue "+msgqname+" created");
            return EMomError.NoError;
        }
        addToLog("Error: Couldn't create message queue "+ msgqname+", it already exisits");
        return EMomError.JaExisteixMsgQ;
    }
    @Override
    public EMomError MsgQ_CloseQueue(String msgqname)throws RemoteException{
        return closeQueue(msgqname);
    }

    private EMomError closeQueue(String msgqname) {
        if(clientQueues.get(msgqname)!=null){
            clientQueues.remove(msgqname);
            addToLog("Mesage queue "+msgqname+" closed");
            return EMomError.NoError;
        }
        addToLog("Couldn't close message queue "+msgqname+", it doesn't exist");
        return EMomError.NoExisteixMsgQ;
    }
    @Override
    public EMomError MsgQ_SendMessage(String msgqname, String message, int type) throws RemoteException{
        return sendMessage(msgqname,message,type);
    }

    private EMomError sendMessage(String msgqname, String message, int type) {
        if(existeixMsgQ(msgqname)){
            clientQueues.get(msgqname).add(new Message(message,type));
            addToLog("Message "+message+" sent to message queue "+ msgqname );
            return EMomError.NoError;
        }
        addToLog("Couldn't send message, queue "+ msgqname+ " doesn't exist");
        return EMomError.NoExisteixMsgQ;
    }
    @Override
    public String MsgQ_ReceiveMessage(String msgqname,int type,Boolean bloqueante) throws RemoteException{
        return  receiveMessage(msgqname,type,bloqueante);
    }

    public String receiveMessage(String msgqname,int type,boolean bloqueante){
        if(existeixMsgQ(msgqname)) {
            if(bloqueante){
                int it=-1;
                while(it==-1) {
                    it = FIFOSeach(clientQueues.get(msgqname), type);
                    if (it != -1) {
                        Message msg = clientQueues.get(msgqname).remove(it);
                        addToLog("Message " + msgqname + " recieved");
                        return msg.message;
                    }
                }
            }
            else{
                int it = FIFOSeach(clientQueues.get(msgqname), type);
                if (it != -1) {
                    Message msg = clientQueues.get(msgqname).remove(it);
                    addToLog("Message " + msgqname + " recieved");
                    return msg.message;
                }
                addToLog("No messages found at queue" + msgqname);
                return null; //crec que això hauria de ser un null ["Error, no queden missatges!"]
            }
        }
        addToLog(msgqname+" queue doesn't exist");
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
    @Override
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
    @Override
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
    @Override
    public  EMomError MsgQ_Publish(String topic, String message, int type) throws RemoteException{
        return publish(topic,message,type);
    }
    public  EMomError publish(String topic, String message, int type){
        if(existeixTopicQ(topic)){
            if(topic.equals("Log")){
                topicQueues.get(topic).addMsg(new Message(message, type),topic);
                return EMomError.NoError;
            }
            else {
                topicQueues.get(topic).addMsg(new Message(message, type),topic);
                addToLog("Client published message: " + message + ", at topic: " + topic);
                return EMomError.NoError;
            }
        }
        addToLog("Client couldn't publish at topic: "+topic);
        return EMomError.NoExisteixTopicQ;
    }
    @Override
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
        publish("Log","[Log] --> "+logMsg,0);
    }
    @Override
    public void run() {
        try {
            new FileWriter("operations.log", false).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        createTopic("Log",EPublishMode.Broadcast);
    }
}
