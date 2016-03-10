package it.matteinn.soundclouddebug;

import android.media.MediaPlayer;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.ProviderTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener {

    Button buttonPlay;
    TextView tvStatus;
    MediaPlayer mediaPlayer;

    final String SOUNDCLOUD_URL = "https://soundcloud.com/kendricktrax/rich-homie-quan-bitches-ft-young-thug-digitaldrippedcom";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPlay = (Button)findViewById(R.id.buttonPlay);
        tvStatus = (TextView)findViewById(R.id.tvStatus);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPlay.setEnabled(false);
                buttonPlay.setClickable(false);
                buttonPlay.setAlpha(0.5f);

                tvStatus.append("Calling SoundCloud APIs...");

                SoundCloudAPI.getInstance().getSoundCloudTrack(SOUNDCLOUD_URL, new SoundCloudAPI.GetSoundCloudTrackListener() {
                    @Override
                    public void onSuccess(String url) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvStatus.append("\nGot streaming URL, preparing MediaPlayer...");
                            }
                        });
                        try {
                            mediaPlayer.setDataSource(url);
                            mediaPlayer.prepareAsync();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onBadTrack() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvStatus.append("\nTrack is no longer available");
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvStatus.append("\nFailed to query SoundCloud APIs");
                            }
                        });
                    }
                });
            }
        });

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, final int percent) {
        Log.d("MediaPlayer", "Buffering: " + percent);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvStatus.append("\nBuffering: " + percent);
            }
        });
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d("MediaPlayer", "Completion");
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d("MediaPlayer", "Error");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvStatus.append("\nMediaPlayer error");
            }
        });
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d("MediaPlayer", "Prepared");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvStatus.append("\nMediaPlayer prepared, starting...");
            }
        });

        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.start();
    }
}
