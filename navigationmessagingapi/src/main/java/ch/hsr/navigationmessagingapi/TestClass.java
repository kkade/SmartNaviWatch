package ch.hsr.navigationmessagingapi;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

public class TestClass {

    public TestClass(Context c) {
        new GoogleApiClient.Builder(c).addApi(Wearable.API).build();
    }
}
