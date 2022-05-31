
package ahiri.controllers;

import ahiri.Song;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;


public class MediaBarController implements Initializable {

    @FXML
    private Button btnMore;
    @FXML
    private Button btnHome;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnLibrary;
    @FXML
    private Button btnCreatePlaylist;
    @FXML
    private Button btnFavourite;
    @FXML
    private Button previousButton;
    @FXML
    private Button playButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button repeatButton;
    @FXML
    private Button volumeButton;
    @FXML
    private Slider volumeSlider;
    
    private File directory;
    File[] file;
 
    private ArrayList<File> playlist;
 
    private int songCount;
 
    private Timer timer;
    private TimerTask task;
    private boolean running;
 
    private Media media;
    private MediaPlayer mediaPlayer;
    @FXML
    private Label curTimeLabel;
    @FXML
    private Label endTimeLabel;
    @FXML
    private Slider musicSlider;
    @FXML
    private HBox recentlyPlayedComponent;
    private HBox favouriteComponent;
    @FXML
    private ImageView selectedImg;
    @FXML
    private Label selectedSongName;
    @FXML
    private Label selectedArtist;

    String path = new File("C:\\Users\\ASUS\\OneDrive\\Documents\\NetBeansProjects\\Ahiri\\src\\ahiri\\music").getAbsolutePath();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
            
        playlist = new ArrayList<File>();
        directory = new File(path);
        
//        File starting = new File(path+"\\"+selectedSongName.getText()+".mp3");
//        playlist.add(starting);
        
        try{
            ContentController controller = new ContentController();
            List<Song> recentlyPlayed = controller.getRecentlyPlayed();
            
            for(int i=0;i<recentlyPlayed.size();i++){
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../fxml/Song.fxml"));
                VBox vbox = loader.load();
                SongController songController = loader.getController();
                songController.setData(recentlyPlayed.get(i));
                recentlyPlayedComponent.getChildren().add(vbox);
                vbox.setOnMouseClicked(new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        ObservableList<Node> list = vbox.getChildren();
                        for(int i=0;i<list.size();i++){
                            Node node = list.get(i);
                            if(i==0 && node instanceof ImageView){
                                Image image = ((ImageView)node).getImage();
                                selectedImg.setImage(image);
                            }else if(i==1 && node instanceof Label){
                                String name = ((Label)node).getText();
                                selectedSongName.setText(name);
                            }else if(node instanceof Label){
                                String artist = ((Label)node).getText();
                                selectedArtist.setText(artist);
                            }
                        }
                        file=directory.listFiles();
                        for(File files: file){
                            String name = selectedSongName.getText();
                            if((path+"\\"+name+".mp3").equals(files.toString())){
                                System.out.println(name);
                                playlist.add(files);
                                media = new Media(files.toURI().toString());
                                mediaPlayer = new MediaPlayer(media);
                                volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
                    
                                    @Override
                                    public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                                        mediaPlayer.setVolume(volumeSlider.getValue()*0.01);
                                    }
                                });
                            }
                        }
                    }
                });
            }
            musicSlider.setMin(0);
            curTimeLabel.setText("00.00");
            endTimeLabel.setText("00.00");
        }catch(IOException e){
            e.printStackTrace();
        }
    }    

    @FXML
    private void previousMusic(ActionEvent event) {
        if (songCount > 0) {
 
            --songCount;
            stopMusic(event);
            if (running) cancelTimer();
		media = new Media(playlist.get(songCount).toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		playMusic(event);
 
	} else {
 
            stopMusic(event);
            songCount = playlist.size()-1;
            if (running) cancelTimer();
            media = new Media(playlist.get(songCount).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            playMusic(event);
 
	}
    }

    @FXML
    private void playMusic(ActionEvent event) {
        
                initiateTimer();
                mediaPlayer.setVolume(volumeSlider.getValue()*0.01);
                System.out.println(media);
                mediaPlayer.play();
    }

    @FXML
    private void pauseMusic(ActionEvent event) {
        cancelTimer();
	mediaPlayer.pause();
    }

    @FXML
    private void stopMusic(ActionEvent event) {
        mediaPlayer.stop();
    }

    @FXML
    private void nextMusic(ActionEvent event) {
        if (songCount < playlist.size()-1) {
 
            ++songCount;
            stopMusic(event);
            if (running) cancelTimer();
            media = new Media(playlist.get(songCount).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            playMusic(event);
 
        } else {
 
            stopMusic(event);
            songCount = 0;
            if (running) cancelTimer();
            media = new Media(playlist.get(songCount).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            playMusic(event);
 
	}
    }

    @FXML
    private void repeatMusic(ActionEvent event) {
    }

    private void initiateTimer() {
        timer = new Timer();
	task = new TimerTask() {
            public void run () {
                running = true;
                double curTime = mediaPlayer.getCurrentTime().toSeconds();
                double endTime = media.getDuration().toSeconds();
                musicSlider.setValue(curTime/endTime);
                if (curTime/endTime == 1) {
                    cancelTimer();
                }
            }
        };
	timer.scheduleAtFixedRate(task, 0, 500);
    }
    
    private void cancelTimer(){
        running = false;
	timer.cancel();
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
