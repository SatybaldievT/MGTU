package com.company;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Scanner;

public class MqttPublishSample {
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

        int a, b, c, d;
        Scanner in = new Scanner(System.in);
        a = in.nextInt(); b = in.nextInt(); c = in.nextInt(); d = in.nextInt();


        String topic        = "MQTT Examples";
        String content      = "" + a + " " + b + " " + c + " " + d;
        int qos             = 2;
        String broker       = "tcp://mqtt.eclipseprojects.io:1883";
        String clientId     = "JavaSample";
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
            int s[] = new int[4];
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
            for (int i = 0; i < 4; i++){
                System.out.println(s[i]);
            }

            int det = s[0] * s[3] - s[1] * s[2];

            MqttMessage messageDet = new MqttMessage(("" + det).getBytes());
            message.setQos(qos);
            System.out.println("Pablishing message: " + det);
            System.out.println("Message published");
            sampleClient.publish(topic, messageDet);


            MqttClient client2=new MqttClient(broker, MqttClient.generateClientId());
            SimpleMqttCallBack call2 = new SimpleMqttCallBack();
            client2.setCallback( call2);
            client2.connect();
            client2.subscribe(topic);
            call2.messageArrived(topic, messageDet);

            System.out.println("answer: " + res);

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