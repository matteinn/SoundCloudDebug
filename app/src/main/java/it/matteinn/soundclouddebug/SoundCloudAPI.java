package it.matteinn.soundclouddebug;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.client.params.ClientPNames;

/**
 * Created by matteo on 10/03/16.
 */
public class SoundCloudAPI {

    private final String SOUNDCLOUD_CLIENT_ID = "de6b9c28f7eb1f2bc53d27872d0e9225";

    private static SoundCloudAPI instance;

    public static SoundCloudAPI getInstance(){
        if(instance == null){
            instance = new SoundCloudAPI();
        }
        return instance;
    }

    private AsyncHttpClient client;

    private SoundCloudAPI(){
        super();
        this.client = new AsyncHttpClient();
        this.client.setTimeout(15000);
    }

    public interface GetSoundCloudTrackListener{
        void onSuccess(String url);
        void onBadTrack();
        void onFailure();
    }

    public void getSoundCloudTrack(String soundCloudURL, final GetSoundCloudTrackListener listener){

        String url = "https://api.soundcloud.com/resolve.json?url=" + soundCloudURL + "&client_id=" + SOUNDCLOUD_CLIENT_ID;
        Log.d("SoundCloudAPI", url);

        client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                String response = (responseBody == null) ? "" : new String(responseBody);
                Log.d("SoundCloudAPI", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String trackId = jsonObject.optString("id");
                    String streamingUrl = "https://api.soundcloud.com/tracks/" + trackId + "/stream?client_id=" + SOUNDCLOUD_CLIENT_ID;
                    listener.onSuccess(streamingUrl);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                listener.onFailure();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                String response = (responseBody == null) ? "" : new String(responseBody);
                Log.e("SoundCloudAPI", response);
                if (statusCode == 404) {
                    listener.onBadTrack();
                } else {
                    listener.onFailure();
                }
            }

        });
    }

}
