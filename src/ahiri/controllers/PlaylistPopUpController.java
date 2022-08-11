package ahiri.controllers;

import ahiri.DatabaseConnection;
import ahiri.Playlist;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * This class implements functionality of PlaylistPopUpController fxml
 *
 * @author ASUS
 */
public class PlaylistPopUpController implements Initializable {
    
    private ObservableList<Playlist> playlist = FXCollections.observableArrayList();
    @FXML
    private TableView<Playlist> playlistTable;
    @FXML
    private TableColumn<Playlist, String> colName;
    @FXML
    private TableColumn<Playlist, Integer> colCount;
    
    String playlistName,songName,query;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /*
            Initializing playlist table with data from database
        */
        
        Connection conn = new DatabaseConnection().getConnection();
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
        // Sets the value of the property cellValueFactory
        colName.setCellValueFactory(new PropertyValueFactory<Playlist,String>("name"));
        colCount.setCellValueFactory(new PropertyValueFactory<Playlist,Integer>("songCount"));
        playlistTable.setItems(playlist);
        
        // Add to the selected playlist
        playlistTable.getSelectionModel().setCellSelectionEnabled(true);
        ObservableList selectedCells = playlistTable.getSelectionModel().getSelectedCells();

        selectedCells.addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
                TablePosition tablePosition = (TablePosition) selectedCells.get(0);
                Object val = tablePosition.getTableColumn().getCellData(tablePosition.getRow());
                playlistName = (String) val;
                songName = LibraryController.selectedSongName;
            }
        });
    }    

    @FXML
    private void addSong(MouseEvent event) {
        Connection conn =new DatabaseConnection().getConnection();
        query = "INSERT INTO playlist_songs(playlist_name,song_name) VALUES('"+playlistName+"','"+songName+"');";
        try{
            Statement statement = conn.createStatement();
            statement.executeUpdate(query);
        }catch(Exception e){
            e.printStackTrace();
        }
        Stage stage = (Stage)playlistTable.getScene().getWindow();
        stage.close();
    }
    
}
