package ar.edu.ahk;

import io.javalin.Javalin;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Server implements MqttCallback {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(getHerokuAssignedPort());
        app.get("/", ctx -> ctx.result("Test AHK MQTT"));
	new Server().subscribe(System.getenv("MQTTClient"));
    }

    private static int getHerokuAssignedPort() {
        String herokuPort = System.getenv("PORT");
        if (herokuPort != null) {
            return Integer.parseInt(herokuPort);
        }
        return 7000;
    }

    private static final String brokerUrl ="tcp://node02.myqtthub.com:1883";
//    private static final String brokerUrl ="tcp://node02.myqtthub.com:1883";

    /** The topic. */
    private static final String topic = "temp";

    public void subscribe(String clientId) {
        //    logger file name and pattern to log
        MemoryPersistence persistence = new MemoryPersistence();
        try
        {
            MqttClient sampleClient = new MqttClient(brokerUrl, clientId, persistence);
            System.out.println("Starting: " + clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName("eze");
            connOpts.setPassword("123".toCharArray());
            connOpts.setCleanSession(true);
            System.out.println("checking");
            System.out.println("Mqtt Connecting to broker: " + brokerUrl);
            sampleClient.connect(connOpts);
            System.out.println("Mqtt Connected");
            sampleClient.setCallback(this);
            sampleClient.subscribe(topic);
            System.out.println("Subscribed");
            System.out.println("Listening");

        } catch (MqttException me) {
            System.out.println(me);
        }
    }
    //Called when the client lost the connection to the broker
    public void connectionLost(Throwable arg0) {
        System.out.println(arg0);
    }
    //Called when a outgoing publish is complete
    public void deliveryComplete(IMqttDeliveryToken arg0) {
    }
    public void messageArrived(String topic, MqttMessage message) {
        System.out.println("Topic:" + topic);
        System.out.println("Message: " +message.toString());
    }

}
