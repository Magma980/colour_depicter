package com.example.lab3;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    private TextView txv_rgb;
//    private TextView txv_light;
//    private TextView txv_proximity;
    private Button btn_color;
    int RGBMessage;
    int r;
    int g;
    int b;
    String[] colorValues;

    private MainActivity context;
    private MqttAndroidClient client;
    private static final String SERVER_URI = "tcp://test.mosquitto.org:1883";
    private static final String TAG = "MainActivity";

//    private static final String TOPICPROX = "PROXIMITY"; // YOUR TOPIC HERE, must match the Python script!!!
//    private static final String TOPICLUX = "LUX"; // YOUR TOPIC HERE, must match the Python script!!!
    private static final String TOPICRGB = "RGB"; // YOUR TOPIC HERE, must match the Python script!!!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        txv_rgb = (TextView) findViewById(R.id.txv_rgbValue);
//        txv_light = (TextView) findViewById(R.id.txv_lightValue);
//        txv_proximity = (TextView) findViewById(R.id.txv_proximityValue);
        btn_color = (Button) findViewById(R.id.btnColor);

        connect();

        btn_color.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Add code to execute on click

                MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.red);
                mediaPlayer.start();

                btn_color.setTextColor(RGBMessage);
            }
        });

        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (reconnect) {
                    System.out.println("Reconnected to : " + serverURI);
                    // Re-subscribe as we lost it due to new session
//                    subscribe(TOPICPROX);
//                    subscribe(TOPICLUX);
                    subscribe(TOPICRGB);
                } else {
                    System.out.println("Connected to: " + serverURI);
//                    subscribe(TOPICPROX);
//                    subscribe(TOPICLUX);
                    subscribe(TOPICRGB);
                }
            }
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("The Connection was lost.");
            }
//            String proxMessage;
//            String luxMessage;

            final String [] colorNames = {"aqua",
                    "black",
                    "blue",
                    "red",
                    "fuchsia",
                    "gray",
                    "green",
                    "lime",
                    "maroon",
                    "navy",
                    "olive",
                    "purple",
                    "silver",
                    "teal",
                    "white",
                    "yellow"};
            final int [] colorIds = {R.raw.aqua,
                    R.raw.black,
                    R.raw.blue,
                    R.raw.red,
                    R.raw.fuchsia,
                    R.raw.gray,
                    R.raw.green,
                    R.raw.lime,
                    R.raw.maroon,
                    R.raw.navy,
                    R.raw.olive,
                    R.raw.purple,
                    R.raw.silver,
                    R.raw.teal,
                    R.raw.white,
                    R.raw.yellow
            };
            final int[] colors = {0,255,255,
                    0,0,0,
                    0,0,255,
                    255,0,0,
                    255,0,255,
                    128,128,128,
                    0,128,0,
                    0,255,0,
                    128,0,0,
                    0,0,128,
                    128,128,0,
                    128,0,128,
                    192,192,192,
                    0,128,128,
                    255,255,255,
                    255,255,0};

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
//                if(topic.equals("PROXIMITY")){
//                     proxMessage = new String(message.getPayload());
//                }
//                else if (topic.equals("LUX")){
//                    luxMessage = new String(message.getPayload());
//                }
//                else{
                    colorValues = new String(message.getPayload()).split(",");
                    r = Integer.parseInt(colorValues[0]);
                    g = Integer.parseInt(colorValues[1]);
                    b = Integer.parseInt(colorValues[2]);
                    RGBMessage = Color.rgb(r, g, b);
                    System.out.println(RGBMessage);

                    int index = 0;
                    int minDist = Integer.MAX_VALUE;
                    for (int i=0; i<colors.length; i++)
                    {
                        // get i-th color RGB values from the Color array
                        int r1 = colors[3*i];
                        int g1 = colors[3*i +1];
                        int b1 = colors[3*i + 2];
                        // calculate the squared distance from our color RGB to the i-th table color
                        int dist = (r1-r)*(r1-r) + (g1-g)*(g1-g) + (b1-b)*(b1-b);

                        // update the min distance and its color table index
                        if (dist < minDist)
                        {
                            minDist =dist;
                            index = i;
                        }
                    }

                    // now we have the closest color in the color table, get the color name
                    String name = colorNames[index];
                    // file name to play the sound
                    //String fileName = name +".mp3";
                    int audioId = colorIds[index];

                // PLAY THE SOUND HERE
                MediaPlayer mediaPlayer = MediaPlayer.create(context, audioId);
                mediaPlayer.start();
                //System.out.println(RGBMessage);
//              }
                String newMessage = new String(message.getPayload());
                System.out.println("Incoming message: " + newMessage);

                /* add code here to interact with elements
                 (text views, buttons)
                 using data from newMessage
                */
                // Uncomment accordingly
                //txv_proximity.setText(proxMessage);
                txv_rgb.setText(RGBMessage+"");
                //txv_light.setText(luxMessage);
                //txv_rgb.setTextColor(RGBMessage);
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
    }

    private void connect(){
        String clientId = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(this.getApplicationContext(), SERVER_URI, clientId);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    System.out.println(TAG + " Success. Connected to " + SERVER_URI);
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");
                    System.out.println(TAG + " Oh no! Failed to connect to " + SERVER_URI);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void subscribe(String topicToSubscribe) {
        final String topic = topicToSubscribe;
        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Subscription successful to topic: " + topic);
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    System.out.println("Failed to subscribe to topic: " + topic);
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


}





