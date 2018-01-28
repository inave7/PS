package com.bellaku.naveenprakash.musicmaterial;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rm.com.audiowave.AudioWaveView;
import rm.com.audiowave.OnProgressListener;
import rm.com.audiowave.OnSamplingListener;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static RotateAnimation mRotateAnimation;
    private static final float ROTATE_FROM = 0.0f;
    private static final float ROTATE_TO = -10.0f * 360.0f;

    private ArrayList<Song> songList;

    private ArrayList<String> songNames, songArtists, songPaths;
    private ArrayList<Integer> songDurations;
    public static ArrayList<Long> songIds;
    private ListView songView;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public static TextView TxPlSong, TxPlArtist, TxPlDuration;
    public static FloatingActionButton fabPlaying;
    public static CardView PlayingLayout;
    public Boolean isPlaying = false;

    private static AudioWaveView waveView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        waveView = (AudioWaveView) findViewById(R.id.wave);
        final byte[] data = {1, 3, 37, 117, 69, 0, 0, 58};

        waveView.setScaledData(data);

        waveView.setRawData(data, new OnSamplingListener() {
            @Override
            public void onComplete() {

            }
        });

        waveView.setOnProgressListener(new OnProgressListener() {
            @Override
            public void onStartTracking(float progress) {

            }

            @Override
            public void onStopTracking(float progress) {

            }

            @Override
            public void onProgressChanged(float progress, boolean byUser) {

            }
        });


        PlayingLayout = (CardView) findViewById(R.id.layout_playing);

        TxPlSong = (TextView) findViewById(R.id.tx_pl_song);
        TxPlArtist = (TextView) findViewById(R.id.tx_pl_artist);
        TxPlDuration = (TextView) findViewById(R.id.tx_duration);

        fabPlaying = (FloatingActionButton) findViewById(R.id.fab);
        fabPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fabPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPlaying) {
                    isPlaying = true;
                    fabPlaying.setImageResource(android.R.drawable.ic_media_pause);
                } else {
                    isPlaying = false;
                    fabPlaying.setImageResource(R.drawable.music_material_icon);
                }
            }
        });

        mRotateAnimation = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);//0, 0, 40, 0);
        mRotateAnimation.setDuration((long) 2 * 500);
        mRotateAnimation.setRepeatCount(5);

        //    fabPlaying.setAnimation(mRotateAnimation);

        songList = new ArrayList<Song>();

        songNames = new ArrayList<String>();
        songPaths = new ArrayList<String>();
        songArtists = new ArrayList<String>();
        songDurations = new ArrayList<Integer>();

        songIds = new ArrayList<Long>();

        getSongList();

        Log.d(TAG, "Bfr Sort" + songList.get(0).getTitle());
        /*Collections.sort(songNames, new Comparator<String>() {
            public int compare(String a, String b) {
                return a.compareTo(b);
            }
        });*/
        Log.d(TAG, "Aftr Sort" + songList.get(0).getTitle());

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        recyclerView.setFocusable(true);
        recyclerView.requestFocus();
        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new MyAdapter(getApplicationContext(), songNames, songArtists, songDurations);
        recyclerView.setAdapter(mAdapter);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {


        super.onDestroy();


    }


    public void getSongList() {
        //retrieve song info


        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);


        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int pathColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);

            int durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisPath = musicCursor.getString(pathColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                int thisDuration = Integer.valueOf(musicCursor.getString(durationColumn));
                songList.add(new Song(thisId, thisTitle, thisPath, thisArtist));

                songIds.add(thisId);
                songPaths.add(thisPath);
                songNames.add(thisTitle);
                songArtists.add(thisArtist);
                songDurations.add(thisDuration);

            }
            while (musicCursor.moveToNext());
        }

    }


    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private final Context appContext;
        public List<String> songNameValues, songArtistValues;
        private List<Integer> songDurationValues;
        private MainActivity mainActivity;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView txtHeader, txtFooter, txtDuration;
            public View layout;

            public ViewHolder(View v) {
                super(v);
                layout = v;
                txtHeader = (TextView) v.findViewById(R.id.firstLine);
                txtFooter = (TextView) v.findViewById(R.id.secondLine);
                txtDuration = (TextView) v.findViewById(R.id.tx_duration);

            }
        }

    /*public void add(int position, String item) {
        songNameValues.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }*/

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Context context, List<String> mySongs, List<String> myArtists, List<Integer> myDurations) {
            songNameValues = mySongs;
            songArtistValues = myArtists;
            songDurationValues = myDurations;
            appContext = context;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {

            mainActivity = new MainActivity();

            // create a new view
            LayoutInflater inflater = LayoutInflater.from(
                    parent.getContext());
            View v =
                    inflater.inflate(R.layout.row_layout, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final String name = songNameValues.get(position);
            final String artist = songArtistValues.get(position);
            final int duration = songDurationValues.get(position);
            holder.txtHeader.setText(name);

            holder.txtFooter.setText(artist);

            //    holder.txtDuration.setText(duration);

            holder.layout.setOnClickListener(new View.OnClickListener() {
                public int index;

                @Override
                public void onClick(View view) {


                    for (int i = 0; i < songNameValues.size(); i++) {
                        if (holder.txtHeader.getText().equals(songNameValues.get(i).toString())) {
                            index = i;
                        }

                    }

                    Intent intent = new Intent(MainActivity.this, MService.class);
                    intent.putExtra("p", songPaths.get(index));
                    intent.putExtra("t", duration);

                    if (isMyServiceRunning(MService.class)) {
                        stopService(intent);
                    }
                    startService(intent);


                    mainActivity.PlayingLayout.setVisibility(View.VISIBLE);
                    mainActivity.TxPlSong.setText(holder.txtHeader.getText());
                    mainActivity.TxPlArtist.setText(holder.txtFooter.getText());

                    final float ROTATE_FROM = 0.0f;
                    final float ROTATE_TO = -10.0f * 360.0f;
                    RotateAnimation mRotateAnimation = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);//0, 0, 40, 0);
                    mRotateAnimation.setDuration((long) 2 * 500);
                    mRotateAnimation.setRepeatCount(5);

                    for (int i = 0; i < duration; i++) {

                        mainActivity.fabPlaying.setAnimation(mRotateAnimation);
                    }
                }
            });
        }

        private void makeSnack(String s) {
            Toast.makeText(appContext, s, Toast.LENGTH_SHORT).show();
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return songNameValues.size();
        }

    }


    public static class MService extends Service {


        private static final String TAG = "MService";
        private String sPath;
        private MediaPlayer mediaPlayer;

        @Override
        public void onCreate() {
            Log.i(TAG, "Service onCreate");
        }


        @Override
        public int onStartCommand(final Intent intent, int flags, int startId) {

            Log.i(TAG, "Service onStartCommand");

            Integer endTime = 5;
            if (intent != null) {
                if (intent.getExtras().get("p") != null) {
                    Log.i(TAG, "Service intent extra - " + intent.getExtras().get("p"));
                    sPath = (String) intent.getExtras().get("p");
                }
                if (intent.getExtras().get("t") != null)
                    endTime = (Integer) intent.getExtras().get("t");
            }

            final int currentId = startId;


            final Integer finalEndTime = endTime;
            final Long playTime = System.currentTimeMillis() + finalEndTime;
            Runnable r = new Runnable() {
                public void run() {

                    mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(sPath));
                    mediaPlayer.start();


                }
            };

            Thread t = new Thread(r);
            t.start();
            return Service.START_NOT_STICKY;
        }

        @Override
        public IBinder onBind(Intent arg0) {
            // TODO Auto-generated method stub
            Log.i(TAG, "Service onBind");
            return null;
        }

        @Override
        public void onDestroy() {
            Log.i(TAG, "Service onDestroy");

            mediaPlayer.stop();
            mediaPlayer.release();
        }

    }


}
