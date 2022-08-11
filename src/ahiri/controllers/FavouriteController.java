package ahiri.controllers;

import ahiri.DatabaseConnection;
import ahiri.Song;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * This class implements functionality of favourite fxml
 *
 * @author Alve
 */
public class FavouriteController implements Initializable {

    @FXML
    private TableView<Song> tableViewFavourite;
    @FXML
    private TableColumn<Song, Integer> colSerial;
    @FXML
    private TableColumn<Song, String> colTitle;
    @FXML
    private TableColumn<Song, String> colArtist;
    @FXML
    private TableColumn<Song, String> colDuration;
    
    private ObservableList<Song> songs = FXCollections.observableArrayList();
    
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
    private ImageView selectedImg;
    @FXML
    private Label selectedSongName;
    @FXML
    private Label selectedArtist;
    @FXML
    private Label curTimeLabel;
    @FXML
    private Button previousButton;
    @FXML
    private ImageView imgPlayPause;
    @FXML
    private Button stopButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button repeatButton;
    @FXML
    private Slider musicSlider;
    @FXML
    private Label endTimeLabel;
    @FXML
    private Button volumeButton;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ComboBox speedBox;
    
    private File directory;
    File[] file;
 
    private ArrayList<File> playlist;
    
    private String speeds[]={"0.25","0.50","0.75","1","1.25","1.5","1.75","2"};
    @FXML
    private Label totalSongCount;
    
    private int songCount;
    
    private Timer timer;
    private TimerTask task;
    private boolean running;
 
    private Media media;
    private MediaPlayer mediaPlayer;
    String time,songName,artistName;
    int index;
    
    Image imgPause;
    Image imgPlay;

    String path = new File("src/ahiri/music").getAbsolutePath();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        playlist = new ArrayList<File>();
        directory = new File(path);
        file=directory.listFiles();
        
        //Initializing favourite song table with data from database
        index = 0;
        
