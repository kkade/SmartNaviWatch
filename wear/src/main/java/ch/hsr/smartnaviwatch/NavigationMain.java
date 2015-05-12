package ch.hsr.smartnaviwatch;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    private TextView directionMessage;
    private TextView leftTime;
    private MessageEndPoint endPoint;
    private DisplayMetrics displayMetrics;
    private ProgressBar progressBar;
    private TextView loadingMessage;
    private ImageView locationMarkerSmall;
    private ImageView locationMarkerBig;

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
                            setDirectionImage((String) values.get(MessageDataKeys.TurnType));
                            directionMessage.setText((String) values.get(MessageDataKeys.RoutingDescription));
                            leftTime.setText(intToStringTimeFormat((int) values.get(MessageDataKeys.RouteLeftTime)));
                            leftTime.setVisibility(View.VISIBLE);

                            float accuracy = (float)values.get(MessageDataKeys.LocationAccuracy);
                            if (accuracy <= 20) {
                                locationMarkerSmall.setVisibility(View.VISIBLE);
                                locationMarkerBig.setVisibility(View.INVISIBLE);
                            } else {
                                locationMarkerSmall.setVisibility(View.INVISIBLE);
                                locationMarkerBig.setVisibility(View.VISIBLE);
                            }

                            Vibrate(new long[]{0, 300, 50, 300});
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
                            setDirectionImage((String) values.get(MessageDataKeys.TurnType));
                            directionMessage.setText((String) values.get(MessageDataKeys.RoutingDescription));
                            leftTime.setText(intToStringTimeFormat ((int) values.get(MessageDataKeys.RouteLeftTime)));
                            leftTime.setVisibility(View.VISIBLE);

                            float accuracy = (float)values.get(MessageDataKeys.LocationAccuracy);
                            if (accuracy <= 20) {
                                locationMarkerSmall.setVisibility(View.VISIBLE);
                                locationMarkerBig.setVisibility(View.INVISIBLE);
                            } else {
                                locationMarkerSmall.setVisibility(View.INVISIBLE);
                                locationMarkerBig.setVisibility(View.VISIBLE);
                            }

                            Vibrate(new long[]{0, 300});
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
                            leftTime.setVisibility(View.INVISIBLE);

                            float accuracy = (float)values.get(MessageDataKeys.LocationAccuracy);
                            if (accuracy <= 20) {
                                locationMarkerSmall.setVisibility(View.VISIBLE);
                                locationMarkerBig.setVisibility(View.INVISIBLE);
                            } else {
                                locationMarkerSmall.setVisibility(View.INVISIBLE);
                                locationMarkerBig.setVisibility(View.VISIBLE);
                            }
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
                        }
                    });
                    break;
                default:
            }

            progressBar.setVisibility(View.GONE);
            loadingMessage.setVisibility(View.GONE);
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
                directionMessage = (TextView) stub.findViewById(R.id.directionMessage);
                viewFlipper = (ViewFlipper) stub.findViewById(R.id.viewFlipper);
                directionImage = (ImageView) stub.findViewById(R.id.directionImage);
                progressBar = (ProgressBar) stub.findViewById(R.id.progressBar);
                leftTime = (TextView) stub.findViewById(R.id.leftTime);
                locationMarkerSmall = (ImageView)stub.findViewById(R.id.locationMarkerSmall);
                locationMarkerBig = (ImageView)stub.findViewById(R.id.locationMarkerBig);
                loadingMessage = (TextView)stub.findViewById(R.id.loadingMessage);

                progressBar.setVisibility(View.VISIBLE);
                loadingMessage.setVisibility(View.VISIBLE);

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
    }

    public void sendTestMessage() {
        NavigationMessage msg = NavigationMessage.create(MessageTypes.PositionRequest, new Integer(1));
        endPoint.sendMessage(msg);
    }

    public void handleCard(View v){
        if (v.getBackground().getAlpha() < 255) {
            v.getBackground().setAlpha(255);
        } else {
            v.getBackground().setAlpha(100);
        }
    }

    private void Vibrate(long[] vibrationPattern) {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //-1 - don't repeat
        final int indexInPatternToRepeat = -1;
        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
    }

    private void setBackgroundMap(MapPolygonCollection mapData) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        viewFlipper.setBackground(new BitmapDrawable(getResources(), MapRenderer.render(mapData, size.x, size.y)));
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

    public String intToStringTimeFormat(int time)
    {
        String strTemp = new String();
        int minutes = time / 60;
        int hours = 0;

        if (minutes >= 60){
            hours = minutes / 60;
            minutes = minutes - (hours * 60);
        }

        strTemp = "~";

        if(hours > 0)
            strTemp = strTemp + Integer.toString(hours) + " h ";

        if (minutes > 0)
            strTemp = strTemp + Integer.toString(minutes) + " min";

        return strTemp;
    }
}
