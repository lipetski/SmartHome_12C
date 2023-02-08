package at.co.shaman.smarthome12c;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SwitchBoiler extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switch_boiler);

        Spinner spnTime = (Spinner) findViewById(R.id.spnMode);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.BoilerSwitchMode, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spnTime.setAdapter(adapter);

        Spinner spnDuration = (Spinner) findViewById(R.id.spnDuration);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.BoilerIntervals, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spnDuration.setAdapter(adapter2);

        EditText timeStart = findViewById(R.id.edtTimeStart);
        timeStart.setVisibility(EditText.INVISIBLE);

        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        timeStart.setText( String.format( "%02d:%02d", currentHour, currentMinute ) );

        timeStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SwitchBoiler.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        timeStart.setText( String.format( "%02d:%02d", selectedHour , selectedMinute ) );
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        Calendar calEnd = (Calendar)calendar.clone();
        calEnd.add(Calendar.HOUR_OF_DAY, 1);

        EditText timeEnd = findViewById(R.id.edtTimeEnd);
        timeEnd.setVisibility(EditText.INVISIBLE);
        int endHour = calEnd.get(Calendar.HOUR_OF_DAY);
        int endMinute = calEnd.get(Calendar.MINUTE);
        timeEnd.setText( String.format( "%02d:%02d", endHour, endMinute ) );

        timeEnd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SwitchBoiler.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        timeEnd.setText( String.format( "%02d:%02d", selectedHour , selectedMinute ) );
                    }
                }, endHour, endMinute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        spnTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView durMin = findViewById(R.id.lblDurationMin);
                durMin.setVisibility(TextView.VISIBLE);
                if( position == 0) {
                    timeStart.setVisibility(EditText.INVISIBLE);
                    timeEnd.setVisibility(EditText.INVISIBLE);
                    spnDuration.setVisibility(EditText.VISIBLE);
                }
                else if( position == 1 ) {
                    timeStart.setVisibility(EditText.VISIBLE);
                    timeEnd.setVisibility(EditText.INVISIBLE);
                    spnDuration.setVisibility(EditText.VISIBLE);
                }
                else if( position == 2 ) {
                    timeStart.setVisibility(EditText.INVISIBLE);
                    timeEnd.setVisibility(EditText.VISIBLE);
                    spnDuration.setVisibility(EditText.VISIBLE);
                }
                else {
                    timeStart.setVisibility(EditText.INVISIBLE);
                    timeEnd.setVisibility(EditText.INVISIBLE);
                    spnDuration.setVisibility(EditText.INVISIBLE);
                    durMin.setVisibility(TextView.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnDuration.setSelection(1);
        Button btnOk = findViewById( R.id.btnOKBoilerSwitch );
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final SmarthomeApp app = (SmarthomeApp) getApplication();
                Context context = getApplicationContext();

                Spinner spnMode = findViewById( R.id.spnMode );
                int start_hour = 0, end_hour = 0, start_minute = 0, end_minute = 0;
                int pos = spnMode.getSelectedItemPosition();
                int dur_minutes = ( spnDuration.getSelectedItemPosition() + 1 ) * 15;
                final long ONE_MINUTE_IN_MILLIS = 60000;
                if( pos == 0 ) {
                    Calendar cal = Calendar.getInstance();
                    start_hour = calendar.get(Calendar.HOUR_OF_DAY);
                    start_minute = calendar.get(Calendar.MINUTE);
                    Calendar calEnd = (Calendar)cal.clone();
                    calEnd.add(Calendar.MINUTE, dur_minutes);
                    end_hour = calEnd.get(Calendar.HOUR_OF_DAY);
                    end_minute = calEnd.get(Calendar.MINUTE);
                }
                else if( pos == 1 ) {
                    String str = timeStart.getText().toString();
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    try {
                        Date date = format.parse(str);
                        start_hour = date.getHours();
                        start_minute = date.getMinutes();
                        long curTimeInMs = date.getTime();
                        Date date2 = new Date(curTimeInMs + (dur_minutes * ONE_MINUTE_IN_MILLIS));
                        end_hour = date2.getHours();
                        end_minute = date2.getMinutes();
                    } catch ( ParseException e ) {

                    }
                }
                else if( pos == 2 ) {
                    String str = timeEnd.getText().toString();
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    try {
                        Date date = format.parse(str);
                        end_hour = date.getHours();
                        end_minute = date.getMinutes();
                        long curTimeInMs = date.getTime();
                        Date date2 = new Date(curTimeInMs - dur_minutes * ONE_MINUTE_IN_MILLIS);
                        start_hour = date2.getHours();
                        start_minute = date2.getMinutes();
                    } catch (ParseException e) {

                    }
                }
                String mode = String.valueOf( spnMode.getSelectedItemPosition() );


                int finalStart_hour = start_hour;
                int finalStart_minute = start_minute;
                int finalEnd_hour = end_hour;
                int finalEnd_minute = end_minute;
                app.getExecutors().networkIO().execute(() -> {
                    String baseUrl = "http://nas8055.synology.me:50111/rest/set_boiler?name=admin&password=citroen2020!" +
                        "&mode=" + mode + "&hour_on=" + Integer.toString(finalStart_hour) +
                        "&minute_on=" + Integer.toString(finalStart_minute) +
                        "&hour_off=" + Integer.toString(finalEnd_hour) +
                        "&minute_off=" + Integer.toString(finalEnd_minute);
                    try {
                        URL filterUrl = new URL(baseUrl);
                        HttpURLConnection connection = (HttpURLConnection) filterUrl.openConnection();
                        connection.setReadTimeout(10000); // 10 sec
                        connection.setConnectTimeout(10000); // 10 sec
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("Accept", "application/json");
                        connection.connect();
                        int statusCode = connection.getResponseCode();
                        if (statusCode != 200) {
                        }

                        InputStream is = connection.getInputStream();
                        BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
                        StringBuilder content = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            content.append(line);
                        }
                        reader.close();
                        is.close();
                        connection.disconnect();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setResult( 3 );
                            finish();
                        }
                    });
                });
            }
        });

        Button btnCancel = findViewById( R.id.btnCancelBoilerSwitch );
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchBoiler.super.onBackPressed();
            }
        });
    }

}