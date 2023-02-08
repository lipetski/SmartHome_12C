package at.co.shaman.smarthome12c;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class RollerRules extends AppCompatActivity {

    private RuleViewAdapter _ruleAdapter;
    private TextView lblEG1;
    private TextView lblEG2;
    private TextView lblPolina;
    private TextView lblBedroom;
    private String _name;

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data  ) {
        super.onActivityResult( requestCode, resultCode, data );

        if( resultCode == requestCode ) {
            queryRules(_name);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roller_rules);

        RecyclerView lst = findViewById(R.id.lstRules);
        lst.setLayoutManager(new LinearLayoutManager(this));

        lblEG1 = findViewById( R.id.lblEG1 );
        lblEG2 = findViewById( R.id.lblEG2 );
        lblPolina = findViewById( R.id.lblPolina );
        lblBedroom = findViewById( R.id.lblBedroom );

        lblEG1.setTypeface( Typeface.DEFAULT_BOLD);
        _name = "EG1";

        lblEG1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lblEG1.setTypeface(Typeface.DEFAULT_BOLD);
                lblEG2.setTypeface(Typeface.DEFAULT);
                lblBedroom.setTypeface(Typeface.DEFAULT);
                lblPolina.setTypeface(Typeface.DEFAULT);
                queryRules( "EG1" );
                _name = "EG1";
            }
        });

        lblEG2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lblEG2.setTypeface(Typeface.DEFAULT_BOLD);
                lblEG1.setTypeface(Typeface.DEFAULT);
                lblBedroom.setTypeface(Typeface.DEFAULT);
                lblPolina.setTypeface(Typeface.DEFAULT);
                queryRules( "EG2" );
                _name = "EG2";
            }
        });

        lblBedroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lblBedroom.setTypeface(Typeface.DEFAULT_BOLD);
                lblEG2.setTypeface(Typeface.DEFAULT);
                lblEG1.setTypeface(Typeface.DEFAULT);
                lblPolina.setTypeface(Typeface.DEFAULT);
                queryRules( "Bedroom" );
                _name = "Bedroom";
            }
        });

        lblPolina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lblPolina.setTypeface(Typeface.DEFAULT_BOLD);
                lblEG2.setTypeface(Typeface.DEFAULT);
                lblBedroom.setTypeface(Typeface.DEFAULT);
                lblEG1.setTypeface(Typeface.DEFAULT);
                queryRules( "Polina" );
                _name = "Polina";
            }
        });

        queryRules( "EG1" );
        _name = "EG1";

        Button btnAdd = findViewById( R.id.btnRuleAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( RollerRules.this, EditRule.class);

                JSONObject next = new JSONObject();
                try {
                    next.put( "Type", "single" );
                    Calendar calendar = Calendar.getInstance();
                    int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                    int currentMinute = calendar.get(Calendar.MINUTE);
                    int currentMinute5 = currentMinute / 5;
                    currentMinute5 *= 5;
                    next.put( "Time", String.format( "%02d:%02d", currentHour, currentMinute5 ) );
                    next.put( "Active", "1" );
                    next.put( "Action", "open" );
                    JSONArray jArray = new JSONArray();
                    for( int i = 0; i < 7; i++ ) {
                        jArray.put( i + 1 );
                    }
                    next.put( "days", jArray );
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                intent.putExtra("data", next.toString() );
                intent.putExtra( "position", "-1" );
                intent.putExtra( "name", _name );
                intent.putExtra( "active", "1" );
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 2);
            }
        });

    }

    private void queryRules( String name ) {
        final SmarthomeApp app = (SmarthomeApp) getApplication();
        StringBuilder content = new StringBuilder();
        app.getExecutors().networkIO().execute(() -> {
            try {
                URL filterUrl = new URL("http://nas8055.synology.me:50111/rest/roller_rules/" + name);
                //URL filterUrl = new URL("http://192.168.13.4:8085/rest/roller_rules/" + name);
                HttpURLConnection connection = (HttpURLConnection) filterUrl.openConnection();
                connection.setReadTimeout(2000); // 2 sec
                connection.setConnectTimeout(2000); // 2 sec
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                connection.connect();
                int statusCode = connection.getResponseCode();
                if (statusCode != 200) {
                }

                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                reader.close();
                is.close();
                connection.disconnect();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RecyclerView lst = findViewById(R.id.lstRules);
                        _ruleAdapter = new RuleViewAdapter();
                        lst.setAdapter(_ruleAdapter);
                        _ruleAdapter.setRules(content.toString(), name, app, RollerRules.this );
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
