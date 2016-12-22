package in.kumar.krish.assistant;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Settings_Activity extends AppCompatActivity {

    TextView app;
    String appName;
    private static final String TAG = "SettingsActivity";
    //String[] val = {};
    ArrayList<String> val;
    Dialog listDialog;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        app = (TextView)findViewById(R.id.open_sub);
        val = new ArrayList<String>();


        app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"text pressed");
                showdialog();
            }
        });


    }

    private void showdialog() {
        listDialog = new Dialog(this);
        listDialog.setTitle("Installed Apps");
        LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.popup, null, false);
        listDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        listDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        listDialog.setContentView(v);
        listDialog.setCancelable(true);
        //there are a lot of settings, for dialog, check them all out!

        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
//The log is not required so if you want to and I recommend during release you remove that statement.
            Log.d(TAG, "Installed package :" + packageInfo.packageName);
            try {
                appName = (String) pm.getApplicationLabel(pm.getApplicationInfo(packageInfo.packageName, PackageManager.GET_META_DATA));
                val.add(appName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            ListView list1 = (ListView) listDialog.findViewById(R.id.list2);
            list1.setAdapter(new ArrayAdapter<String>(this, R.layout.list_white_text, val));
            //now that the dialog is set up, it's time to show it
            listDialog.show();
        }
    }

}
