package com.example.lab3;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;


/*
import info.mqtt.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/*/
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
//*/

public class MainActivity extends AppCompatActivity {

    final int[] colors = {0,255,255,
            //0,0,0,
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
            255,255,0,
            255, 125, 0,
            255, 250, 250,
            255, 127, 80,
            150,75,0,
            123,63,0,
            255,219,88,
            194,178,128,
            127, 255, 212,
            62,180,137,
            0,127,255,
            0,71,171,
            18,10,143,
            181,126,220,
            143,0,255,
            142, 69, 133,
            255,192,203,
            224,17,95,
            128,0,32,
            196,30,58,
            101,0,11,
            32,32,0,
            32,0,0,
            0,32,0,
            0,0,32
    };

    final int [] colorIds = {R.raw.aqua,
            //R.raw.black,
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
            R.raw.yellow,
            R.raw.orange,
            R.raw.snow,
            R.raw.coral,
            R.raw.brown,
            R.raw.chocolate,
            R.raw.mustarad,
            R.raw.sand,
            R.raw.aquamarine,
            R.raw.mint,
            R.raw.azure,
            R.raw.cobalt,
            R.raw.ultramarine,
            R.raw.lavender,
            R.raw.violet,
            R.raw.plum,
            R.raw.pink,
            R.raw.ruby,
            R.raw.burgundy,
            R.raw.cardinal,
            R.raw.rosewood,
            R.raw.yellow,
            R.raw.red,
            R.raw.green,
            R.raw.blue
    };

    final String [] colorNames = {"aqua",
            //"black",
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
            "yellow",
            "orange",
            "snow",
            "coral",
            "brown",
            "chocolate",
            "mustarad",
            "sand",
            "aquamarine",
            "mint",
            "azure",
            "cobalt",
            "ultramarine",
            "lavender",
            "violet",
            "plum",
            "pink",
            "ruby",
            "burgundy",
            "cardinal",
            "rosewood",
            "dark yellow",
            "dark red",
            "dark green",
            "dark blue",
    };

    //private TextView txv_rgb;

    TextView txt_colorName;
    LinearLayout rgb_layout;
    ImageView img_palette;
    TextView txv_sensorTxt;

    private Switch switch_speaker;
//    private TextView txv_light;
//    private TextView txv_proximity;
    private Button btn_color;

    int RGBMessage;
    int r;
    int g;
    int b;
    int colorIndex = 0;
    String[] colorValues;
    private boolean canSpeak = true;
    private int colorWhite = Color.rgb(255, 255, 255);
    private int colorBlack = Color.rgb(0, 0, 0);

    private int audioId;

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

        // initialize the Switch
        switch_speaker = findViewById(R.id.speakerSwitch);
        switch_speaker.setChecked(canSpeak);
        //txv_rgb = findViewById(R.id.txv_rgbValue);
        txt_colorName = findViewById(R.id.txt_colorName);
        txv_sensorTxt = findViewById(R.id.txv_sensorTxt);
        rgb_layout = findViewById(R.id.rgb_layout);
        img_palette = findViewById(R.id.img_palette);

        // HIDE:
        txt_colorName.setVisibility(View.GONE);
        rgb_layout.setVisibility(View.GONE);
        img_palette.setVisibility(View.GONE);

        // set the initial text
        txv_sensorTxt.setText("Nothing yet!");


//        txv_light = (TextView) findViewById(R.id.txv_lightValue);
//        txv_proximity = (TextView) findViewById(R.id.txv_proximityValue);
        btn_color = findViewById(R.id.btnColor);

        switch_speaker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                canSpeak = isChecked;
            }
        });

        btn_color.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Add code to execute on click

                // TEST - uncomment to test the application
                //*
                colorIndex = 3;
                int r = colors[colorIndex*3];
                int g = colors[colorIndex*3+1];
                int b = colors[colorIndex*3+2];
                audioId = colorIds[colorIndex];
                txv_sensorTxt.setText("R: " + r + "    G: " + g + "    B: " + b);
                btn_color.setBackgroundColor(Color.rgb(r, g, b));
                txt_colorName.setText(colorNames[colorIndex]);
                if (colorIndex == 1) {
                    btn_color.setTextColor(colorWhite);
                } else {
                    btn_color.setTextColor(colorBlack);
                }
                //*/

                // SHOW controls:
                txt_colorName.setVisibility(View.VISIBLE);
                rgb_layout.setVisibility(View.VISIBLE);
                img_palette.setVisibility(View.VISIBLE);

                // PALETTE
                //*
                int numColors = colorIds.length;
                int[] palette = new int[numColors];

                for (int i = 0; i < numColors; i++) {
                    palette[i] = (255 << 24) | (colors[3*i] << 16) | (colors[3*i + 1] << 8) | (colors[3*i + 2]);
                }

                try {
                    Bitmap bitmap = Bitmap.createBitmap(palette, 0, numColors, numColors, 1,
                            Bitmap.Config.ARGB_8888);

                    img_palette.setImageBitmap(bitmap);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                //*/

                if (audioId != 0 && canSpeak) {
                    String colorName = colorNames[colorIndex];
                    if (colorName.startsWith("dark")) {
                        MediaPlayer mediaPlayer1 = MediaPlayer.create(context, R.raw.dark);
                        mediaPlayer1.start();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                        }
                    }
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, audioId);
                    mediaPlayer.start();
                }
            }
        });

        connect();

 //*
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

                colorIndex = 0;
                int minDist = Integer.MAX_VALUE;
                for (int i = 0; i < colors.length/3; i++) {
                    // get i-th color RGB values from the Color array
                    int r1 = colors[3 * i];
                    int g1 = colors[3 * i + 1];
                    int b1 = colors[3 * i + 2];
                    // calculate the squared distance from our color RGB to the i-th table color
                    int dist = (r1 - r) * (r1 - r) + (g1 - g) * (g1 - g) + (b1 - b) * (b1 - b);

                    // update the min distance and its color table index
                    if (dist < minDist) {
                        minDist = dist;
                        colorIndex = i;
                    }
                }

                //System.out.println("test");

                // now we have the closest color in the color table, get the color name
                //String name = colorNames[index];
                // file name to play the sound
                txt_colorName.setText(colorNames[colorIndex]);

                audioId = colorIds[colorIndex];

                String newMessage = new String(message.getPayload());
                System.out.println("Incoming message: " + newMessage);
                // add code here to interact with elements
                // (text views, buttons)
                // using data from newMessage
                //
                // Uncomment accordingly
                //txv_proximity.setText(proxMessage);
                //txv_rgb.setText(RGBMessage+"");
                txv_sensorTxt.setText("R: " +r + "    G: " +g + "    B: " + b);

                //txv_light.setText(luxMessage);
                btn_color.setBackgroundColor(RGBMessage);
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
        //*/
    }

    private void connect() {
        String clientId = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(this.getApplicationContext(), SERVER_URI, clientId);
        try {
            //*
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
            //*/
        } catch (Exception e) {
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





