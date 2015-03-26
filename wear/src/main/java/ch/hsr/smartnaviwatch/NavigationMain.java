package ch.hsr.smartnaviwatch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;

import ch.hsr.navigationmessagingapi.IMessageListener;
import ch.hsr.navigationmessagingapi.MessageEndPoint;
import ch.hsr.navigationmessagingapi.NavigationMessage;
import ch.hsr.navigationmessagingapi.services.StartupOnMessageService;

public class NavigationMain extends Activity implements IMessageListener {

    private TextView mTextView;
    private MessageEndPoint endPoint;

    @Override
    public void messageReceived(NavigationMessage message) {
        if (message!=null) {
            final String t = message.getMessageType();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextView.setText(t);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        final NavigationMessage initialMessage = StartupOnMessageService.getMessageFromIntent(getIntent());
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                messageReceived(initialMessage);
            }
        });
        endPoint=new MessageEndPoint(getApplicationContext());
        endPoint.addMessageListener(this);


        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] vibrationPattern = {0, 500, 50, 300};
        //-1 - don't repeat
        final int indexInPatternToRepeat = -1;
        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
    }

    public void sendTestMessage(View view) {
        NavigationMessage msg = NavigationMessage.create("/yeah/backwards/msg", new Integer(3));

        endPoint.sendMessage(msg);
    }
}
