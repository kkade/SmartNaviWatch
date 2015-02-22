package ch.hsr.smartnaviwatch;

// https://kennethmascarenhas.wordpress.com/2014/08/19/developing-for-android-wear-with-emulators/
// ?utm_source=Android%20Weekly&utm_campaign=a97f04efe2-Android_Weekly_116&utm_medium=email&utm_term=0_4eb677ad19-a97f04efe2-337259209

// http://stackoverflow.com/questions/25205888/pairing-android-emulator-with-wear-emulator

// telnet localhost <Emulatorport>
// redir add tcp:5601:5601
// DANACH über android wear app connecten -> es können sowohl für uhr als auch für mobile nun
// die emulatoren benutzt werden. Damit ist einfaches debuggen möglich.

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
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
                direction.setMsg(editField.getText().toString()+"!?!");
                Wearable.MessageApi.sendMessage(apiClient, getConnectedNodesResult.getNodes().get(0).getId(), MessageTypes.MESSAGE_NEW_DIRECTION , direction.getBytes());
            }
        });

    }
}
