package ch.hsr.smartnaviwatch;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.HashMap;

import ch.hsr.navigationmessagingapi.IConnectionStateChanged;
import ch.hsr.navigationmessagingapi.IMessageListener;
import ch.hsr.navigationmessagingapi.MapPolygonCollection;
import ch.hsr.navigationmessagingapi.MessageDataKeys;
import ch.hsr.navigationmessagingapi.MessageEndPoint;
import ch.hsr.navigationmessagingapi.NavigationMessage;
import ch.hsr.navigationmessagingapi.MessageTypes;
import ch.hsr.navigationmessagingapi.services.StartupOnMessageService;

public class NavigationMain extends Activity implements IMessageListener {

    private ViewFlipper viewFlipper;
    private ImageView directionImage;
    private Animation slide_in_left, slide_out_right;
    private TextView currentPosition;
    private TextView currentNavPosition;
    private TextView directionMessage;
    private TextView distance;
    private TextView progressIndicator;
    private GridLayout layoutMap;
    private MessageEndPoint endPoint;
    private DisplayMetrics displayMetrics;

    @Override
    public void messageReceived(final NavigationMessage message) {
        if (message != null) {
            switch (message.getMessageType()) {
                case MessageTypes.NewRouteMessage:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (viewFlipper.getCurrentView().getId() != R.id.second) {
                                viewFlipper.setDisplayedChild(1);
                            }

                            HashMap<String, Object> values = (HashMap<String, Object>) message.getPayload();
                            setBackgroundMap((MapPolygonCollection) values.get(MessageDataKeys.MapPolygonData));
                            setProgressBar((double) values.get(MessageDataKeys.RouteProgressPercentage));
                            setDirectionImage((String) values.get(MessageDataKeys.TurnType));
                            //currentNavPosition.setText((String) values.get(MessageDataKeys.LocationName));
                            directionMessage.setText((String) values.get(MessageDataKeys.StreetName));
                            distance.setText(Integer.toString((int)values.get(MessageDataKeys.Distance)) + " m");
                        }
                    });
                    break;

                case MessageTypes.NextStepMessage:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (viewFlipper.getCurrentView().getId() != R.id.second) {
                                viewFlipper.setDisplayedChild(1);
                            }

                            HashMap<String, Object> values = (HashMap<String, Object>) message.getPayload();
                            setBackgroundMap((MapPolygonCollection) values.get(MessageDataKeys.MapPolygonData));
                            setProgressBar((double) values.get(MessageDataKeys.RouteProgressPercentage));
                            setDirectionImage((String) values.get(MessageDataKeys.TurnType));
                            //currentNavPosition.setText((String) values.get(MessageDataKeys.LocationName));
                            directionMessage.setText((String) values.get(MessageDataKeys.StreetName));
                            distance.setText(Integer.toString((int)values.get(MessageDataKeys.Distance)) + " m");
                        }
                    });
                    break;

                case MessageTypes.PositionMessage:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (viewFlipper.getCurrentView().getId() != R.id.first) {
                                viewFlipper.setDisplayedChild(0);
                            }

                            HashMap<String, Object> values = (HashMap<String, Object>) message.getPayload();

                            currentPosition.setText((String) values.get(MessageDataKeys.LocationName));
                            setBackgroundMap((MapPolygonCollection) values.get(MessageDataKeys.MapPolygonData));
                            setProgressBar(0);
                        }
                    });
                    break;

                case MessageTypes.CancellationMessage:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (viewFlipper.getCurrentView().getId() != R.id.first) {
                                viewFlipper.setDisplayedChild(0);
                            }

                            viewFlipper.setBackgroundColor(Color.GRAY);
                            setProgressBar(0);
                        }
                    });
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

        Display display = getWindowManager().getDefaultDisplay();
        displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                currentPosition = (TextView) stub.findViewById(R.id.currentPosition);
                currentNavPosition = (TextView) stub.findViewById(R.id.currentNavPosition);
                directionMessage = (TextView) stub.findViewById(R.id.directionMessage);
                distance = (TextView) stub.findViewById(R.id.distance);
                progressIndicator = (TextView) stub.findViewById(R.id.progressIndicator);
                viewFlipper = (ViewFlipper) stub.findViewById(R.id.viewFlipper);
                directionImage = (ImageView) stub.findViewById(R.id.directionImage);

                viewFlipper.setInAnimation(slide_in_left);
                viewFlipper.setOutAnimation(slide_out_right);

                if (initialMessage == null) {
                    endPoint.addConnectionListener(new IConnectionStateChanged() {
                        @Override
                        public void onConnectionChanged(MessageEndPoint endPoint) {
                            sendTestMessage();
                        }
                    });
                }
                else
                {
                    messageReceived(initialMessage);
                }
            }
        });
        endPoint = new MessageEndPoint(getApplicationContext());
        endPoint.addMessageListener(this);

        //Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //long[] vibrationPattern = {0, 500, 50, 300};
        //-1 - don't repeat
        //final int indexInPatternToRepeat = -1;
        //vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);

    }

    public void sendTestMessage() {
        NavigationMessage msg = NavigationMessage.create(MessageTypes.PositionRequest, new Integer(1));
        endPoint.sendMessage(msg);
    }

    private void setBackgroundMap(MapPolygonCollection mapData) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        viewFlipper.setBackground(new BitmapDrawable(getResources(), MapRenderer.render(mapData, size.x, size.y)));
    }

    private void setProgressBar(double progressInPrecentage) {
        progressIndicator.setHeight(displayMetrics.heightPixels / 100 * (int)Math.round(progressInPrecentage));
    }

    private void setDirectionImage(String directionType) {
        if (directionType.equals("C"))
            directionImage.setImageResource(R.mipmap.straight_on);
        else if (directionType.equals("TL"))
            directionImage.setImageResource(R.mipmap.turn_left);
        else if (directionType.equals("TSLL"))
            directionImage.setImageResource(R.mipmap.turn_left_slightly);
        else if (directionType.equals("TSHL"))
            directionImage.setImageResource(R.mipmap.turn_left_hard);
        else if (directionType.equals("TR"))
            directionImage.setImageResource(R.mipmap.turn_right);
        else if (directionType.equals("TSLR"))
            directionImage.setImageResource(R.mipmap.turn_right_slightly);
        else if (directionType.equals("TSHR"))
            directionImage.setImageResource(R.mipmap.turn_right_hard);
        else if (directionType.equals("KL"))
            directionImage.setImageResource(R.mipmap.turn_left_slightly);
        else if (directionType.equals("KR"))
            directionImage.setImageResource(R.mipmap.turn_right_slightly);
        else if (directionType.equals("TU"))
            directionImage.setImageResource(R.mipmap.u_turn);
        else if (directionType.equals("TRU"))
            directionImage.setImageResource(R.mipmap.u_turn);
        else if (directionType.equals("OFFR"))
            directionImage.setImageResource(R.mipmap.off_route);
        else if (directionType.startsWith("RNDB"))
            directionImage.setImageResource(R.mipmap.roundabout);
        else
            directionImage.setImageResource(android.R.color.transparent);
    }
}
