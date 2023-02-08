package at.co.shaman.smarthome12c;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static at.co.shaman.smarthome12c.RuleViewAdapter.sendPostRequest;

public class EditRule extends AppCompatActivity {

    private TextView txtTime1;
    private TextView txtTime2;
    private EditText numTemp1;
    private EditText numTemp2;
    private CheckBox chkMo, chkTu, chkWe, chkTh, chkFr, chkSa, chkSu;
    private Spinner spnAction1;
    private Spinner spnAction2;
    private Spinner spnMode;
    private TextView lblTempOpen, lblTempClose, lblTime2, lblAction2, lblTime1, lblAction1;
    private TextView txtMinus, txtTime3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.edit_rule);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.RuleMode, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spnMode = findViewById( R.id.spnRuleMode );
        spnMode.setAdapter(adapter);

        lblTempOpen = findViewById( R.id.lblRuleTempOpen );
        lblTempClose = findViewById( R.id.lblRuleTempClose );
        lblAction1 = findViewById( R.id.lblRuleAction1 );
        lblAction2 = findViewById( R.id.lblRuleAction2 );
        lblTime2 = findViewById( R.id.lblRuleTime2 );
        lblTime1 = findViewById( R.id.lblRuleTime1 );
        txtTime1 = findViewById( R.id.txtRuleTime1 );
        txtTime2 = findViewById( R.id.txtRuleTime2 );

        txtTime1.setText("");
        txtTime2.setText("");
        txtTime3 = findViewById( R.id.txtRuleTime3 );
        txtTime3.setText("");
        txtMinus = findViewById( R.id.txtRuleMinus );

        numTemp1 = findViewById( R.id.edtRuleTemp1 );
        numTemp2 = findViewById( R.id.edtRuleTemp2 );

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.RollerAction, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spnAction1 = findViewById( R.id.spnRuleAction1 );
        spnAction1.setAdapter(adapter2);

        spnAction2 = findViewById( R.id.spnRuleAction2 );
        spnAction2.setAdapter(adapter2);

        String data = getIntent().getStringExtra( "data" );

        spnMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                setVisibilities( position );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        JSONObject jObject = null;
        try {
            jObject = new JSONObject( data );

            String strDays = jObject.getString("days");
            chkMo = findViewById(R.id.chkRuleMo);
            chkMo.setChecked( strDays.contains("1"));
            chkTu = findViewById(R.id.chkRuleTu);
            chkTu.setChecked( strDays.contains("2"));
            chkWe = findViewById(R.id.chkRuleWe);
            chkWe.setChecked( strDays.contains("3"));
            chkTh = findViewById(R.id.chkRuleTh);
            chkTh.setChecked( strDays.contains("4"));
            chkFr = findViewById(R.id.chkRuleFr);
            chkFr.setChecked( strDays.contains("5"));
            chkSa = findViewById(R.id.chkRuleSa);
            chkSa.setChecked( strDays.contains("6"));
            chkSu = findViewById(R.id.chkRuleSu);
            chkSu.setChecked( strDays.contains("7"));

            String strType = jObject.getString("Type" );

            if( strType.equals( "single" ) ) {
                spnMode.setSelection( 0 );
                setVisibilities( 0 );
                spnAction1.setSelection( actionToNumber( jObject.getString( "Action") ) );
                txtTime1.setText( jObject.getString("Time") );
            }
            else if( strType.equals("pair")) {
                spnMode.setSelection( 1 );
                setVisibilities( 1 );
                spnAction1.setSelection( actionToNumber( jObject.getString( "Action1") ) );
                spnAction2.setSelection( actionToNumber( jObject.getString( "Action2") ) );
                txtTime1.setText( jObject.getString("Time1") );
                txtTime2.setText( jObject.getString("Time2") );
            }
            else if( strType.equals("temperature")) {
                spnMode.setSelection( 2 );
                setVisibilities( 2 );
                spnAction1.setSelection( actionToNumber( "open1" ) );
                spnAction2.setSelection( actionToNumber( "close1" ) );
                String times = jObject.getString("Time");
                List<String> ar_times = Arrays.asList(times.split("-"));
                txtTime1.setText( ar_times.get(0));
                txtTime3.setText( ar_times.get(1));
                numTemp1.setText( jObject.getString( "Temperature_open"));
                numTemp2.setText( jObject.getString( "Temperature_close"));

            }
        } catch (JSONException e) {
                e.printStackTrace();
        }

        txtTime1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int hour = 0;
                int minute = 0;
                try {
                    String str = txtTime1.getText().toString();
                    List<String> arTime = Arrays.asList(str.split(":"));
                    hour = Integer.parseInt(arTime.get(0));
                    minute = Integer.parseInt(arTime.get(1));
                } catch ( Exception e ) {
                    Calendar mcurrentTime = Calendar.getInstance();
                    hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    minute = mcurrentTime.get(Calendar.MINUTE);
                }
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EditRule.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        txtTime1.setText( String.format( "%02d:%02d ", selectedHour , selectedMinute ) );
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        txtTime2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int hour = 0;
                int minute = 0;
                try {
                    String str = txtTime2.getText().toString();
                    List<String> arTime = Arrays.asList(str.split(":"));
                    hour = Integer.parseInt(arTime.get(0));
                    minute = Integer.parseInt(arTime.get(1));
                } catch ( Exception e ) {
                    Calendar mcurrentTime = Calendar.getInstance();
                    hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    minute = mcurrentTime.get(Calendar.MINUTE);
                }
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EditRule.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        txtTime2.setText( String.format( "%02d:%02d ", selectedHour, selectedMinute ) );
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        txtTime3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int hour = 0;
                int minute = 0;
                try {
                    String str = txtTime3.getText().toString();
                    List<String> arTime = Arrays.asList(str.split(":"));
                    hour = Integer.parseInt(arTime.get(0));
                    minute = Integer.parseInt(arTime.get(1));
                } catch ( Exception e ) {
                    Calendar mcurrentTime = Calendar.getInstance();
                    hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    minute = mcurrentTime.get(Calendar.MINUTE);
                }
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EditRule.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        txtTime3.setText( String.format( "%02d:%02d ", selectedHour, selectedMinute ) );
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        Button btnApply = findViewById( R.id.btnRuleApply );
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jObject = new JSONObject();
                try {
                    jObject.put( "Type", spnMode.getSelectedItem().toString() );
                    jObject.put("Active", getIntent().getStringExtra( "active" ));
                    JSONArray strDays = new JSONArray();
                    if( chkMo.isChecked() ) {
                        strDays.put( 1 );
                    }
                    if( chkTu.isChecked() ) {
                        strDays.put(2);
                    }
                    if( chkWe.isChecked() ) {
                        strDays.put(3);
                    }
                    if( chkTh.isChecked() ) {
                        strDays.put(4);
                    }
                    if( chkFr.isChecked() ) {
                        strDays.put(5);
                    }
                    if( chkSa.isChecked() ) {
                        strDays.put(6);
                    }
                    if( chkSu.isChecked() ) {
                        strDays.put(7);
                    }
                    jObject.put( "days", strDays );
                    int mode = spnMode.getSelectedItemPosition();
                    boolean is_ok = true;
                    boolean is_ok_time = true;
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                    if( mode == 0 ) {
                        jObject.put( "Time", txtTime1.getText() );
                        jObject.put( "Action", spnAction1.getSelectedItem().toString() );
                        Date date = sdf.parse(txtTime1.getText().toString());
                        int minute1 = date.getMinutes();
                        is_ok_time = ( ( minute1 % 5 ) == 0 );
                    } else if( mode == 1 ) {
                        jObject.put( "Time1", txtTime1.getText() );
                        jObject.put( "Time2", txtTime2.getText() );
                        jObject.put( "Action1", spnAction1.getSelectedItem().toString() );
                        jObject.put( "Action2", spnAction2.getSelectedItem().toString() );
                        is_ok = ( txtTime2.length() != 0 );
                        if( is_ok ) {
                            Date date = sdf.parse(txtTime1.getText().toString());
                            int minute1 = date.getMinutes();
                            Date date2 = sdf.parse(txtTime2.getText().toString());
                            int minute2 = date2.getMinutes();
                            is_ok_time = ( ( minute1 % 5 ) == 0 ) && ( ( minute2 % 5 ) == 0 );
                        }
                    } else {
                        String times = txtTime1.getText() + "-" + txtTime3.getText();
                        jObject.put( "Time", times );
                        jObject.put( "Temperature_open", numTemp1.getText() );
                        jObject.put( "Temperature_close", numTemp2.getText() );
                        is_ok = ( txtTime3.length() != 0 ) && ( numTemp1.length() != 0 ) && ( numTemp2.length() != 0 );
                        if( is_ok ) {
                            Date date = sdf.parse(txtTime1.getText().toString());
                            int minute1 = date.getMinutes();
                            Date date2 = sdf.parse(txtTime3.getText().toString());
                            int minute2 = date2.getMinutes();
                            is_ok_time = ( ( minute1 % 5 ) == 0 ) && ( ( minute2 % 5 ) == 0 );
                        }
                    }
                    if( !is_ok ) {
                        Context context = getApplicationContext();
                        CharSequence text = "Wrong parameters!";
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    } else if( !is_ok_time ) {
                        Context context = getApplicationContext();
                        CharSequence text = "Wrong minutes!";
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    } else {
                        SmarthomeApp app = (SmarthomeApp) getApplicationContext();
                        app.getExecutors().networkIO().execute(() -> {
                            String name = getIntent().getStringExtra( "name" );
                            String position = getIntent().getStringExtra( "position" );
                            Integer pos_int = Integer.valueOf( position );
                            String baseUrl;
                            if( pos_int >= 0 ) {
                                baseUrl = "http://nas8055.synology.me:50111/rest/update_rule/" + name + "/" + position;
                            }
                            else {
                                baseUrl = "http://nas8055.synology.me:50111/rest/add_rule/" + name;
                            }
                            sendPostRequest(baseUrl, jObject.toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run()  {
                                    setResult( 2 );
                                    getIntent().putExtra("name", name);
                                    finish();
                                }
                            });
                        });
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }

            }
        });

        Button btnRemove = findViewById( R.id.btnRuleRemove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = getIntent().getStringExtra("name");
                String position = getIntent().getStringExtra("position");
                Integer pos_int = Integer.valueOf(position);
                if (pos_int < 0) {
                    EditRule.super.onBackPressed();
                } else {
                    String baseUrl = "http://nas8055.synology.me:50111/rest/remove_rule/" + name + "/" + position;
                    SmarthomeApp app = (SmarthomeApp) getApplicationContext();
                    app.getExecutors().networkIO().execute(() -> {

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
                            connection.disconnect();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setResult( 2 );
                                finish();
                            }
                        });
                    });
                }
            }
        });

    }

    private int actionToNumber( String txt ) {
        int num = 0;
        if (txt.equals("open")) {
            num = 0;
        } else if (txt.equals("close")) {
            num = 1;
        } else if (txt.equals("open1")) {
            num = 2;
        } else {
            num = 3;
        }
        return num;
    }

    private void setVisibilities( int pos ) {
        if( pos == 0) {
            spnAction1.setEnabled( true );
            spnAction1.setEnabled( true );
            lblTime1.setText( "Time" );
            lblAction1.setText( "Action" );
            spnAction2.setVisibility( Spinner.INVISIBLE );
            txtTime2.setVisibility( TextView.INVISIBLE );
            numTemp1.setVisibility( EditText.INVISIBLE );
            numTemp2.setVisibility( EditText.INVISIBLE );
            lblAction2.setVisibility( TextView.INVISIBLE );
            lblTime2.setVisibility( TextView.INVISIBLE );
            lblTempOpen.setVisibility( TextView.INVISIBLE );
            lblTempClose.setVisibility( TextView.INVISIBLE );
            txtTime3.setVisibility( TextView.INVISIBLE );
            txtMinus.setVisibility( TextView.INVISIBLE );
        }
        else if( pos == 1 ) {
            spnAction1.setEnabled( true );
            spnAction2.setEnabled( true );
            lblTime1.setText( "Time1" );
            lblAction1.setText( "Action1" );
            spnAction2.setVisibility( Spinner.VISIBLE );
            txtTime2.setVisibility( TextView.VISIBLE );
            numTemp1.setVisibility( EditText.INVISIBLE );
            numTemp2.setVisibility( EditText.INVISIBLE );
            lblAction2.setVisibility( TextView.VISIBLE );
            lblTime2.setVisibility( TextView.VISIBLE );
            lblTempOpen.setVisibility( TextView.INVISIBLE );
            lblTempClose.setVisibility( TextView.INVISIBLE );
            txtTime3.setVisibility( TextView.INVISIBLE );
            txtMinus.setVisibility( TextView.INVISIBLE );
        }
        else if( pos == 2 ) {
            spnAction1.setEnabled( false );
            spnAction2.setEnabled( false );
            lblTime1.setText( "Time" );
            lblAction1.setText( "Action1" );
            spnAction2.setVisibility( Spinner.VISIBLE );
            txtTime2.setVisibility( TextView.INVISIBLE );
            numTemp1.setVisibility( EditText.VISIBLE );
            numTemp2.setVisibility( EditText.VISIBLE );
            lblAction2.setVisibility( TextView.VISIBLE );
            lblTime2.setVisibility( TextView.INVISIBLE );
            lblTempOpen.setVisibility( TextView.VISIBLE );
            lblTempClose.setVisibility( TextView.VISIBLE );
            txtTime3.setVisibility( TextView.VISIBLE );
            txtMinus.setVisibility( TextView.VISIBLE );
            spnAction1.setSelection( 2 );
            spnAction2.setSelection( 3 );
        }
    }
}
