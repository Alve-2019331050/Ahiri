package ahiri.controllers;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MediaBarController implements Initializable {
	
	@FXML
	private Button previousButton,playButton,pauseButton,stopButton,
		nextButton,repeatButton,volumeButton,enlargeButton;
	
	@FXML
	private Slider volumeSlider;
	
	@FXML
	private ProgressBar songProgressBar;
	
	@FXML
	private Label curTimeLabel,endTimeLabel; 
	
	
	private File directory;
	private File[] files;
	
	private ArrayList<File> playlist;
	
	private int songCount;
	
	private Timer timer;
	private TimerTask task;
	private boolean running;
	
	private Media media;
	private MediaPlayer mediaPlayer;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		playlist = new ArrayList<File>();
		directory = new File("music");
		
		files = directory.listFiles();
		
		if (files != null) {
			for (File file : files) {
				playlist.add(file);
			}
		}
		
		media = new Media(playlist.get(songCount).toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		
		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				mediaPlayer.setVolume(volumeSlider.getValue()*0.01);	
			}
		});
		
		songProgressBar.setStyle("-fx-accent : #111111");
		curTimeLabel.setText("00.00");
		endTimeLabel.setText("00.00");
	}
	
	public void playMusic() {
		initiateTimer();
		mediaPlayer.setVolume(volumeSlider.getValue()*0.01);
		mediaPlayer.play();
	}
	
	public void pauseMusic() {
		cancelTimer();
		mediaPlayer.pause();
	}
	
	public void stopMusic() {
		mediaPlayer.stop();
	}
	
	public void previousMusic() {
		
		if (songCount > 0) {
			
			--songCount;
			stopMusic();
			if (running) cancelTimer();
			media = new Media(playlist.get(songCount).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			playMusic();
			
		} else {
			
			stopMusic();
			songCount = playlist.size()-1;
			if (running) cancelTimer();
			media = new Media(playlist.get(songCount).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			playMusic();
			
		}
		
	}
	
	public void nextMusic() {
		
		if (songCount < playlist.size()-1) {
			
			++songCount;
			stopMusic();
			if (running) cancelTimer();
			media = new Media(playlist.get(songCount).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			playMusic();
			
		} else {
			
			stopMusic();
			songCount = 0;
			if (running) cancelTimer();
			media = new Media(playlist.get(songCount).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			playMusic();
			
		}
	}
	
	public void repeatMusic() {
		
	}
	
	public void initiateTimer() {
		timer = new Timer();
		task = new TimerTask() {
			public void run () {
				running = true;
				double curTime = mediaPlayer.getCurrentTime().toSeconds();
				double endTime = media.getDuration().toSeconds();
				songProgressBar.setProgress(curTime/endTime);
				if (curTime/endTime == 1) {
					cancelTimer();
				}
			}
		};
		timer.scheduleAtFixedRate(task, 0, 500);
		
	}
	
	public void cancelTimer() {
		running = false;
		timer.cancel();
	}



}