package com.hc.testheart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements AudioManager.OnAudioFocusChangeListener {
    private RelativeLayout relativeLayout;
    private HeartView heartView;
    private ImageView imageView;
    private AudioManager mAm;
    private MediaPlayer mMediaPlayer;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMusic();
        initView();

        heartView.setShowImageListener(new HeartView.ShowImageListener() {
            @Override
            public void createImage(final float alpha) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageResource(R.drawable.ic_lock_open_yellow_24dp);
                        float change;
                        if (alpha < 22) {
                            change = alpha / 200;
                        } else if (alpha < 26) {
                            change = alpha / 100;
                        } else if (alpha < 28) {
                            change = alpha / 50;
                        } else {
                            change = alpha / 30;
                            imageView.setImageResource(R.drawable.ic_lock_close_yellow_24dp);
                        }
                        imageView.setAlpha(change);

                    }
                });

            }

            @Override
            public void drawText(final boolean isDraw, final float xLocation, final float yLocation) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isDraw) {
                            if (textView.getParent() == null) {
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.leftMargin = (int) xLocation;
                                params.topMargin = (int) yLocation;

                                relativeLayout.addView(textView, params);
                            }

                        } else {
                            relativeLayout.removeView(textView);
                        }

                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }

        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
            }

        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            mAm.abandonAudioFocus(this);
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
        }
    }

    private void initMusic() {
        mAm = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMediaPlayer = MediaPlayer.create(this, R.raw.alice);
        mMediaPlayer.setLooping(true);

        int result = mAm.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mMediaPlayer.start();
        }

    }

    private void initView() {
        relativeLayout = (RelativeLayout) findViewById(R.id.frameLayout);
        heartView = (HeartView) findViewById(R.id.surfaceView);
        imageView = (ImageView) findViewById(R.id.imageView);

        textView = new TextView(this);
        textView.setTextColor(Color.RED);
        textView.setTextSize(getResources().getDisplayMetrics().density * 12);
        textView.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        textView.setText("到站，下车了");
    }
}



