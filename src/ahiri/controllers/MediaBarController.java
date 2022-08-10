
package ahiri.controllers;

import ahiri.Artist;
import ahiri.DatabaseConnection;
import ahiri.Song;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;


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
    
    private String speeds[]={"0.25","0.50","0.75","1","1.25","1.5","1.75","2"};
 
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
    @FXML
    private ImageView selectedImg;
    @FXML
    private Label selectedSongName;
    @FXML
    private Label selectedArtist;
    @FXML
    private ComboBox speedBox;
    
    ContentController controller;
    List<Song> recentlyPlayed;
    
    Image imgPause;
    Image imgPlay;

    String path = new File("src/ahiri/music").getAbsolutePath();
    @FXML
    private ImageView imgPlayPause;
    
    boolean isPlaying;
    @FXML
    private ToggleButton favBtn;
    
    static Artist artist = new Artist();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
            
        playlist = new ArrayList<File>();
        directory = new File(path);
        
        /*
            Adding initial song and artist name
        */
        artist.add("TUMI RABE NIROBE"," Arnob");
        artist.add("BHALOBESHEY SHOKHI JODI NIBHRITE JOTONE"," Borno Chokroborty");
        artist.add("AMI CHINIGO CHINI TOMARE"," Borno Chokroborty");
        artist.add("AMI KAN PETE ROI"," Borno Chokroborty");
        
        try{
            
            for(int i=0;i<speeds.length;i++){
                speedBox.getItems().add(speeds[i]);
            }
            
            controller = new ContentController();
            recentlyPlayed = controller.getRecentlyPlayed();
            
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
                        
                        /*
                            Manage change of favourite button
                        */
                        if(checkDB()){
                            favBtn.setStyle("-fx-background-color: #039BE5;");
                        }else{
                            favBtn.setStyle("-fx-background-color: white;");
                        }
                        
                        // handle if music is already playing
                        
                        if(isPlaying){
                            imgPlayPause.setImage(imgPlay);
                            cancelTimer();
                            mediaPlayer.pause();
                            isPlaying = false;
                        }
                        
                        file=directory.listFiles();
                        for(File files: file){
                            String name = selectedSongName.getText();
                            if((path+"\\"+name+".mp3").equals(files.toString())){
                                playlist.add(files);
                                songCount=playlist.size()-1;
                                media = new Media(files.toURI().toString());
                                mediaPlayer = new MediaPlayer(media);
                                
                                bindCurTimeLabel();
                                
                                mediaPlayer.totalDurationProperty().addListener(new ChangeListener<Duration>() {
                                    @Override
                                    public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                                        musicSlider.setValue(0.0);
                                        musicSlider.setMax(newValue.toSeconds());
                                        endTimeLabel.setText(getTime(newValue));
                                    }
                                });
                                
                                mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>(){
                                    @Override
                                    public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                                        musicSlider.setValue(newValue.toSeconds());
                                    }
                                });
                                
                                musicSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
                                    @Override
                                    public void handle(MouseEvent event) {
                                        mediaPlayer.seek(Duration.seconds(musicSlider.getValue()));
                                    }
                                });
                                
                                musicSlider.setOnMouseDragged(new EventHandler<MouseEvent>() {
                                    @Override
                                    public void handle(MouseEvent event) {
                                        mediaPlayer.seek(Duration.seconds(musicSlider.getValue()));
                                    }
                                });
                                
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
            
            imgPause = new Image(new File("src/ahiri/images/pause_button_50px.png").toURI().toString());
            imgPlay = new Image(new File("src/ahiri/images/circled_play_50px.png").toURI().toString());
            curTimeLabel.setText("00.00");
            endTimeLabel.setText("00.00");
            isPlaying = false;
            
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
        }else {
 
            stopMusic(event);
            songCount = playlist.size()-1;
            if (running) cancelTimer();
            media = new Media(playlist.get(songCount).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
	}
        /*
            Manage change of thumbnail
        */
        
        String previous = playlist.get(songCount).toString();
        String name = previous.substring(path.length()+1, previous.length()-4);
        selectedSongName.setText(name);
        Image img = new Image(new File("src/ahiri/images/"+name.toLowerCase()+".jpg").toURI().toString());
        selectedImg.setImage(img);
        String artistName = artist.getName(name);
        selectedArtist.setText(artistName);
        
        insertDB();
        fillPane();
        /*
            Manage change of favourite button
        */
        if(checkDB()){
            favBtn.setStyle("-fx-background-color: #039BE5;");
        }else{
            favBtn.setStyle("-fx-background-color: white;");
        }
        bindCurTimeLabel();
        mediaPlayer.totalDurationProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                musicSlider.setValue(0.0);
                musicSlider.setMax(newValue.toSeconds());
                endTimeLabel.setText(getTime(newValue));
            }
        });
                                
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>(){
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
               musicSlider.setValue(newValue.toSeconds());
            }
        });
                                
        musicSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
              mediaPlayer.seek(Duration.seconds(musicSlider.getValue()));
           }
        });
                                
        musicSlider.setOnMouseDragged(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
              mediaPlayer.seek(Duration.seconds(musicSlider.getValue()));
           }
        });
            
        playMusic(event);
    }
    
    @FXML
    private void playMusic(ActionEvent event) {
        
        if(isPlaying == false){
            imgPlayPause.setImage(imgPause);
            initiateTimer();
            mediaPlayer.setVolume(volumeSlider.getValue()*0.01);
            mediaPlayer.play();
            insertDB();
            fillPane();
            isPlaying = true;
        }else{
            imgPlayPause.setImage(imgPlay);
            pauseMusic(event);
            isPlaying = false;
        }       
    }

    private void pauseMusic(ActionEvent event) {
        cancelTimer();
	mediaPlayer.pause();
    }

    @FXML
    private void stopMusic(ActionEvent event) {
        if(isPlaying){
            imgPlayPause.setImage(imgPlay);
            isPlaying = false;
        }
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
        } else {
 
            stopMusic(event);
            songCount = 0;
            if (running) cancelTimer();
            media = new Media(playlist.get(songCount).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
	}
        
        /*
            Manage change of thumbnail
        */
        
        String previous = playlist.get(songCount).toString();
        String name = previous.substring(path.length()+1, previous.length()-4);
        selectedSongName.setText(name);
        Image img = new Image(new File("src/ahiri/images/"+name.toLowerCase()+".jpg").toURI().toString());
        selectedImg.setImage(img);
        String artistName = artist.getName(name);
        selectedArtist.setText(artistName);
        
        insertDB();
        fillPane();
        /*
            Manage change of favourite button
        */
        if(checkDB()){
            favBtn.setStyle("-fx-background-color: #039BE5;");
        }else{
            favBtn.setStyle("-fx-background-color: white;");
        }
        bindCurTimeLabel();
        mediaPlayer.totalDurationProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                musicSlider.setValue(0.0);
                musicSlider.setMax(newValue.toSeconds());
                endTimeLabel.setText(getTime(newValue));
            }
        });
                                
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>(){
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
               musicSlider.setValue(newValue.toSeconds());
            }
        });
                                
        musicSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
              mediaPlayer.seek(Duration.seconds(musicSlider.getValue()));
           }
        });
                                
        musicSlider.setOnMouseDragged(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
              mediaPlayer.seek(Duration.seconds(musicSlider.getValue()));
           }
        });
        
        playMusic(event);
    }

    @FXML
    private void repeatMusic(ActionEvent event) {
        initiateTimer();
        mediaPlayer.seek(Duration.seconds(0));
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
        
        // Stop music while navigating to other page
        if(isPlaying){
            cancelTimer();
            mediaPlayer.pause();
        }
        
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/Fabourite.fxml"));
        Scene scene = new Scene(root);
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount()==2){
                    stage.setFullScreen(true);
                }
            }
        });
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
    
    @FXML
    private void navigateLibrary(MouseEvent event) throws IOException {
        // Stop music while navigating to other page
        if(isPlaying){
            cancelTimer();
            mediaPlayer.pause();
        }
        
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/Library.fxml"));
        Scene scene = new Scene(root);
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount()==2){
                    stage.setFullScreen(true);
                }
            }
        });
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
    private void bindCurTimeLabel() {
        curTimeLabel.textProperty().bind(Bindings.createStringBinding(new Callable<String>(){
            @Override
            public String call() throws Exception {
                return getTime(mediaPlayer.getCurrentTime());
            }
            
        }, mediaPlayer.currentTimeProperty()));
    }
    
    public String getTime(Duration time){
        int hours = (int)time.toHours();
        int minutes = ((int)time.toMinutes())%60;
        int seconds = ((int)time.toSeconds())%60;
        
        if(hours>0){
            return String.format("%d:%02d:%02d", hours,minutes,seconds);
        }else{
            return String.format("%02d:%02d", minutes,seconds);
        }
    }
    
    @FXML
    private void changeSpeed(ActionEvent event) {
        mediaPlayer.setRate(Double.parseDouble((String)speedBox.getValue()));
    }

    @FXML
    private void FavManage(ActionEvent event) {
        Connection conn = new DatabaseConnection().getConnection();
        if(checkDB()){
            /*
                This song was favourite. 
                Remove this from favourite.
            */
            String query = "DELETE FROM favourite_list WHERE song_name = '"+selectedSongName.getText()+"';";
            try{
                Statement statement = conn.createStatement();
                statement.executeUpdate(query);
                favBtn.setStyle("-fx-background-color: white; ");
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            /* 
                This song is not yet favourite.
                Make this song favourite.
            */
            String query = "INSERT INTO favourite_list(song_name) VALUES('" +selectedSongName.getText()+"');";
            try{
                Statement statement = conn.createStatement();
                statement.executeUpdate(query);
                favBtn.setStyle("-fx-background-color: #039BE5;");
            }catch(Exception e){
                e.printStackTrace();
            }
        }
}
    
    private boolean checkDB(){
        Connection conn = new DatabaseConnection().getConnection();
        String query = "SELECT count(1) FROM favourite_list WHERE song_name = '"+selectedSongName.getText()+"';";
        try{
            Statement statement = conn.createStatement();
            ResultSet queryResult = statement.executeQuery(query);
            if(queryResult.next()){
                if(queryResult.getInt(1)==1) return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    
    private void insertDB() {
        // Insert currenly playing song in database
        Connection conn = new DatabaseConnection().getConnection();
        if(checkDB()==true){
            String query = "DELETE FROM recentlyplayed WHERE song_name = '"+selectedSongName.getText()+"';";
            try{
                Statement statement = conn.createStatement();
                statement.executeUpdate(query);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        String query = "INSERT INTO recentlyplayed(song_name) VALUES('"+selectedSongName.getText()+"');";
        try{
            Statement statement = conn.createStatement();
            statement.executeUpdate(query);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void fillPane() {
        controller = new ContentController();
        recentlyPlayedComponent.getChildren().clear();
        recentlyPlayed = controller.getRecentlyPlayed();
        for(int i=0;i<recentlyPlayed.size();i++){
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../fxml/Song.fxml"));
            VBox vbox;
            try{
                vbox = loader.load();
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
                        
                        /*
                            Manage change of favourite button
                        */
                        if(checkDB()){
                            favBtn.setStyle("-fx-background-color: #039BE5;");
                        }else{
                            favBtn.setStyle("-fx-background-color: white;");
                        }
                        
                        // handle if music is already playing
                        
                        if(isPlaying){
                            imgPlayPause.setImage(imgPlay);
                            cancelTimer();
                            mediaPlayer.pause();
                            isPlaying = false;
                        }
                        
                        file=directory.listFiles();
                        for(File files: file){
                            String name = selectedSongName.getText();
                            if((path+"\\"+name+".mp3").equals(files.toString())){
                                playlist.add(files);
                                songCount=playlist.size()-1;
                                media = new Media(files.toURI().toString());
                                mediaPlayer = new MediaPlayer(media);
                                
                                bindCurTimeLabel();
                                
                                mediaPlayer.totalDurationProperty().addListener(new ChangeListener<Duration>() {
                                    @Override
                                    public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                                        musicSlider.setValue(0.0);
                                        musicSlider.setMax(newValue.toSeconds());
                                        endTimeLabel.setText(getTime(newValue));
                                    }
                                });
                                
                                mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>(){
                                    @Override
                                    public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                                        musicSlider.setValue(newValue.toSeconds());
                                    }
                                });
                                
                                musicSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
                                    @Override
                                    public void handle(MouseEvent event) {
                                        mediaPlayer.seek(Duration.seconds(musicSlider.getValue()));
                                    }
                                });
                                
                                musicSlider.setOnMouseDragged(new EventHandler<MouseEvent>() {
                                    @Override
                                    public void handle(MouseEvent event) {
                                        mediaPlayer.seek(Duration.seconds(musicSlider.getValue()));
                                    }
                                });
                                
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
            }catch (IOException ex) {
                Logger.getLogger(MediaBarController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void navigatePlaylist(MouseEvent event) throws IOException {
        // Stop music while navigating to other page
        if(isPlaying){
            cancelTimer();
            mediaPlayer.pause();
        }
        
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/Playlist.fxml"));
        Scene scene = new Scene(root);
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount()==2){
                    stage.setFullScreen(true);
                }
            }
        });
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}
