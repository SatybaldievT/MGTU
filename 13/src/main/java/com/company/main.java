package com.company;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.util.Scanner;

public class main {
    public static String res = "";

    public static class SimpleMqttCallBack implements MqttCallback {

        public void connectionLost(Throwable throwable) {
            System.out.println("Connection to MQTT broker lost!");
        }

        public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
            res = new String(mqttMessage.getPayload());
            System.out.println("Message received:\n\t"+ res );
        }

        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            // not used in this example
        }
    }

    public static void main(String[] args) {

        int ax,bx,by,ay,dx,dy;
        Scanner in = new Scanner(System.in);
        ax= in.nextInt(); ay= in.nextInt();
        bx = in.nextInt(); by= in.nextInt();
        dx = in.nextInt(); dy= in.nextInt();


        String topic        = "MainTalgat";
        String content      = "" + ax + " "
                + ay + " "
                + bx + " "
                + by + " "
                + dx + " "
                + dy ;

        int qos             = 2;
        String broker       = "tcp://mqtt.eclipseprojects.io:1883";
        String clientId     = "aaaaaa";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            System.out.println("Publishing message: "+content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);

            sampleClient.publish(topic, message);
            System.out.println("Message published");
            MqttClient client=new MqttClient(broker, MqttClient.generateClientId());
            SimpleMqttCallBack call = new SimpleMqttCallBack();
            client.setCallback( call);
            client.connect();
            client.subscribe(topic);
            //client.getTopic(topic);
            call.messageArrived(topic, message);

            int s[] = new int[6];
            int j = 0, t = 0;
            for (int i = 0; i < res.length(); i++){
                if (res.charAt(i) != ' '){
                    t *= 10;
                    t += res.charAt(i) - '0';
                }
                else{
                    s[j] = t;
                    j++;
                    t = 0;
                }
            }
            s[j] = t;
            for (int i = 0; i < 6; i++){
                System.out.println(s[i]);
            }
            double a=Math.sqrt((s[0]-s[2])*(s[0]-s[2])+(s[1]-s[3])*(s[1]-s[3]));
            double b=Math.sqrt((s[0]-s[4])*(s[0]-s[4])+(s[1]-s[5])*(s[1]-s[5]));
            double c=Math.sqrt((s[4]-s[2])*(s[4]-s[2])+(s[5]-s[3])*(s[5]-s[3]));

            double p=(a+b+c) /2;
            double ress = Math.sqrt(p*(p-a)*(p-b)*(p-c)) ;

            MqttMessage messageDet = new MqttMessage(("" + ress).getBytes());
            message.setQos(qos);
            System.out.println(" message: " + ress);
            System.out.println("Message published");

            sampleClient.publish(topic, messageDet);



            MqttClient client2=new MqttClient(broker, MqttClient.generateClientId());
            SimpleMqttCallBack call2 = new SimpleMqttCallBack();
            client2.setCallback( call2);
            client2.connect();
            client2.subscribe(topic);
            call2.messageArrived(topic, messageDet);
            System.out.println("answer: " + res );

            client.disconnect();
            client2.disconnect();
            sampleClient.disconnect();
            System.out.println("Disconnected");
            System.exit(0);
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}