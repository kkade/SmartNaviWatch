package ch.hsr.smartnaviwatch;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import ch.hsr.navigationmessagingapi.MessageEndPoint;
import ch.hsr.navigationmessagingapi.NavigationMessage;


public class DisplayInstallTips extends ActionBarActivity {
    private MessageEndPoint endPoint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_install_tips);
        endPoint = new MessageEndPoint(getApplicationContext());
    }

    public void sendTestMessage(View view) {
        NavigationMessage msg = NavigationMessage.create("/navigation/turn/mute", new Integer(3));

        endPoint.sendMessage(msg);
    }

    public void startOsmand(View view) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("net.osmand.plus");
        startActivity(launchIntent);
    }
}
