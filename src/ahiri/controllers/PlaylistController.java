package ahiri.controllers;

import ahiri.DatabaseConnection;
import ahiri.Playlist;
import java.sql.Connection;
import java.sql.Statement;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * This class implements functionality of playlist fxml
 *
 * @author Alve
 */
public class PlaylistController implements Initializable {

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
    private TableView<Playlist> playlistTable;
    
    private ObservableList<Playlist> playlist = FXCollections.observableArrayList();
    @FXML
    private TableColumn<Playlist, String> colName;
    @FXML
    private TableColumn<Playlist, Integer> colCount;
    @FXML
    private TextField playlistName;
    
    // Static variable to store selected playlist name
    public static String selectedPlaylistName;
    
    public void initialize(URL url, ResourceBundle rb) {
        /*
            Initializing playlist table with data from database
        */
        
        Connection conn = new DatabaseConnection().getConnection();
        String query = "SELECT * FROM playlist;";
        try{
            Statement statement = conn.createStatement();
            ResultSet queryResult = statement.executeQuery(query);
            while(queryResult.next()){
                String name = queryResult.getString("playlist_name");
                int count;
                query = "SELECT COUNT(*) FROM playlist_songs WHERE BINARY playlist_name = '"+name+"';";
                try{
                    statement = conn.createStatement();
                    ResultSet currentQueryResult = statement.executeQuery(query);
                    if(currentQueryResult.next()){
                        count=currentQueryResult.getInt(1);
                        playlist.add(new Playlist(name,count));
                    }
                    
                }catch(Exception e){
                    e.printStackTrace();
                } 
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        // Sets the value of the property cellValueFactory
        colName.setCellValueFactory(new PropertyValueFactory<Playlist,String>("name"));
        colCount.setCellValueFactory(new PropertyValueFactory<Playlist,Integer>("songCount"));
        playlistTable.setItems(playlist);
        
        // Navigate to the selected playlist
        playlistTable.getSelectionModel().setCellSelectionEnabled(true);
        ObservableList selectedCells = playlistTable.getSelectionModel().getSelectedCells();

        selectedCells.addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
                TablePosition tablePosition = (TablePosition) selectedCells.get(0);
                Object val = tablePosition.getTableColumn().getCellData(tablePosition.getRow());
                selectedPlaylistName = (String)val;
                try{
                    Stage stage = (Stage)btnMore.getScene().getWindow();
                    Parent root = FXMLLoader.load(getClass().getResource("../fxml/PlayToSongList.fxml"));
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
                }catch(Exception e){
                    e.printStackTrace();
                }
                
            }
        });
    }    

    @FXML
    private void navigateHome(MouseEvent event) throws IOException {
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
    private void navigateLibrary(MouseEvent event) throws IOException {
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
    private void navigateFavourite(MouseEvent event) throws IOException {
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

    /*
        This function adds a new playlist in the database
    */
    @FXML
    private void addNewPlaylist(MouseEvent event) {
        if(playlistName.getText().isBlank()==false){
            Connection conn = new DatabaseConnection().getConnection();
            String query = "INSERT INTO playlist(playlist_name) VALUES('"+playlistName.getText()+"');";
            try{
                Statement statement = conn.createStatement();
                statement.executeUpdate(query);
            }catch(Exception e){
                e.printStackTrace();
            }
            playlist.clear();
            conn = new DatabaseConnection().getConnection();
            query = "SELECT * FROM playlist;";
            try{
                Statement statement = conn.createStatement();
                ResultSet queryResult = statement.executeQuery(query);
                while(queryResult.next()){
                    String name = queryResult.getString("playlist_name");
                    int count;
                    query = "SELECT COUNT(*) FROM playlist_songs WHERE BINARY playlist_name = '"+name+"';";
                    try{
                        statement = conn.createStatement();
                        ResultSet currentQueryResult = statement.executeQuery(query);
                        if(currentQueryResult.next()){
                            count=currentQueryResult.getInt(1);
                            playlist.add(new Playlist(name,count));
                        }

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            colName.setCellValueFactory(new PropertyValueFactory<Playlist,String>("name"));
            colCount.setCellValueFactory(new PropertyValueFactory<Playlist,Integer>("songCount"));
            playlistTable.setItems(playlist);
        }
    }
}
