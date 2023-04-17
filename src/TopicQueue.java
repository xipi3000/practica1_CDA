/* ---------------------------------------------------------------
Práctica 1.
Código fuente: MsgRMI.java
Grau Informàtica
48053151X Pol Escolà
49263877Q Antonio López Gómez
--------------------------------------------------------------- */
import java.io.*;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

public class TopicQueue {
    Vector<Message> topicQueue;
    EPublishMode publishMode;
    Vector<TopicListenerInterface> listenerQueue;
    Vector<TopicListenerInterface> roundRobinQueue;
    public TopicQueue(EPublishMode publishMode){
        this.topicQueue = new Vector<>();
        this.publishMode = publishMode;
        this.listenerQueue = new Vector<>();
        this.roundRobinQueue = new Vector<>();
    }
    public void addMsg(Message message,String topic){
        topicQueue.add(message);
        if(topic.equals("Log")) {
            try {
                File f1 = new File("operations.log");
                if(!f1.exists()) {
                    f1.createNewFile();
                }
                FileWriter fileWritter = new FileWriter(f1.getName(),true);
                BufferedWriter bw = new BufferedWriter(fileWritter);
                bw.write(message.message+"\n");
                bw.close();
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
        else{
            if (publishMode == EPublishMode.RoundRobin) {
                for (Enumeration e = listenerQueue.elements(); e.hasMoreElements(); ) {
                    TopicListenerInterface listener = (TopicListenerInterface) e.nextElement();
                    if (!roundRobinQueue.contains(listener)) {
                        try {

                            listener.onTopicMessage(message.message, topic);
                        } catch (RemoteException re) {
                            System.out.println(" Listener not accessible, removing listener -" + listener);
                            // Remote the listener
                            listenerQueue.remove(listener);
                            roundRobinQueue.remove(listener);
                        }
                        roundRobinQueue.add(listener);
                        break;
                    } else if (roundRobinQueue.size() == listenerQueue.size()) {
                        roundRobinQueue.clear();
                        e = listenerQueue.elements();
                    }


                }
            } else if (publishMode == EPublishMode.Broadcast) {

                for (Enumeration e = listenerQueue.elements(); e.hasMoreElements(); ) {
                    TopicListenerInterface listener = (TopicListenerInterface) e.nextElement();
                    try {
                        listener.onTopicMessage(message.message, topic);
                    } catch (RemoteException re) {
                        System.out.println(" Listener not accessible, removing listener -" + listener);
                        // Remote the listener
                        listenerQueue.remove(listener);
                    }
                }
            }
            topicQueue.remove(message);
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
                //Si es necessari buidar la llista, fem un .clear al acabar el for. Sino simplement treiem-ho, perque
                //amb això funciona mal.
                //listenerQueue.remove( listener );
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
