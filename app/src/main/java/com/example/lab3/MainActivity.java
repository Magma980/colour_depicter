package com.example.lab3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
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

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // RGB colors
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

    // ID for sounds in resourses (res)
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

    // color names
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

    TextView txt_colorName;
    LinearLayout rgb_layout;
    ImageView img_palette;
    TextView txv_sensorTxt;

    private Button btn_color;

    String spokenText = "";

    int RGBMessage;
    int r;
    int g;
    int b;
    int colorIndex = 18;
    String[] colorValues;
    private boolean canSpeak = true;
    private final int Scale = 4;

    private static final int SPEECH_REQUEST_CODE = 0;

    // From the MQTT lab:
    private MqttAndroidClient client;
    private static final String SERVER_URI = "tcp://test.mosquitto.org:1883";
    private static final String TAG = "MainActivity";
    private static final String TOPICRGB = "RGB"; // YOUR TOPIC HERE, must match the Python script!!!

    // Called ONLY ONCE, at the activity start.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the current activity look to the XML designed in Android Studio
        setContentView(R.layout.activity_main);

        // Our own initializations

        // get all controls and store them into corresponding class variables
        txt_colorName = findViewById(R.id.txt_colorName);
        txv_sensorTxt = findViewById(R.id.txv_sensorTxt);
        rgb_layout = findViewById(R.id.rgb_layout);
        img_palette = findViewById(R.id.img_palette);
        btn_color = findViewById(R.id.btnColor);

        // HIDE controls at the beginning, so that they are not visible on the screen (they are GONE)
        txt_colorName.setVisibility(View.GONE);
        rgb_layout.setVisibility(View.GONE);
        img_palette.setVisibility(View.GONE);

        // set the initial text
        txv_sensorTxt.setText("Nothing yet!");

        // PALETTE
        //*
        int numColors = colorIds.length;
        int[] palette = new int[numColors];

        for (int i = 0; i < numColors; i++) {
            palette[i] = (255 << 24) | (colors[3 * i] << 16) | (colors[3 * i + 1] << 8) | (colors[3 * i + 2]);
        }

        try {
            Bitmap bitmap = Bitmap.createBitmap(palette, 0, numColors, numColors, 1,
                    Bitmap.Config.ARGB_8888);

            img_palette.setImageBitmap(bitmap);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //*/

        // SWITCH to turn ON/OFF the voice announcement for the color name
        // initialize the Switch locally (we do not save it into the class variable)
        Switch switch_speaker = findViewById(R.id.speakerSwitch);
        // Set the initial switch state
        switch_speaker.setChecked(canSpeak);
        // set the function callback that will be called when the user touches the switch control
        switch_speaker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                canSpeak = isChecked;
            }
        });

        // set the function callback that will be called when the user touches the button
        btn_color.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // The code to execute on click:
                displaySpeechRecognizer();
            }
        });

        // From the MQTT lab: connect the client to Raspberry PI
        connect();

        //*
        // From the MQTT lab: set the client callback to subscribe to the RGB sensor messages
        // The callback will be called each time we get a message from the RGB sensor
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (reconnect) {
                    System.out.println("Reconnected to : " + serverURI);
                    // Re-subscribe as we lost it due to new session
                    subscribe(TOPICRGB);
                } else {
                    System.out.println("Connected to: " + serverURI);
                    subscribe(TOPICRGB);
                }
            }
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("The Connection was lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                colorValues = new String(message.getPayload()).split(",");
                // 1. we extract each color component from the string
                // 2. we scale each color component by Scale
                // 3. after scaling the value may become > 255, we clip it to 255
                r = Math.min(255, Scale * Integer.parseInt(colorValues[0]));
                g = Math.min(255, Scale * Integer.parseInt(colorValues[1]));
                b = Math.min(255, Scale * Integer.parseInt(colorValues[2]));
                RGBMessage = Color.rgb(r, g, b);

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

                // file name to play the sound
                txt_colorName.setText(colorNames[colorIndex]);

                txv_sensorTxt.setText("R: " + r + "    G: " + g + "    B: " + b);

                btn_color.setBackgroundColor(RGBMessage);

                // set the best text color so it is visible on top of the background color
                setTextColor();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
        //*/

        // show the Google voice recognizer
        displaySpeechRecognizer();
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            spokenText = results.get(0);
            // convert the recognized text to the lower case for simplicity
            spokenText = spokenText.toLowerCase();
            // Do something with the recognized text
            recognizeColor();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // This starts the activity and populates the intent with the speech text.
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    private void recognizeColor() {

        if (spokenText.equals("depict") || spokenText.equals("try") || spokenText.equals("do") || spokenText.equals("")) {

            r = colors[colorIndex * 3];
            g = colors[colorIndex * 3 + 1];
            b = colors[colorIndex * 3 + 2];

            txv_sensorTxt.setText("R: " + r + "    G: " + g + "    B: " + b);
            btn_color.setBackgroundColor(Color.rgb(r, g, b));
            txt_colorName.setText(colorNames[colorIndex]);
            setTextColor();

            // SHOW controls:
            txt_colorName.setVisibility(View.VISIBLE);
            rgb_layout.setVisibility(View.VISIBLE);
            //img_palette.setVisibility(View.VISIBLE);

            if (canSpeak) {
                // get the audio resource ID
                int audioId = colorIds[colorIndex];
                MediaPlayer mediaPlayer = MediaPlayer.create(this, audioId);
                mediaPlayer.start();
            }
        }
    }

    // We calculate the best text color so it will be visible on the current button background
    private void setTextColor() {

        int r1, g1, b1;
        if (r < 128) r1 = 255;
        else r1 = 0;

        if (g < 128) g1 = 255;
        else g1 = 0;

        if (b < 128) b1 = 255;
        else b1 = 0;

        btn_color.setTextColor(Color.rgb(r1, g1, b1));
    }

    // Try to connect to Raspberry PI using MQTT client
    private void connect() {
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), SERVER_URI, clientId);
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





