package at.co.shaman.smarthome12c;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class RuleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> _desc;
    private List<Boolean> _active;
    private String _rules;
    private String _name;
    private SmarthomeApp _app;
    private AppCompatActivity _parent;

    public RuleViewAdapter() {
        _desc = new ArrayList<String>();
        _active = new ArrayList<Boolean>();

    }

    public static String sendPostRequest(String requestUrl, String payload) {
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(payload);
            writer.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer jsonString = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            br.close();
            connection.disconnect();
            return jsonString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }
    public void setRules(String answer, String name, SmarthomeApp app, AppCompatActivity parent )
    {
        _app = app;
        _rules = new String( answer );
        _name = new String( name );
        _parent = parent;
        try {
            JSONArray jArray = new JSONArray( answer );
            for( int iRule = 0; iRule < jArray.length(); iRule++ )
            {
                JSONObject next = jArray.getJSONObject(iRule);
                Boolean active = ( next.getString("Active").equals("1") );
                _active.add( active );
                String tp = next.getString( "Type" );
                String desc = "Type: " + tp + "\n";
                if( tp.equals("single")) {
                    desc += "Time: " + next.getString("Time" ) + ", ";
                    desc += "Action: " + next.getString("Action") + "\n";
                }
                else if( tp.equals("pair")) {
                    desc += "Time1: " + next.getString("Time1" ) + ", ";
                    desc += "Action1: " + next.getString("Action1") + "\n";
                    desc += "Time2: " + next.getString("Time2" ) + ", ";
                    desc += "Action2: " + next.getString("Action2") + "\n";
                }
                else if( tp.equals("temperature")) {
                    desc += "Time: " + next.getString("Time" ) + "\n";
                    desc += "Temperature open: " + next.getString("Temperature_open") + ", ";
                    desc += "close: " + next.getString("Temperature_close" ) + "\n";
                }
                desc += "Days: " + next.getString( "days" );
                _desc.add( desc );
            }
        } catch (JSONException e) {
            // Oops
        }

    }

    @NonNull
    @Override
    public RuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.roller_rule, parent, false);
        RuleViewHolder holder1 = new RuleViewHolder( view );

        return holder1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RuleViewHolder holder1 = (RuleViewHolder)(holder);
        TextView txtDesc = holder1.getDesc();
        Switch swActive = holder1.getSwitch();
        txtDesc.setText( _desc.get(position) );
        swActive.setChecked( _active.get( position ) );

        swActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    JSONArray jArray = new JSONArray( _rules );
                    JSONObject next = jArray.getJSONObject(position);
                    String str = "0";
                    if( swActive.isChecked() ) {
                        str = "1";
                    }
                    next.put( "Active", str );
                    jArray.put( position, next );

                    _app.getExecutors().networkIO().execute(() -> {
                        Integer pos2 = position;
                        String baseUrl = "http://nas8055.synology.me:50111/rest/update_rule/" + _name + "/" + pos2.toString();
                        sendPostRequest( baseUrl, next.toString() );
                    });

                } catch (JSONException e) {
                    // Oops
                }
            }
        });

        txtDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RollerRules parent = (RollerRules)(_parent);
                Intent intent = new Intent(parent, EditRule.class);
                JSONArray jArray = null;
                JSONObject next = null;
                try {
                    jArray = new JSONArray( _rules );
                    next = jArray.getJSONObject(position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                intent.putExtra("data", next.toString() );
                intent.putExtra( "position", String.valueOf( position ) );
                intent.putExtra( "name", _name );
                String strActive = "0";
                if( _active.get( position ) ) {
                    strActive = "1";
                }
                intent.putExtra( "active", strActive );
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                parent.startActivityForResult(intent,2);
            }
        });

    }

    @Override
    public int getItemCount() {
        return _desc.size();

    }

    static class RuleViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtDesc;
        private final Switch swActive;
        public RuleViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDesc = itemView.findViewById(R.id.txtRuleDescription);
            swActive = itemView.findViewById(R.id.swRuleActive);
        }

        public TextView getDesc() {
            return txtDesc;
        }

        public Switch getSwitch() {
            return swActive;
        }
    }
}
