package org.ubilabs.ubichess.control;


import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class VoiceHint {
    private MediaPlayer mediaPlayer;
    private Context context;

    public VoiceHint(Context context){
        this.context = context;
    }

    public void playVoice(int resourceID) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            Log.e("TAG", "Release media player!");
        }
        mediaPlayer = MediaPlayer.create(context, resourceID);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.release();
            }
        });
        mediaPlayer.start();
    }
}
