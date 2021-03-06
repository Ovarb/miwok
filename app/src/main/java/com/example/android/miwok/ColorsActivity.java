package com.example.android.miwok;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ColorsActivity extends AppCompatActivity {

    final String TAG_APP = "Miwok";
    final String TAG_ACT = "ColorsActivity";
    final String TAG = TAG_APP + " " + TAG_ACT;

    /** Handles playback of all the sound files */
    private MediaPlayer mMediaPlayer;

    /** Handles audio focus when playings sound file */
    private AudioManager mAudioManager;

    /** This listener gets triggered when the {@link MediaPlayer} has completed playing the audio file */
    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            releaseMediaPlayer();
        }
    };

    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    //We have lost our audio focus and stop playback and clean up resources
                    Log.i(TAG, "audiofocus lost");
                    releaseMediaPlayer();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    //if another app interrupts our media player for short amount of time
                    //we pause the audio file and rewind it to the beginning for repeating after audio focus will be returned
                    Log.i(TAG, "audiofocus lost for a while");
                    mMediaPlayer.pause();
                    mMediaPlayer.seekTo(0);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    //if another app interrupts our media player for short amount of time
                    //we pause the audio file and rewind it to the beginning for repeating after audio focus will be returned
                    Log.i(TAG, "audiofocus lost for a while but can be ducked");
                    mMediaPlayer.pause();
                    mMediaPlayer.seekTo(0);
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    //We have regained audio focus and can resume playback
                    Log.i(TAG, "audiofocus is gained!");
                    mMediaPlayer.start();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);

        /** Create and setup the {@link AudioManager} to request audio focus */
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        final ArrayList<Word> words = new ArrayList<>();
        words.add(new Word("red", "wetetti", R.drawable.color_red, R.raw.color_red));
        words.add(new Word("green", "chokokki", R.drawable.color_green, R.raw.color_green));
        words.add(new Word("brown", "takaakki", R.drawable.color_brown, R.raw.color_brown));
        words.add(new Word("gray", "topoppi", R.drawable.color_gray, R.raw.color_gray));
        words.add(new Word("black", "kululli", R.drawable.color_black, R.raw.color_black));
        words.add(new Word("white", "kelelli", R.drawable.color_white, R.raw.color_white));
        words.add(new Word("dusty yellow", "topiisә", R.drawable.color_dusty_yellow, R.raw.color_dusty_yellow));
        words.add(new Word("mustard yellow", "chiwiitә", R.drawable.color_mustard_yellow, R.raw.color_mustard_yellow));

        // Create an {@link ArrayAdapter}, whose data source is a list of Strings. The
        // adapter knows how to create layouts for each item in the list, using the
        // simple_list_item_1.xml layout resource defined in the Android framework.
        // This list item layout contains a single {@link TextView}, which the adapter will set to
        // display a single word.
        WordAdapter adapter = new WordAdapter(this, words, R.color.category_colors);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // activity_numbers.xml layout file.
        ListView listView = (ListView) findViewById(R.id.list);

        // Make the {@link ListView} use the {@link ArrayAdapter} we created above, so that the
        // {@link ListView} will display list items for each word in the list of words.
        // Do this by calling the setAdapter method on the {@link ListView} object and pass in
        // 1 argument, which is the {@link ArrayAdapter} with the variable name itemsAdapter.
        listView.setAdapter(adapter);

        //set a click listener to play the audio when the list item is clicked on
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //DEBUG Toast message
                Toast.makeText(ColorsActivity.this, words.get(position).getDefaultTranslation(), Toast.LENGTH_SHORT).show();

                //Release the media player if it currently exists because we are about to play a different sound file
                releaseMediaPlayer();

                // Request audio focus for playback
                int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                Log.i(TAG, "audiofocus is requested");
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                    //we have audio focus now

                    //create and setup {@link MediaPlayer} for the audio resource associated with the current word
                    mMediaPlayer = MediaPlayer.create(ColorsActivity.this, words.get(position).getAudioResourceId());

                    Log.i(TAG, "audiofocus is granted");

                    //Start the audio file
                    mMediaPlayer.start();

                    //DEBUG logging
                    Log.i(TAG, "audiofile is playing" + words.get(position).toString());

                    //Setup a listener on the media player, so that we can stop and release the media player
                    // once the sound has finished playing
                    mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        releaseMediaPlayer();
        super.onStop();
    }

    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer() {

        Log.i(TAG, "releaseMediaPlayer() is evoked");

        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();

            Log.i(TAG, "mMediaPlayer is released");

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;
        }

        //Regardless of whether or not we were granted audio focus, abandon it. This also unregisters
        //the OnAudioFocusChangeListener so we don't get anymore callbacks.
        Log.i(TAG, "audiofocus is abandon");
        mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
    }
}
