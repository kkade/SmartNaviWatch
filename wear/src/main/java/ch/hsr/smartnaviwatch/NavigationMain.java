package ch.hsr.smartnaviwatch;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import ch.hsr.navigationmessagingapi.IMessageListener;
import ch.hsr.navigationmessagingapi.MapPolygonCollection;
import ch.hsr.navigationmessagingapi.MessageEndPoint;
import ch.hsr.navigationmessagingapi.NavigationMessage;
import ch.hsr.navigationmessagingapi.MessageTypes;
import ch.hsr.navigationmessagingapi.services.StartupOnMessageService;

public class NavigationMain extends Activity implements IMessageListener {

    private Button buttonStartNav;
    private ViewFlipper viewFlipper;
    private Animation slide_in_left, slide_out_right;
    private TextView currentPosition;
    private GridLayout layoutMap;
    private MessageEndPoint endPoint;

    @Override
    public void messageReceived(final NavigationMessage message) {
        if (message!=null) {
            switch (message.getMessageType()) {
                case MessageTypes.NewRouteMessage:

                    break;
                case MessageTypes.NextStepMessage:

                    break;
                case MessageTypes.PositionMessage:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           // currentPosition.setText(message.getPayload());
                            //MapRenderer.render(new MapPolygonCollection());
                        }
                    });
                    break;
                case MessageTypes.CancellationMessage:

                    break;
                default:


            }
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

                currentPosition = (TextView)stub.findViewById(R.id.currentPosition);
                //buttonStartNav = (Button)stub.findViewById(R.id.startNav);
                viewFlipper = (ViewFlipper)stub.findViewById(R.id.viewFlipper);

                viewFlipper.setInAnimation(slide_in_left);
                viewFlipper.setOutAnimation(slide_out_right);

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                viewFlipper.setBackground(new BitmapDrawable(getResources() ,MapRenderer.render(new MapPolygonCollection(), size.x, size.y)));

                //buttonStartNav.setOnClickListener(new View.OnClickListener() {
                  //  @Override
                   // public void onClick(View arg0) {
                     //   viewFlipper.showNext();
                   /// }
                //});
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
