
package ahiri.controllers;

import ahiri.DatabaseConnection;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class LoginSignupController implements Initializable {

    @FXML
    private AnchorPane layer1;
    @FXML
    private Label lblWelcome;
    @FXML
    private Label lblSignIn;
    @FXML
    private ImageView imgfb;
    @FXML
    private ImageView imgGoogle;
    @FXML
    private ImageView imgInsta;
    @FXML
    private Label lblEmailAcc;
    @FXML
    private TextField nameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label lblForgot;
    @FXML
    private Button btnSignIn;
    @FXML
    private Label lblCreateAcc;
    @FXML
    private Label lblNewUser;
    @FXML
    private Label lblEmailReg;
    @FXML
    private TextField emailField;
    @FXML
    private Button btnSignUpDup;
    @FXML
    private AnchorPane layer2;
    @FXML
    private Button btnSignUp;
    @FXML
    private Button btnSignInDup;
    @FXML
    private Label loginMessage;
    @FXML
    private ImageView imgMail;
    @FXML
    private Label successfulregistrationlabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnSignInDup.setVisible(false);
        lblNewUser.setVisible(false);
        lblCreateAcc.setVisible(false);
        lblEmailReg.setVisible(false);
        emailField.setVisible(false);
        btnSignUpDup.setVisible(false);
        imgMail.setVisible(false);
        successfulregistrationlabel.setVisible(false);
        btnSignUp.setVisible(true);
        lblWelcome.setVisible(true);
        lblSignIn.setVisible(true);
        lblEmailAcc.setVisible(true);
        lblForgot.setVisible(true);
        btnSignIn.setVisible(true);
    }    

    @FXML
    private void handleSignIn(MouseEvent event) {
        if(nameField.getText().isBlank()==false && passwordField.getText().isBlank()==false){
            validateLogin(event);
        }else{
            showMessage(loginMessage,"Username/Password can not be empty.");
        }
    }

    @FXML
    private void handleSignUpDup(MouseEvent event) {
        if(nameField.getText().isBlank()==true || passwordField.getText().isBlank()==true){
            showMessage(loginMessage,"Username/Password can not be empty.");
        }
        final String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(emailField.getText());
        System.out.println(emailField.getText());
        if(!matcher.matches()){
            showMessage(loginMessage,"Invalid Email address");
        }else{
            register_user();
        }
    }

    @FXML
    private void handleSignUp(MouseEvent event) {
        TranslateTransition slider = new TranslateTransition();
        slider.setDuration(Duration.seconds(1));
        slider.setNode(layer2);
        slider.setByX(-337);
        slider.play();
        layer1.setTranslateX(399);
        btnSignInDup.setVisible(true);
        lblNewUser.setVisible(true);
        lblCreateAcc.setVisible(true);
        lblEmailReg.setVisible(true);
        emailField.setVisible(true);
        btnSignUpDup.setVisible(true);
        imgMail.setVisible(true);
        btnSignUp.setVisible(false);
        lblWelcome.setVisible(false);
        lblSignIn.setVisible(false);
        lblEmailAcc.setVisible(false);
        lblForgot.setVisible(false);
        btnSignIn.setVisible(false);
    }

    @FXML
    private void handleSignInDup(MouseEvent event) {
        TranslateTransition slider = new TranslateTransition();
        slider.setDuration(Duration.seconds(1));
        slider.setNode(layer2);
        slider.setByX(337);
        slider.play();
        layer1.setTranslateX(0);
        btnSignInDup.setVisible(false);
        lblNewUser.setVisible(false);
        lblCreateAcc.setVisible(false);
        lblEmailReg.setVisible(false);
        emailField.setVisible(false);
        btnSignUpDup.setVisible(false);
        imgMail.setVisible(false);
        btnSignUp.setVisible(true);
        lblWelcome.setVisible(true);
        lblSignIn.setVisible(true);
        lblEmailAcc.setVisible(true);
        lblForgot.setVisible(true);
        btnSignIn.setVisible(true);
    }

    private void validateLogin(MouseEvent event) {
        Connection conn = new DatabaseConnection().getConnection();
        String query = "SELECT count(1) FROM user_account WHERE BINARY username = '" + nameField.getText()
                + "' AND BINARY password = '" + passwordField.getText() + "';";
        
        try{
            Statement statement = conn.createStatement();
            ResultSet queryResult = statement.executeQuery(query);
            if(queryResult.next()){
                if(queryResult.getInt(1)==1){
                    Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                    Parent root = FXMLLoader.load(getClass().getResource("../fxml/Home.fxml"));
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.centerOnScreen();
                    stage.show();
                }else{
                    showMessage(loginMessage,"Invalid login.Please try again.");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void register_user() {
        Connection conn = new DatabaseConnection().getConnection();
        String query = "INSERT INTO user_account(username,password,email) VALUES('" +
                nameField.getText() + "','" + passwordField.getText() + "','" +
                emailField.getText() + "');";
        
        try{
            Statement statement = conn.createStatement();
            statement.executeUpdate(query);
            showMessage(successfulregistrationlabel,"Successfully Resgistered.");
        }catch(SQLException ex){
            if(ex.getErrorCode()==1062){
                showMessage(loginMessage,"Username/Email/Password exists.Please try again.");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    private void showMessage(Label label,String message){
        label.setVisible(true);
        label.setText(message);
        PauseTransition wait = new PauseTransition(Duration.seconds(2));
        wait.setOnFinished((e)->{
            label.setVisible(false);
        });
        wait.play();
    }
}
