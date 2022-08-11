package ahiri.controllers;

import ahiri.DatabaseConnection;
import ahiri.LibrarySong;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * This class controls functionalities of Library.
 * @author Raha
 */

public class LibraryController implements Initializable {
    
    @FXML
    private Button libPreviousMusicBtn,libPlayPauseBtn,libNextMusicBtn,
            libVolumeBtn,HomeBtn,SearchBtn,LibraryBtn,FavouriteBtn,libStopBtn,addPlaylistBtn;
    @FXML
    private ComboBox<String> speedBox;
    @FXML
    private Slider libVolumeSlider;
    @FXML
    private ProgressBar songProgressBar;
    @FXML
    private Label libSongLabel,topLabel,totSongNumberLabel;
    @FXML
    private ImageView libPlayPauseImgView,LibAudioSpectrum;
    @FXML
    private TableView songTable;
    
    static String selectedSongName;
    Image imgPause,imgPlay,stSpec,cSpec0,cSpec1,lSpec0,lSpec1;
    
    private File directory;
    private File[] files;
    
    private ArrayList<File> songs;
    private int songNumber;
    private String speeds[] = {"0.25","0.50","0.75","1.00","1.25","1.50","1.75","2.00"};
    
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

    public ObservableList<LibrarySong> data;
    
    /**
     * This function initializes the library when the app is run first. 
     * <p>
     * @param url
     * @param rb 
     */

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try{
            for(int i = 0; i < speeds.length; i++) {
                speedBox.getItems().add(speeds[i]);
            }
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
            totSongNumberLabel.setText(String.valueOf(songs.size())+" songs");

            imgPause = new Image(new File("src/ahiri/images/libPauseBtn.png").toURI().toString());
            imgPlay = new Image(new File("src/ahiri/images/library_play.png").toURI().toString());
            stSpec = new Image(new File("src/ahiri/images/stSpec.jpg").toURI().toString());
            cSpec0 = new Image(new File("src/ahiri/images/cSpec0.gif").toURI().toString());
            cSpec1= new Image(new File("src/ahiri/images/cSpec1.gif").toURI().toString());
            lSpec0 = new Image(new File("src/ahiri/images/lSpec0.gif").toURI().toString());
            lSpec1 = new Image(new File("src/ahiri/images/lSpec1.gif").toURI().toString());
            libPlayPauseImgView.setImage(imgPlay);
            LibAudioSpectrum.setImage(stSpec);
            running = 0;
            
            libVolumeSlider.valueProperty().addListener(new ChangeListener<Number>(){
                @Override 
                public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                    mediaPlayer.setVolume(libVolumeSlider.getValue()*0.01);
                }
                
            });
            
            //songProgressBar.setStyle("-fx-accent : #FFFFFF");
            
            TableColumn c1 = new TableColumn("Track");
            TableColumn c2 = new TableColumn("Duration");
            songTable.getColumns().addAll(c1,c2);
            songTable.setColumnResizePolicy(songTable.CONSTRAINED_RESIZE_POLICY);
            
            data = FXCollections.observableArrayList();
            