        Connection conn = new DatabaseConnection().getConnection();
        String query = "SELECT * FROM favourite_list;";
        try{
            Statement statement = conn.createStatement();
            ResultSet queryResult = statement.executeQuery(query);
            while(queryResult.next()){
                songName = queryResult.getString("song_name");
                artistName = MediaBarController.artist.getName(songName);
                
                // Getting total time of this song
                time = MediaBarController.songDuration.getDuration(songName);
                
                String imgPath;
                switch(songName.toLowerCase()){
                    case "ami chinigo chini tomare":
                    case "ami kan pete roi":
                    case "bhalobeshey shokhi jodi nibhrite jotone":
                    case "pagla hawar badol din e":
                        imgPath=songName.toLowerCase();break;
                    case "the final countdown": imgPath="europa";break;
                    default: imgPath="musicbd";break;
                }
                songs.add(new Song(++index,songName,artistName,time,"../images/"+imgPath+".jpg"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        // Sets the value of the property cellValueFactory
        colSerial.setCellValueFactory(new PropertyValueFactory<Song,Integer>("serialNo"));
        colTitle.setCellValueFactory(new PropertyValueFactory<Song,String>("title"));
        colArtist.setCellValueFactory(new PropertyValueFactory<Song,String>("artist"));
        colDuration.setCellValueFactory(new PropertyValueFactory<Song,String>("duration"));
        
        tableViewFavourite.setItems(songs);
        
        // Initializing songcount label
        totalSongCount.setText(Integer.toString(index)+" songs");
        
        //Adding listener for mouse click in table cell
        tableViewFavourite.getSelectionModel().setCellSelectionEnabled(true);
        ObservableList selectedCells = tableViewFavourite.getSelectionModel().getSelectedCells();

        selectedCells.addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change c) {
                TablePosition tablePosition = (TablePosition) selectedCells.get(0);
                Object val = tablePosition.getTableColumn().getCellData(tablePosition.getRow());
                String songName = (String)val;
                selectedSongName.setText(songName);
                String artist = MediaBarController.artist.getName(songName);
                selectedArtist.setText(artist);
                String imgPath;
                switch(songName.toLowerCase()){
                    case "ami chinigo chini tomare":
                    case "ami kan pete roi":
                    case "bhalobeshey shokhi jodi nibhrite jotone":
                    case "pagla hawar badol din e":
                        imgPath=songName.toLowerCase();break;
                    case "the final countdown": imgPath="europa";break;
                    default: imgPath="musicbd";break;
                }
                Image img = new Image(new File("src/ahiri/images/"+imgPath+".jpg").toURI().toString());
                selectedImg.setImage(img);
                
                // handle if music is already playing
                        
                if(running){
                    imgPlayPause.setImage(imgPlay);
                    cancelTimer();
                    mediaPlayer.pause();
                    running = false;
                }
                
                // Traversing the music folder to get the selected song
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
        
        imgPause = new Image(new File("src/ahiri/images/pause_button_50px.png").toURI().toString());
        imgPlay = new Image(new File("src/ahiri/images/circled_play_50px.png").toURI().toString());
        curTimeLabel.setText("00.00");
        endTimeLabel.setText("00.00");
        running = false;
        
        // Initializing speedbox
        for(int i=0;i<speeds.length;i++){
            speedBox.getItems().add(speeds[i]);
        }
    }    

    @FXML
    private void navigateHome(MouseEvent event)throws IOException {
        
        // Stop music while navigating to other page
        if(running){
            cancelTimer();
            mediaPlayer.pause();
        }
        
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/Home.fxml"));
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
        
        // Manage change of thumbnail
        String previous = playlist.get(songCount).toString();
        String name = previous.substring(path.length()+1, previous.length()-4);
        selectedSongName.setText(name);
        String imgPath;
        switch(name.toLowerCase()){
            case "ami chinigo chini tomare":
            case "ami kan pete roi":
            case "bhalobeshey shokhi jodi nibhrite jotone":
            case "pagla hawar badol din e":
                imgPath=name.toLowerCase();break;
            case "the final countdown": imgPath="europa";break;
            default: imgPath="musicbd";break;
        }
        Image img = new Image(new File("src/ahiri/images/"+imgPath+".jpg").toURI().toString());
        selectedImg.setImage(img);
        String artistName = MediaBarController.artist.getName(name);
        selectedArtist.setText(artistName);
        
        insertDB();
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
        
        if(running == false){
            imgPlayPause.setImage(imgPause);
            initiateTimer();
            mediaPlayer.setVolume(volumeSlider.getValue()*0.01);
            mediaPlayer.play();
            
            insertDB();
            running = true;
        }else{
            imgPlayPause.setImage(imgPlay);
            pauseMusic(event);
            running = false;
        }       
    }
    
    private void pauseMusic(ActionEvent event) {
        cancelTimer();
	mediaPlayer.pause();
    }

    @FXML
    private void stopMusic(ActionEvent event) {
        if(running){
            imgPlayPause.setImage(imgPlay);
            running = false;
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
        
        
        // Manage change of thumbnail       
        String previous = playlist.get(songCount).toString();
        String name = previous.substring(path.length()+1, previous.length()-4);
        selectedSongName.setText(name);
        String imgPath;
        switch(name.toLowerCase()){
            case "ami chinigo chini tomare":
            case "ami kan pete roi":
            case "bhalobeshey shokhi jodi nibhrite jotone":
            case "pagla hawar badol din e":
                imgPath=name.toLowerCase();break;
            case "the final countdown": imgPath="europa";break;
            default: imgPath="musicbd";break;
        }
        Image img = new Image(new File("src/ahiri/images/"+imgPath+".jpg").toURI().toString());
        selectedImg.setImage(img);
        String artistName = MediaBarController.artist.getName(name);
        selectedArtist.setText(artistName);
        
        insertDB();
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

    @FXML
    private void changeSpeed(ActionEvent event) {
        mediaPlayer.setRate(Double.parseDouble((String)speedBox.getValue()));
    }
    
    // Initialize the timer
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
    
    // Stop the timer
    private void cancelTimer(){
        running = false;
	timer.cancel();
    }
    
    /**
     * Creates binding between media player's current time and current time showing label
     */
    private void bindCurTimeLabel() {
        curTimeLabel.textProperty().bind(Bindings.createStringBinding(new Callable<String>(){
            @Override
            public String call() throws Exception {
                return getTime(mediaPlayer.getCurrentTime());
            }
            
        }, mediaPlayer.currentTimeProperty()));
    }
    
    /**
     * 
     * @param time - time in Duration format
     * @return time in hour:minute:second format
     */
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
    
    /**
     * 
     * @return true if currently playing song is already in recentlyplayed database , false otherwise
     */
    private boolean checkDB(){
        Connection conn = new DatabaseConnection().getConnection();
        String query = "SELECT count(1) FROM recentlyplayed WHERE song_name = '"+selectedSongName.getText()+"';";
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

    /**
     * Insert currently playing song in database
     */
    private void insertDB() {
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

    @FXML
    private void navigateLibrary(MouseEvent event) throws IOException {
        // Stop music while navigating to other page
        if(running){
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

    @FXML
    private void navigatePlaylist(MouseEvent event) throws IOException {
        // Stop music while navigating to other page
        if(running){
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
