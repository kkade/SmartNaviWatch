package ch.hsr.smartnaviwatch;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import ch.hsr.transfer.MessageTypes;
import ch.hsr.transfer.NewDirectionMessage;

public class MainActivity extends Activity {

    private GoogleApiClient apiClient;
    private MessageApi.MessageListener listener;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });

        listener = new MessageApi.MessageListener() {
            @Override
            public void onMessageReceived(MessageEvent messageEvent) {
                processMessage(messageEvent);
            }
        };

        apiClient = new GoogleApiClient.Builder(getApplicationContext()).addApi(Wearable.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Wearable.MessageApi.addListener(apiClient, listener);
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        }).build();
        apiClient.connect();
    }

    private void processMessage(final MessageEvent e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String msgType = e.getPath();
                switch(msgType)
                {
                    case MessageTypes.MESSAGE_NEW_DIRECTION:
                        NewDirectionMessage msg = new NewDirectionMessage(e.getData());
                        mTextView.setText("Phone message: " + msg.getMsg());
                        break;
                }
            }
        });
    }
}
