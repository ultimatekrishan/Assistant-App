package in.kumar.krish.assistant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

    ImageButton speak;
    SpeechRecognizer speech;
    TextView text;
    String str, say, appName;
    ListView list;
    TextToSpeech tts;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    static String selectedFromList;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.list);
        speak = (ImageButton) findViewById(R.id.speakButton);

        text = (TextView) findViewById(R.id.textView);
        speak.setOnClickListener(this);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(new listener());
        arrayList = new ArrayList<String>();
        tts = new TextToSpeech(this, this);

        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_black_text, R.id.list_content, arrayList);
        list.setAdapter(adapter);


        text.setText("Welcome");
        say = "Welcome. to personal Assistant app";
        speakOut(say);
        text.setText(R.string.press);


    }

    @Override
    protected void onStop() {
        super.onStop();
        speak.setImageResource(R.drawable.tap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        speak.setImageResource(R.drawable.tap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, Settings_Activity.class);
            this.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            startActivity(intent);
            return true;
        }
        if (id == R.id.contact_developer) {
            Intent intent = new Intent(MainActivity.this, Contact_Dev_Activity.class);
            this.overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class listener implements RecognitionListener {

        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech");
        }

        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech");
        }

        public void onRmsChanged(float rmsdB) {
            Log.d(TAG, "onRmsChanged");
            text.setText("Listening....");

        }

        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "onBufferReceived");
        }

        public void onEndOfSpeech() {
            Log.d(TAG, "onEndofSpeech");
            speak.setImageResource(R.drawable.tap);
        }

        public void onError(int error) {
            Log.d(TAG, "error " + error);
            if (error == 1) {
                text.setText("Network operation timed out");
                say = "Network operation timed out";
                speakOut(say);
            } else if (error == 2) {
                text.setText("Network related errors");
                say = "Network related errors.";
                speakOut(say);
            } else if (error == 3) {
                text.setText("Audio recording error");
                say = "Audio recording error.";
                speakOut(say);
            } else if (error == 4) {
                text.setText(R.string.noconnection);
                say = "Internet Connection Not Available";
                speakOut(say);
            } else if (error == 6) {
                text.setText("No speech input");
                say = "No speech input";
                speakOut(say);
            } else if (error == 7) {
                text.setText(R.string.nosound);
                say = "No sound recorded Please try again";
                speakOut(say);
            } else if (error == 8) {
                text.setText("RecognitionService busy.Please wait");
                say = "RecognitionService busy";
                speakOut(say);

            } else if (error == 9) {
                text.setText("Insufficient permissions");
                say = "Insufficient permissions";
                speakOut(say);
            }
        }

        public void onResults(Bundle results) {

            text.setText("Results Found..");
            Log.d(TAG, "onResults " + results);
            speak.setImageResource(R.drawable.tap3);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);


            for (int i = 0; i < data.size(); i++) {
                Log.d(TAG, "result " + data.get(i));
                str += data.get(i);


                // this line adds the data of your EditText and puts in your array
                arrayList.add(data.get(i).toString());
                // next thing you have to do is check if your adapter has changed
                adapter.notifyDataSetChanged();

            }
            compareStr((String) data.get(0));
            text.setText("results: " + String.valueOf(data.size()));

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                    selectedFromList = (String) (list.getItemAtPosition(myItemInt));
                    Log.d(TAG, selectedFromList);
                    compareStr(selectedFromList);
                }
            });
        }

        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResults");
        }

        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent " + eventType);
        }

    }


    private void compareStr(String string) {
        Log.e("Speech", "" + string);

        speak.setImageResource(R.drawable.tap3);

        if (string.contains("open")) {
            String newString = string.replace("open", "");

            String app = WordUtils.capitalize(newString);
            Log.e(TAG, app);

            final PackageManager pm = getPackageManager();
            //get a list of installed apps.
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo packageInfo : packages) {
                appName = "";
                // Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
                try {
                    appName = (String) pm.getApplicationLabel(pm.getApplicationInfo(packageInfo.packageName, PackageManager.GET_META_DATA));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (app.contains(pm.getApplicationLabel(packageInfo))) {
                    speakOut("Opening" + appName + "sir");
                    Intent intent = new Intent(pm.getLaunchIntentForPackage(packageInfo.packageName));
                    //intent.setClassName(packageInfo.packageName, packageInfo.className);
                    startActivity(intent);
                }


            }
        } else if (string.contains("dial")) {

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                if (Character.isDigit(c)) {
                    builder.append(c);
                }
            }
            speakOut("Dialling sir");
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + builder.toString()));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(intent);

            Log.e(TAG, String.valueOf(builder.toString()));


        } else if (string.contains("call")) {
            String newString = string.replace("call", "");
            Log.e(TAG, newString);


        } else if (string.contains("search") || string.contains("Search")) {
            String newString = StringUtils.remove(string, "search");
            speakOut("searching" + newString);
            Uri uriUrl = Uri.parse("https://www.google.co.in/#q=" + newString);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(launchBrowser);
        } else if (string.contains("time")) {
            SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm", Locale.UK);//dd/MM/yyyy
            Date now = new Date();
            String[] strDate = sdfDate.format(now).split(":");
            if (strDate[1].contains("00")) strDate[1] = "o'clock";
            {
                speakOut("The time is " + sdfDate.format(now) + "o'clock");

            }
        } else {
            Uri uriUrl = Uri.parse("https://www.google.co.in/#q=" + string);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(launchBrowser);
        }
    }


    public void onClick(View v) {
        if (v.getId() == R.id.speakButton) {

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
            speech.startListening(intent);
            Log.i("111111", "11111111");
            arrayList.clear();
        }
    }


    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.ENGLISH);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
                text.setText("This Language is not supported");
            } else {
                speakOut(say);
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void speakOut(String text) {

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        tts.setPitch(1.7f);
        tts.setSpeechRate(0.95f);
        tts.setLanguage(Locale.ENGLISH);

    }
}



