package com.example.lab3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognizerIntent;
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

import java.util.List;
//*/

public class MainActivity extends AppCompatActivity {

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

    //private TextView txv_rgb;

    TextView txt_colorName;
    LinearLayout rgb_layout;
    ImageView img_palette;
    TextView txv_sensorTxt;

    private Switch switch_speaker;
//    private TextView txv_light;
//    private TextView txv_proximity;
    private Button btn_color;

    String spokenText;

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
    private static final int SPEECH_REQUEST_CODE = 0;

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// This starts the activity and populates the intent with the speech text.
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
// This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            spokenText = results.get(0);
            // Do something with spokenText.
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
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
                displaySpeechRecognizer();
                // Add code to execute on click

                // TEST - uncomment to test the application
                //*
                if(spokenText.equals("depict") || spokenText.equals("")) {
                    colorIndex = 3;
                    int r = colors[colorIndex * 3];
                    int g = colors[colorIndex * 3 + 1];
                    int b = colors[colorIndex * 3 + 2];
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

                    if (audioId != 0 && canSpeak) {
                        MediaPlayer mediaPlayer = MediaPlayer.create(context, audioId);
                        mediaPlayer.start();
                    }
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
                for (int i = 0; i < colors.length; i++) {
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
            /*
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





