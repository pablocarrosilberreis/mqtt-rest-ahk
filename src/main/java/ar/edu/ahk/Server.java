package ar.edu.ahk;

import io.javalin.Javalin;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class Server implements MqttCallback {
   
    private String tempActual = "?";
    private MqttClient client ;
    public MqttClient getClient(){
    return client;
    }

    public String getTempActual(){
    	return tempActual;
    }
    public void setTempActual(String tempActual){
     this.tempActual = tempActual;
    }

    public static void main(String[] args) {
	Server mqttServ = new Server();
        Javalin app = Javalin.create().start(getHerokuAssignedPort());
        app.get("/", ctx -> ctx.result("Test AHK MQTT - temometro: " + mqttServ.getTempActual()));
        app.get("/termometro/:temp", ctx -> {
		int qos             = 2;
		String temp = ctx.pathParam("temp") ;
		MqttMessage message = new MqttMessage(temp.getBytes());
                message.setQos(qos);
                mqttServ.getClient().publish("temp", message);
                mqttServ.setTempActual(temp);
		ctx.result("temometro actualizado: " + mqttServ.getTempActual());
	app.get(){
			CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpGet request = new HttpGet("https://maker.ifttt.com/trigger/test/with/key/n5ezzyNV74srbAwmRhPVu3cZfhRpczvkvhoXzd4C8xY");
                System.out.println(httpClient.execute(request).getEntity().toString());}
	}
	);
	app.get("/test",ctx -> {
	
		CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpGet request = new HttpGet("https://ahkblipo.herokuapp.com/termometro/32");
                System.out.println(httpClient.execute(request).getEntity().toString());
                
	});
	mqttServ.subscribe(System.getenv("MQTTClient"));
    }

    private static int getHerokuAssignedPort() {
        String herokuPort = System.getenv("PORT");
        if (herokuPort != null) {
            return Integer.parseInt(herokuPort);
        }
        return 7000;
    }

    private static final String brokerUrl ="tcp://node02.myqtthub.com:1883";

    /** The topic. */
    private static final String topic = "temp";

    public void subscribe(String clientId) {
        //    logger file name and pattern to log
        MemoryPersistence persistence = new MemoryPersistence();
        try
        {
            this.client = new MqttClient(brokerUrl, clientId, persistence);
            System.out.println("Starting: " + clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName("ahkpabloheroku");
            connOpts.setPassword("lunes001".toCharArray());
            connOpts.setCleanSession(true);
            System.out.println("checking");
            System.out.println("Mqtt Connecting to broker: " + brokerUrl);
            client.connect(connOpts);
            System.out.println("Mqtt Connected");
            client.setCallback(this);
            client.subscribe(topic);
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
	this.tempActual = message.toString();
        System.out.println("Topic:" + topic);
        System.out.println("Message: " +message.toString());
    }

}