            c1.setCellValueFactory(new PropertyValueFactory<LibrarySong,String>("Track"));
            c2.setCellValueFactory(new PropertyValueFactory<LibrarySong,String>("Duration"));
            loadTable(songs);
            songTable.getSelectionModel().setCellSelectionEnabled(true);
            ObservableList selectedCells = songTable.getSelectionModel().getSelectedCells();
            selectedCells.addListener(new ListChangeListener() {
                @Override
                public void onChanged(ListChangeListener.Change change) {
                    // handle if song is already playing
                    if(running==1){
                        libPlayPauseImgView.setImage(imgPlay);
                        cancelTimer();
                        mediaPlayer.stop();
                        running = 0;
                    }
                    TablePosition tp = (TablePosition)selectedCells.get(0);
                    Object ob = tp.getTableColumn().getCellData(tp.getRow());;
                    selectedSongName=(String)ob;
                    for(int i = 0; i < songs.size(); i++){
                        if(Objects.equals(selectedSongName+".mp3",songs.get(i).getName())){
                            media = new Media(songs.get(i).toURI().toString());
                            mediaPlayer = new MediaPlayer(media);
                            libSongLabel.setText(songs.get(i).getName());
                            playMedia();
                            animate();
                            break;
                        }
                        songNumber = (++songNumber)%(int)(songs.size());
                    }
                }
            });
        }catch(NullPointerException e){
            e.printStackTrace();
        }
    }
    
    /**
     * Accepts an arrayList containing .mp3 files and adds file names and duration to the table view.
     * <p>
     * @param value 
     */
    
    private void loadTable(ArrayList<File> value){
        for(int i = 0; i < value.size(); i++) {
            String songNameWithExtension = value.get(i).getName();
            String songName = songNameWithExtension.substring(0, songNameWithExtension.length()-4);
            String songDuration = MediaBarController.songDuration.getDuration(songName);
            data.add(new LibrarySong(songName,songDuration));
        }
        songTable.setItems(data);
    }

    /**
     * Plays the media and adds image of Pause media to the ImageView when the button named libPlayPauseBtn
     * is clicked and the value of variable <i>running<i> is set to zero. 
     * <p>
     * Pauses the media and adds image of Play media to the ImageView when the button named libPlayPauseBtn
     * is clicked and the value of variable <i>running<i> is set to one.
     */
    @FXML
    public void playMedia(){
        if(running == 0){
            beginTimer();
            mediaPlayer.play();
            animate();
            libPlayPauseImgView.setImage(imgPause);
            insertDB();
            running = 1;
        }else{
            cancelTimer();
            mediaPlayer.pause();
            LibAudioSpectrum.setImage(stSpec);
            libPlayPauseImgView.setImage(imgPlay);
            running = 0;
        }
    }
    /**
     * Stops the media when the button named libStopBtn is clicked and changes the 
     * image of libPlayPauseImgView ImageViewer to Play image.
     * <p>
     * It also sets the Progress Bar.
     */
    @FXML
    public void stopMedia() {
        if(running == 1){
            LibAudioSpectrum.setImage(stSpec);
            libPlayPauseImgView.setImage(imgPlay);
            running = 0;
        }
        cancelTimer();
        songProgressBar.setProgress(0);
        mediaPlayer.stop();
    }
    /**
     * It gives access of the media to the previous music file of the current file. 
     * <p>
     * It also set image Play icon to the libPlayPauseImgView.
     */
    @FXML
    public void previousMedia() {
        if(songNumber > 0) {
            songNumber--;
            mediaPlayer.stop();
            if(running == 0){
                LibAudioSpectrum.setImage(stSpec);
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
            if(running == 0){
                LibAudioSpectrum.setImage(stSpec);
                libPlayPauseImgView.setImage(imgPlay);
                running = 0;
            }else{
                cancelTimer();
            } 
            mediaPlayer.stop();
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            libSongLabel.setText(songs.get(songNumber).getName());
            selectedSongName = songs.get(songNumber).getName();
            insertDB();
            playMedia();
        }
    }
    /**
     * It gives access of the media to the next music file of the current file. 
     * <p>
     * It also set image Play icon to the libPlayPauseImgView.
     */
    @FXML
    public void nextMedia() {
        if(songNumber < songs.size()-1) {
            songNumber++;
            if(running == 0){
                LibAudioSpectrum.setImage(stSpec);
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
            if(running == 0){
                LibAudioSpectrum.setImage(stSpec);
                libPlayPauseImgView.setImage(imgPlay);
                running = 0;
            }else{
                cancelTimer();
            }   
            mediaPlayer.stop();
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            libSongLabel.setText(songs.get(songNumber).getName());
            selectedSongName = songs.get(songNumber).getName();
            insertDB();
            playMedia();
        }
    }
    /**
     * Receives an event when the speed combobox is clicked and changes speed of 
     * the media according to the chosen speed by the user.
     * <p>
     * @param event 
     */
    @FXML
    public void changeSpeed(ActionEvent event) {
        mediaPlayer.setRate(Double.parseDouble((String)speedBox.getValue()));
    }
    /**
     * Initializes Timer and TimerTask when a media is played to provide track of
     * duration to the ProgressBar.
     */
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
    /**
     * Cancels timer if the media is paused or stopped.
     */
    private void cancelTimer(){
        running = 0;
        timer.cancel();
    }
    
    /**
     * Receives an event and switches scene from library fxml to Home fxml.
     * @param event
     * @throws IOException 
     */
    @FXML
    private void navigateHome(MouseEvent event) throws IOException {
        // Stop music while navigating to other page
        if(running==1){
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
    /**
     * Receives an event and switches scene from library fxml to Favourite fxml.
     * @param event
     * @throws IOException 
     */
    @FXML
    private void navigateFavourite(MouseEvent event) throws IOException {
        // Stop music while navigating to other page
        if(running==1){
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
    private void navigatePlaylist(MouseEvent event) throws IOException {
        // Stop music while navigating to other page
        if(running==1){
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

    /**
     * Open up playlist chooser and add selected song to the playlist
     */
    @FXML
    private void addSongToPlaylist(MouseEvent event) throws IOException{
        Stage window = new Stage();
        Parent root =FXMLLoader.load(getClass().getResource("../fxml/PlaylistPopUp.fxml"));
        Scene scene = new Scene(root);
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount()==2){
                    window.setFullScreen(true);
                }
            }
        });
        window.setScene(scene);
        window.centerOnScreen();
        window.show();
    }
    
    /**
     * 
     * @return true if currently playing song is already in recentlyplayed database , false otherwise
     */
    private boolean checkDBRP(){
        Connection conn = new DatabaseConnection().getConnection();
        String query = "SELECT count(1) FROM recentlyplayed WHERE song_name = '"+selectedSongName+"';";
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
    
    // Insert currenly playing song in database
    private void insertDB() {
        Connection conn = new DatabaseConnection().getConnection();
        if(checkDBRP()==true){
            String query = "DELETE FROM recentlyplayed WHERE song_name = '"+selectedSongName+"';";
            try{
                Statement statement = conn.createStatement();
                statement.executeUpdate(query);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        String query = "INSERT INTO recentlyplayed(song_name) VALUES('"+selectedSongName+"');";
        try{
            Statement statement = conn.createStatement();
            statement.executeUpdate(query);
        }catch(Exception e){
            e.printStackTrace();
        }
    } 
    
    public int getRandomNumber(int min, int max) {
        return (int)((Math.random()*(max-min)))+min;
    }
    
    private void animate(){
       int id = getRandomNumber(1,5);
       if(id == 1){
           LibAudioSpectrum.setImage(cSpec0);
       }else if(id == 2){
           LibAudioSpectrum.setImage(cSpec1);
       }else if(id == 3){
           LibAudioSpectrum.setImage(lSpec0);
       }else{
           LibAudioSpectrum.setImage(lSpec1);
       }
    }
}

