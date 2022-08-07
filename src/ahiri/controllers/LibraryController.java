package ahiri.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Raha
 */
public class LibraryController implements Initializable {
    
    @FXML
    private Button libPreviousMusicBtn,libPlayPauseBtn,libNextMusicBtn,
            libVolumeBtn,HomeBtn,SearchBtn,LibraryBtn,FavouriteBtn;
    @FXML
    private ComboBox<String> speedBox;
    @FXML
    private Slider libVolumeSlider;
    @FXML
    private ProgressBar songProgressBar;
    @FXML
    private Label libSongLabel,topLabel;
    @FXML
    private ImageView libPlayPauseImgView;
    @FXML
    private TableView songTable;
    
    Image imgPause,imgPlay;
    
    private File directory;
    private File[] files;
    
    private ArrayList<File> songs;
    private int songNumber;
    private String speeds[] = {"0.25","0.50","0.75","1","1.25","1.50","1.75","2"};
    //ObservableList<String> list = FXCollection.observableArrayList("0.25","0.50","0.75","1","1.25","1.50","1.75","2");
    
    private Timer timer;
    private TimerTask task;
    
    /*
    to keep track if song is currently playing or not
    0 - play
    1 - pause
    */
    private int running; 
    
    private Media media;
    private MediaPlayer mediaPlayer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try{
            for(int i = 0; i < speeds.length; i++) {
                speedBox.getItems().add(speeds[i]);
            }
            //speedBox.getItems().addAll("0.25","0.50","0.75","1","1.25","1.50","1.75","2");
            //speedBox.setOnAction(this::changeSpeed);
            songs = new ArrayList<File>();

            directory = new File("src/ahiri/music");
            /*
            listFiles() gets all the different files from our directory.
            */
            files = directory.listFiles();

            if(files!=null) {
                for(File file : files){
                    songs.add(file);
                }
            }
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            libSongLabel.setText(songs.get(songNumber).getName());

            imgPause = new Image(new File("src/ahiri/images/libPauseBtn.png").toURI().toString());
            imgPlay = new Image(new File("src/ahiri/images/library_play.png").toURI().toString());
            libPlayPauseImgView.setImage(imgPlay);
            running = 0;
            
            libVolumeSlider.valueProperty().addListener(new ChangeListener<Number>(){
                @Override 
                public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                    mediaPlayer.setVolume(libVolumeSlider.getValue()*0.01);
                }
                
            });
            
            TableColumn c1 = new TableColumn("Track");
            TableColumn c2 = new TableColumn("Duration");
            songTable.getColumns().addAll(c1,c2);
            songTable.setColumnResizePolicy(songTable.CONSTRAINED_RESIZE_POLICY);
        }catch(NullPointerException e){
            e.printStackTrace();
        }
        
    }
    
    public void playMedia() {
        if(running == 0){
            beginTimer();
            mediaPlayer.play();
            libPlayPauseImgView.setImage(imgPause);
            running = 1;
        }else{
            cancelTimer();
            mediaPlayer.pause();
            libPlayPauseImgView.setImage(imgPlay);
            running = 0;
        }
    }
    public void stopMedia() {
        if(running == 1){
            //cancelTimer();
            libPlayPauseImgView.setImage(imgPlay);
            running = 0;
        }
        cancelTimer();
        songProgressBar.setProgress(0);
        mediaPlayer.stop();
    }
    public void previousMedia() {
        if(songNumber > 0) {
            songNumber--;
            mediaPlayer.stop();
            if(running == 1){
                //cancelTimer();
                libPlayPauseImgView.setImage(imgPlay);
                running = 0;
            }else{
                cancelTimer();
            }
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            libSongLabel.setText(songs.get(songNumber).getName());
            playMedia();
        }else{
            songNumber = songs.size()-1;
            if(running == 1){
                //cancelTimer();
                libPlayPauseImgView.setImage(imgPlay);
                running = 0;
            }else{
                cancelTimer();
            } 
            mediaPlayer.stop();
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            libSongLabel.setText(songs.get(songNumber).getName());
            playMedia();
        }
    }
    public void nextMedia() {
        if(songNumber < songs.size()-1) {
            songNumber++;
            if(running == 1){
                //cancelTimer();
                libPlayPauseImgView.setImage(imgPlay);
                running = 0;
            }else{
                cancelTimer();
            }   
            mediaPlayer.stop();
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            libSongLabel.setText(songs.get(songNumber).getName());
            playMedia();
        }else{
            songNumber = 0;
            if(running == 1){
                //cancelTimer();
                libPlayPauseImgView.setImage(imgPlay);
                running = 0;
            }else{
                cancelTimer();
            }   
            mediaPlayer.stop();
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            libSongLabel.setText(songs.get(songNumber).getName());
            playMedia();
        }
    }
    public void changeSpeed(ActionEvent event) {
        mediaPlayer.setRate(Double.parseDouble((String)speedBox.getValue()));
    }
    private void beginTimer(){
        timer = new Timer();
        task = new TimerTask(){
            public void run(){
                running = 1;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                songProgressBar.setProgress(current/end);
                if(current/end == 1){
                    cancelTimer();
                }
            }
        };
        timer.scheduleAtFixedRate(task,1000,1000);
    }
    private void cancelTimer(){
        running = 0;
        timer.cancel();
    }
    @FXML
    private void navigateHome(MouseEvent event) throws IOException {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/Home.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
    @FXML
    private void navigateFavourite(MouseEvent event) throws IOException {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/Fabourite.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
    

    
}

