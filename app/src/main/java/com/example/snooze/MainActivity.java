package com.example.snooze;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnFocusChangeListener {
	private SeekBar backSeekBar;
	private EditText backEditText;
	private SeekBar seatSeekBar;
	private EditText seatEditText;
	private SeekBar feetSeekBar;
	private EditText feetEditText;
	private int currentBackAngle;
	private int currentseatAngle;
	private int currentfeatAngle;
	private int currentLightseat;
	private int currentLightcapsule;
	private boolean saveOnOff = false;
	public int Red;
	public int Green;
	public int Blue;
	private ImageView setColor;
	private ImageView setColor2;
	private EditText redEditText;
	private EditText greenEditText;
	private EditText blueEditText;

	private ArrayList<FavoritePosition> favoritePositions = new ArrayList<>();
	private ArrayList<Integer> colors = new ArrayList<Integer>();
	FavoritePosition number1 = new FavoritePosition();
	FavoritePosition number2 = new FavoritePosition();
	FavoritePosition number3 = new FavoritePosition();


	private ImageView colorp;
	private View preview;


	private MediaPlayer mMediaPlayer;
	private AudioManager mAudioManager;
	boolean playing = false;

	private View decorView;


	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		backSeekBar.setProgress(backSeekBar.getProgress());
		seatSeekBar.setProgress(seatSeekBar.getProgress());
		feetSeekBar.setProgress(feetSeekBar.getProgress());
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {

	}


	private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
		@Override
		public void onAudioFocusChange(int focusChange) {
			if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
					focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
				// The AUDIOFOCUS_LOSS_TRANSIENT case means that we've lost audio focus for a
				// short amount of time. The AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK case means that
				// our app is allowed to continue playing sound but at a lower volume. We'll treat
				// both cases the same way because our app is playing short sound files.

				// Pause playback and reset player to the start of the file. That way, we can
				// play the word from the beginning when we resume playback.
				mMediaPlayer.pause();
				mMediaPlayer.seekTo(0);
			} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
				// The AUDIOFOCUS_GAIN case means we have regained focus and can resume playback.
				mMediaPlayer.start();
			} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
				// The AUDIOFOCUS_LOSS case means we've lost audio focus and
				// Stop playback and clean up resources
				releaseMediaPlayer();
			}
		}
	};


	private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mediaPlayer) {
			// Now that the sound file has finished playing, release the media player resources.
			mMediaPlayer.start();
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		colors.add(0, 55);
		colors.add(1, 0);
		colors.add(2, 0);
		decorView = getWindow().getDecorView();
		decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				if (visibility == 0) {
					decorView.setSystemUiVisibility(hideBars());
				}


			}
		});

		redEditText = findViewById(R.id.editText6);
		greenEditText = findViewById(R.id.editText8);
		blueEditText = findViewById(R.id.editText7);

		setColor = findViewById(R.id.save_pos3);
		setColor2 = findViewById(R.id.save_pos4);

		colorp = findViewById(R.id.color_picker);
		preview = findViewById(R.id.test);

		colorp.setDrawingCacheEnabled(true);
		colorp.buildDrawingCache(true);

		colorp.setOnTouchListener(new View.OnTouchListener() {

			@Override

			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
					Bitmap bitmap = colorp.getDrawingCache();

					try {
						int pixel = bitmap.getPixel((int) event.getX(), (int) event.getY());

						//R,G,
						int r = Color.red(pixel);
						int g = Color.green(pixel);
						int b = Color.blue(pixel);

						//if(r!=0 && g!=0 && b!=0){
						//get Hex value
						String hex = "#" + Integer.toHexString(pixel);
						//test color preview
						preview.setBackgroundColor(Color.rgb(r, g, b));
						//}


						/*colors.set(0,r);
						colors.set(1,g);
						colors.set(2,b);*/


					} catch (Exception e) {
						e.printStackTrace();
					}
					//} else if (event.getAction() == MotionEvent.ACTION_UP) {
					//	return false;
				}
				return true;

			}
		});

		backSeekBar = findViewById(R.id.back_seekBar);
		backEditText = findViewById(R.id.back_editText);
		backEditText.setOnFocusChangeListener(this);
		backEditText.setText("" + backSeekBar.getProgress());

		backSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

				currentBackAngle = progress;


			}


			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				backEditText.setText("" + currentBackAngle);
				System.out.println(currentBackAngle);
				String variable = String.valueOf(currentBackAngle);
				http("91", "setanglebackrest", variable);
				System.out.println("funktioniert");


			}
		});

		seatSeekBar = findViewById(R.id.seat_seekBar);
		seatEditText = findViewById(R.id.seat_editText);
		seatEditText.setOnFocusChangeListener(this);
		seatEditText.setText("" + seatSeekBar.getProgress());

		seatSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
				seatEditText.setText("" + progress);
				currentseatAngle = progress;
				System.out.println(currentseatAngle);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				String variable = String.valueOf(currentseatAngle);
				http("92", "setangleseating", variable);
				System.out.println("funktioniert");

			}

		});

		feetSeekBar = findViewById(R.id.feet_seekBar);
		feetEditText = (EditText) findViewById(R.id.feet_editText);
		feetEditText.setOnFocusChangeListener(this);
		feetEditText.setText("" + feetSeekBar.getProgress());

		feetSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
				feetEditText.setText("" + progress);
				currentfeatAngle = progress;

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				String variable = String.valueOf(currentfeatAngle);
				http("92", "setanglefootrest", variable);
				System.out.println("funktioniert");
			}
		});

		backEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				String in = backEditText.getText().toString();
				backEditText.setSelection(backEditText.getText().length());
				try {
					Integer.parseInt(backEditText.getText().toString());

					int grad = Integer.parseInt(in);
					currentBackAngle = grad;

					backSeekBar.setProgress(grad);
					if (grad < 0) {
						backEditText.setText(0);
					}
					int limit = 87;
					if (grad > 87) {
						backEditText.setText(("" + limit));
					}
				} catch (Exception e) {
					boolean usableInt = false;
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {


			}
		});
		setColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				int currentLightcapsule = ((colors.get(0)) * ((256) ^ 2) + ((colors.get(1)) * 256) + (colors.get(2)));
				String variable3 = String.valueOf(currentLightcapsule);
				http("93", "setlightinterior", variable3);

			}
		});

		setColor2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				int currentLightseat = (colors.get(0) * (256) ^ 2 + colors.get(1) * 256 + colors.get(2));
				String variable3 = String.valueOf(currentLightseat);
				http("93", "setlightseat", variable3);

			}
		});

		ImageView stopButton = findViewById(R.id.stop);

		stopButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Toast.makeText(MainActivity.this, "pressed", Toast.LENGTH_SHORT).show();

				http1("91", "setstopbackrest");
				http1("92", "setstopseating");
				http1("92", "setstopfootrest");


			}
		});

		seatEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				String in = seatEditText.getText().toString();
				seatEditText.setSelection(seatEditText.getText().length());
				try {
					Integer.parseInt(seatEditText.getText().toString());


					int grad = Integer.parseInt(in);
					currentseatAngle = grad;

					seatSeekBar.setProgress(grad);
					if (grad < 0) {
						seatEditText.setText(0);
					}
					int limit = 30;
					if (grad > 30) {
						seatEditText.setText(("" + limit));
					}
				} catch (Exception e) {
					boolean usableInt = false;
				}

			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});

		feetEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				String in = feetEditText.getText().toString();
				feetEditText.setSelection(feetEditText.getText().length());
				try {
					Integer.parseInt(feetEditText.getText().toString());


					int grad = Integer.parseInt(in);
					currentfeatAngle = grad;

					feetSeekBar.setProgress(grad);
					if (grad < 0) {
						feetEditText.setText(0);
					}
					int limit = 90;
					if (grad > 90) {
						feetEditText.setText(("" + limit));
					}
				} catch (Exception e) {
					boolean usableInt = false;
				}

			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});


		ImageView sittingPosButton = findViewById(R.id.sitting_pos_button);
		ImageView layingPosButton = findViewById(R.id.laying_pos_button);
		ImageView zeroGravityPosButton = findViewById(R.id.zerogravity_pos_button3);




			/*OkHttpClient client = new OkHttpClient();
			String url = "http://10.18.2.165:5000/setanglebackrest/8";

			Request request = new Request.Builder()
					.url(url)
					.build();

			client.newCall(request).enqueue(new Callback() {

				public void onFailure(@NotNull Call call, @NotNull IOException e) {
					e.printStackTrace();
				}

				@Override
				public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
					if (response.isSuccessful()) {
						String myResponse = response.body().string();
						System.out.println(myResponse);
					}
				}

			});


*/


		layingPosButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				backSeekBar.setProgress(0);
				seatSeekBar.setProgress(0);
				feetSeekBar.setProgress(90);
			}
		});

		sittingPosButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				backSeekBar.setProgress(87);
				seatSeekBar.setProgress(0);
				feetSeekBar.setProgress(0);
			}
		});

		zeroGravityPosButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				backSeekBar.setProgress(60);
				seatSeekBar.setProgress(25);
				feetSeekBar.setProgress(25);
			}
		});


		ImageView savePosButton = findViewById(R.id.save_pos);
		savePosButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!saveOnOff) {
					saveOnOff = true;
					Toast.makeText(MainActivity.this, "You can save your favourite Positons", Toast.LENGTH_SHORT).show();

				} else {
					saveOnOff = false;
					Toast.makeText(MainActivity.this, "You can choose your favourite Positons", Toast.LENGTH_SHORT).show();

				}

			}
		});

		ImageView posButton1 = findViewById(R.id.open_pos1);
		posButton1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (saveOnOff == true) {

					System.out.println("bin drin");

					number1.name = "Position1";
					number1.back = backEditText.getText().toString();
					number1.seat = seatEditText.getText().toString();
					number1.foot = feetEditText.getText().toString();

					Toast.makeText(MainActivity.this, "Position1 saved", Toast.LENGTH_SHORT).show();

				} else {
					//NUED Funktion -- Werte ausgeben
					Toast.makeText(MainActivity.this, "Position1 selected", Toast.LENGTH_SHORT).show();

				}
			}
		});

		ImageView posButton2 = findViewById(R.id.open_pos2);
		posButton2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (saveOnOff == true) {

					number2.name = "Position2";
					number2.back = backEditText.getText().toString();
					number2.seat = seatEditText.getText().toString();
					number2.foot = feetEditText.getText().toString();
					Toast.makeText(MainActivity.this, "Position2 saved", Toast.LENGTH_SHORT).show();

				} else {
					//NUED Funktion -- Werte ausgeben
					Toast.makeText(MainActivity.this, "Position2 selected", Toast.LENGTH_SHORT).show();

				}
			}
		});

		ImageView posButton3 = findViewById(R.id.open_pos3);
		posButton3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (saveOnOff == true) {

					number3.name = "Position3";
					number3.back = backEditText.getText().toString();
					number3.seat = seatEditText.getText().toString();
					number3.foot = feetEditText.getText().toString();
					Toast.makeText(MainActivity.this, "Position3 saved", Toast.LENGTH_SHORT).show();

				} else {
					//NUED Funktion -- Werte ausgeben
					Toast.makeText(MainActivity.this, "Position3 selected", Toast.LENGTH_SHORT).show();

				}
			}
		});


		// MeditationList
		final ArrayList<Topic> topics = new ArrayList<>();
		topics.add(new Topic("Rain", R.drawable.rain, R.raw.rain));
		topics.add(new Topic("Stream", R.drawable.stream, R.raw.stream));
		topics.add(new Topic("Birds", R.drawable.birds, R.raw.birds));
		topics.add(new Topic("Jungle", R.drawable.jungle, R.raw.jungle));
		topics.add(new Topic("Waves", R.drawable.waves_beach, R.raw.waves_beach));

		TopicAdapter adapter = new TopicAdapter(MainActivity.this, topics);
		final ListView meditationList = findViewById(R.id.meditation_list);
		meditationList.setAdapter(adapter);


		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


		final ImageButton playButton = findViewById(R.id.play_button);
		ImageButton nextButton = findViewById(R.id.next_button);
		ImageButton previousButton = findViewById(R.id.previous_button);
		final ImageView topicImageView = findViewById(R.id.topic_imagView);
		final ImageView bluetoothButton = findViewById(R.id.bluetooth_button);
		final ImageView spotifyButton = findViewById(R.id.spotify_button);


		playButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (playing) {
					mMediaPlayer.pause();
					playButton.setBackgroundResource(R.drawable.play_arrow);
					topicImageView.setVisibility(View.INVISIBLE);
					bluetoothButton.setVisibility(View.VISIBLE);
					spotifyButton.setVisibility(View.VISIBLE);
				} else {
					if (mMediaPlayer == null) {
						playButton.setBackgroundResource(R.drawable.pause);
						topicImageView.setVisibility(View.VISIBLE);
						bluetoothButton.setVisibility(View.INVISIBLE);
						spotifyButton.setVisibility(View.INVISIBLE);
						Topic topic = topics.get(0);
						topicImageView.setBackgroundResource(topic.getImageResourceId());
						int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
						// Start the audio file
						if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
							mMediaPlayer = MediaPlayer.create(MainActivity.this, topic.getAudioResourceId());
							// Start the audio file
							mMediaPlayer.start();
							// Setup a listener on the media player, so that we can stop and release the
							// media player once the sound has finished playing.
							mMediaPlayer.setOnCompletionListener(mCompletionListener);
						}

					} else {
						playButton.setBackgroundResource(R.drawable.pause);
						topicImageView.setVisibility(View.VISIBLE);
						bluetoothButton.setVisibility(View.INVISIBLE);
						spotifyButton.setVisibility(View.INVISIBLE);
						mMediaPlayer.start();
					}
				}
				playing = !playing;
			}
		});


		// Set a click listener to play the audio when the list item is clicked on
		meditationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				playButton.setBackgroundResource(R.drawable.pause);
				topicImageView.setVisibility(View.VISIBLE);
				bluetoothButton.setVisibility(View.INVISIBLE);
				spotifyButton.setVisibility(View.INVISIBLE);
				if (!playing) {
					playing = !playing;
				}
				// Release the media player if it currently exists because we are about to
				// play a different sound file
				releaseMediaPlayer();
				// Get the {@link Word} object at the given position the user clicked on
				Topic topic = topics.get(position);
				ImageView topicImageView = findViewById(R.id.topic_imagView);
				topicImageView.setImageResource(topic.getImageResourceId());
				int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
						AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
				// Start the audio file
				if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
					mMediaPlayer = MediaPlayer.create(MainActivity.this, topic.getAudioResourceId());
					// Start the audio file
					mMediaPlayer.start();
					// Setup a listener on the media player, so that we can stop and release the
					// media player once the sound has finished playing.
					mMediaPlayer.setOnCompletionListener(mCompletionListener);
				}
			}
		});


	}


	public void http(String id, String manuplate, String variable) {// das ist außer oncreate

		OkHttpClient client = new OkHttpClient();
		String url = "http://10.18.12." + id + ":5000/" + manuplate + "/" + variable;

		Request request = new Request.Builder()
				.url(url)
				.build();

		client.newCall(request).enqueue(new Callback() {

			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
				if (response.isSuccessful()) {
					String Responsehttp = response.body().string();
					System.out.println(Responsehttp);
				}
			}

		});
	}

	public void http1(String id, String manuplate) {// das ist außer oncreate

		System.out.println("geht doch");
		OkHttpClient client = new OkHttpClient();
		String url = "http://10.18.12." + id + ":5000/" + manuplate;

		Request request = new Request.Builder()
				.url(url)
				.build();

		client.newCall(request).enqueue(new Callback() {

			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
				if (response.isSuccessful()) {
					String Responsehttp91 = response.body().string();
					System.out.println(Responsehttp91);
				}
			}

		});
	}

	public void htttpthemes() {

		OkHttpClient client = new OkHttpClient();
		String url = "https://10.18.12.95:3000/api/Themes?filter=0&access_token=12345";
		Request request = new Request.Builder()
				.url(url)
				.build();

		client.newCall(request).enqueue(new Callback() {

			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
				if (response.isSuccessful()) {
					String Responsehttpthemes = response.body().string();
					System.out.println(Responsehttpthemes);
				}
			}

		});
}
	@Override
	protected void onStop() {
		super.onStop();
		// When the activity is stopped, release the media player resources because we won't
		// be playing any more sounds.
		releaseMediaPlayer();
	}

	private void releaseMediaPlayer() {
		// If the media player is not null, then it may be currently playing a sound.
		if (mMediaPlayer != null) {
			// Regardless of the current state of the media player, release its resources
			// because we no longer need it.
			mMediaPlayer.release();

			// Set the media player back to null. For our code, we've decided that
			// setting the media player to null is an easy way to tell that the media player
			// is not configured to play an audio file at the moment.
			mMediaPlayer = null;

			// Regardless of whether or not we were granted audio focus, abandon it. This also
			// unregisters the AudioFocusChangeListener so we don't get anymore callbacks.
			mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			decorView.setSystemUiVisibility(hideBars());
		}
	}

	private int hideBars() {
		return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
	}

	@Override
	public void onFocusChange(View view, boolean b) {
		switch (view.getId()){
			case R.id.back_editText:


				if(!b) {
					String variable = String.valueOf(currentBackAngle);
					http("91","setanglebackrest", variable);
				}
				break;
			case R.id.seat_editText:


				if(!b){
					String variable1 = String.valueOf(currentseatAngle);
					http("92","setanglebackrest", variable1);
				}

				break;
			case R.id.feet_editText:

				if(!b) {
					String variable2 = String.valueOf(currentfeatAngle);
					http("92","setanglebackrest", variable2);
				}
				break;
			case R.id.editText6:
				if(!b){


				}
				break;
			case R.id.editText8:
				if(!b){

				}
				break;
			case R.id.editText7:
				if(!b){

				}
				break;
		}
	}
}