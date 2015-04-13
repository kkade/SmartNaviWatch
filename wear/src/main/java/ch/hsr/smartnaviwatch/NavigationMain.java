package ch.hsr.smartnaviwatch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import ch.hsr.navigationmessagingapi.IMessageListener;
import ch.hsr.navigationmessagingapi.MessageEndPoint;
import ch.hsr.navigationmessagingapi.NavigationMessage;
import ch.hsr.navigationmessagingapi.services.StartupOnMessageService;

public class NavigationMain extends Activity implements IMessageListener {

    private Button buttonStartNav;
    private ViewFlipper viewFlipper;
    private Animation slide_in_left, slide_out_right;
    private TextView messageText;
    private MessageEndPoint endPoint;

    @Override
    public void messageReceived(NavigationMessage message) {
        if (message!=null) {
            final String t = message.getMessageType();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageText.setText(t);
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

        slide_in_left = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_out_right);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                messageReceived(initialMessage);

                messageText = (TextView)stub.findViewById(R.id.messageText);
                buttonStartNav = (Button)stub.findViewById(R.id.startNav);
                viewFlipper = (ViewFlipper)stub.findViewById(R.id.viewFlipper);

                viewFlipper.setInAnimation(slide_in_left);
                viewFlipper.setOutAnimation(slide_out_right);

                buttonStartNav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        viewFlipper.showNext();
                    }
                });
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
