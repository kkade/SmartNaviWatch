package ch.hsr.smartnaviwatch;

// https://kennethmascarenhas.wordpress.com/2014/08/19/developing-for-android-wear-with-emulators/
// ?utm_source=Android%20Weekly&utm_campaign=a97f04efe2-Android_Weekly_116&utm_medium=email&utm_term=0_4eb677ad19-a97f04efe2-337259209

// http://stackoverflow.com/questions/25205888/pairing-android-emulator-with-wear-emulator

// telnet localhost <Emulatorport>
// redir add tcp:5601:5601
// DANACH über android wear app connecten -> es können sowohl für uhr als auch für mobile nun
// die emulatoren benutzt werden. Damit ist einfaches debuggen möglich.

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import ch.hsr.smartnaviwatch.TeletextJsonObject_sources.TeletextJsonObject;
import ch.hsr.transfer.*;

public class MainActivity extends Activity {
    private GoogleApiClient apiClient;
    private EditText editField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiClient = new GoogleApiClient.Builder(getApplicationContext()).addApi(Wearable.API).build();
        apiClient.connect();

        setContentView(R.layout.activity_main);

        editField=(EditText)this.findViewById(R.id.textToSend);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendHello(View v)
    {
        Wearable.NodeApi.getConnectedNodes(apiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                NewDirectionMessage direction = new NewDirectionMessage();
                direction.setMsg("\"" + editField.getText().toString()+"\"");
                Wearable.MessageApi.sendMessage(apiClient, getConnectedNodesResult.getNodes().get(0).getId(), MessageTypes.MESSAGE_NEW_DIRECTION , direction.getBytes());
            }
        });

    }

    public void sendTeletextAsync(View v){
        new HttpAsyncTask().execute("https://www.kimonolabs.com/api/9dkfliuo?apikey=ZvwdICqVdBJuMCpGO754fxrHusXPZyxc");
    }

    public void sendTeletext(TeletextJsonObject teletextObj)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(teletextObj.getResults().getCollection1());
            final byte[] teletextBytes = bos.toByteArray();

            Wearable.NodeApi.getConnectedNodes(apiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                @Override
                public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                    Wearable.MessageApi.sendMessage(apiClient, getConnectedNodesResult.getNodes().get(0).getId(), MessageTypes.MESSAGE_TELETEXT , teletextBytes);
                }
            });

        } catch (IOException e) {
            e.getCause();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    public class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return HttpClientTeletext.GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(final String result) {
             sendTeletext(new Gson().fromJson(result, TeletextJsonObject.class));
        }
    }
}
