package gui;

import controller.BuzzwordController;
import data.GameData;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Iterator;

/**
 * Created by kkyeo on 11/10/2016.
 */
public class ProfileSettings {
    private VBox profileSettingsPane;
    private HBox pointsPane;
    private VBox toolBar;
    private Button homeBt;
    private String algorithm = "MD5";

    public VBox getProfileSettingsPane() {return profileSettingsPane;}

    public VBox getToolBar() {
        return toolBar;
    }

    public void initProfileSettingsScreen(GameData gameData) throws IOException{

        toolBar = new VBox();
        profileSettingsPane = new VBox();
        profileSettingsPane.setPadding(new Insets(5, 5, 5, 5));
        profileSettingsPane.setSpacing(10);
        profileSettingsPane.setAlignment(Pos.CENTER_LEFT);
        pointsPane = new HBox();
        pointsPane.setPadding(new Insets(0,5,5,5));
        pointsPane.setSpacing(20);
        pointsPane.setStyle(" -fx-background-color : WHITE");
        pointsPane.setAlignment(Pos.CENTER);

        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader("././saved/"+ gameData.getID() + ".json"));
            JSONObject jsonObject = (JSONObject)obj;

            //gameData.renewUserInfo(); // 정보 띄우기전 최신 정보 갱신
            Label playerName = new Label("Player Name : " + gameData.getID());
            profileSettingsPane.getChildren().add(0,playerName);
            Label recentMode = new Label("Recent played Mode : "+jsonObject.get("Recent Mode"));
            profileSettingsPane.getChildren().add(1,recentMode);
            profileSettingsPane.getChildren().add(2, new Label("Points Table"));

            VBox edicPoints = new VBox();
            VBox firstNamePoints = new VBox();
            VBox lastNamePoints = new VBox();
            VBox shakespearePOints = new VBox();
            edicPoints.getChildren().add(0, new Label("English Dictionary"));
            firstNamePoints.getChildren().add(0, new Label("First Name"));
            lastNamePoints.getChildren().add(0, new Label("Last Name"));
            shakespearePOints.getChildren().add(0, new Label("Shakespeare"));
            fillUpPoints4Mode(gameData.getMaxLevel4Edic(), edicPoints, gameData.getMaxPoints4EachEdic());
            fillUpPoints4Mode(gameData.getmaxLevel4FirstName(), firstNamePoints, gameData.getMaxPoints4EachFirstName());
            fillUpPoints4Mode(gameData.getmaxLevel4LastName(), lastNamePoints, gameData.getMaxPoints4EachLastName());
            fillUpPoints4Mode(gameData.getmaxLevel4Shakespeare(), shakespearePOints, gameData.getMaxPoints4EachShakespeare());
            pointsPane.getChildren().addAll(edicPoints,firstNamePoints,lastNamePoints,shakespearePOints);
            profileSettingsPane.getChildren().add(3, pointsPane);

            PasswordField currentPW = new PasswordField();
            PasswordField newPW = new PasswordField();
            PasswordField newPWAgain = new PasswordField();
            newPW.setDisable(true);
            newPWAgain.setDisable(true);
            VBox pwChangePanel = new VBox(new HBox(new Label("Current Password : "), currentPW), new HBox(new Label("New Password : "), newPW)
                    ,new HBox(new Label("New Password Confirmation : "), newPWAgain));
            pwChangePanel.setVisible(false);
            Button pwChangeButton = new Button("Edit Password");
            pwChangeButton.setOnMouseClicked(e ->{
                if (!pwChangePanel.isVisible())
                    pwChangePanel.setVisible(true);
            });
            profileSettingsPane.getChildren().add(4, pwChangeButton);
            profileSettingsPane.getChildren().add(5, pwChangePanel);
            currentPW.setOnKeyPressed((KeyEvent event) -> {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    currentPW.setDisable(true);
                    newPW.setDisable(false);
                }
            });
            newPW.setOnKeyPressed((KeyEvent event) -> {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    newPW.setDisable(true);
                    newPWAgain.setDisable(false);
                }
            });
            newPWAgain.setOnKeyPressed((KeyEvent event) ->{
                if (event.getCode().equals(KeyCode.ENTER)){
                    newPWAgain.setDisable(true);
                    currentPW.setDisable(false);


                    try{
                        if (!stringEncrypting(currentPW.getText()).equals(gameData.getPW()))
                            popupSimpleMessage("You have inputted wrong current password\nPlease try again", 4.5);
                        else
                        {
                            if (currentPW.getText().equals(newPW.getText()))
                                popupSimpleMessage("New Password is same as previous one.\nPlease make different Password", 5);
                            else if (!newPW.getText().equals(newPWAgain.getText()))
                                popupSimpleMessage("Confirmation password for new password is wrong.\nPlease try again.", 5);
                            else if (newPW.getText().equals(newPWAgain.getText())){
                                pwChangePanel.setVisible(false);
                                jsonObject.replace("Password",gameData.getPW(), stringEncrypting(newPW.getText())); //새 비번 교환
                                try(FileWriter file = new FileWriter("././saved/"+ gameData.getID() +".json")) //왜 꼭 이렇게 해야되는가? ㅡㅡ? 나중에 이유 찾자
                                {
                                    file.write(jsonObject.toJSONString());
                                    popupSimpleMessage("Your password has been successfuly changed.", 5);
                                }
                                gameData.renewUserInfo(); //변경된사항 그대로 갱신.
                            }
                        }
                    }

                    catch(Exception e){
                        e.printStackTrace();
                    }
                    currentPW.deleteText(0,currentPW.getLength());
                    newPW.deleteText(0, newPW.getLength());
                    newPWAgain.deleteText(0, newPWAgain.getLength());

                }
            });

        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        homeBt = new ToolbarButton(" Home");
        Tooltip homeBtTip = new Tooltip("Go back to HOME menu.");
        homeBtTip.setStyle("-fx-font-size:20");
        homeBt.setTooltip(homeBtTip);
        setHelpToolBar(); //툴바 기본세팅
    }

    public void fillUpPoints4Mode(long maxLevel, VBox column, long[] pointsArray){
        column.getChildren().add(0, new Label(""));
        for (int i = 0; i < (int)maxLevel; i++)
        {
            column.getChildren().add(new Label("level"+(i+1)+" : "+(int)pointsArray[i]));
        }
    }


    public void setHelpToolBar() {

        toolBar.getChildren().setAll(homeBt);
        toolBar.setPrefSize(150, 600);
        toolBar.setPadding(new Insets(100, 5, 0, 15));
        toolBar.setSpacing(15);
        toolBar.setStyle("-fx-background-color : #BBBBBB");
    }

    public void setupHandlers(BuzzwordController controller) throws IOException {
        handleHome(controller);
    }


    public void handleHome(BuzzwordController controller) {
        homeBt.setOnMouseClicked(e -> {
            try {
                profileSettingsPane = null;
                controller.returnHomeScreen();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public String stringEncrypting(String string){
        String result;
        StringBuilder encryptedString = new StringBuilder();
        try{
            MessageDigest md = MessageDigest.getInstance(algorithm); //암호화 저장 algorithm = "mp5"
            byte[] plainText = string.getBytes();
            md.reset();
            md.update(plainText);
            byte[] encodedPassword = md.digest();


            for (int i = 0; i < encodedPassword.length; i++)
            {
                if ((encodedPassword[i] & 0xff) < 0x10)
                    encryptedString.append("0");
                encryptedString.append(Long.toString(encodedPassword[i] & 0xff, 16));
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        result = encryptedString.toString();
        return result;
    }

    public void popupSimpleMessage(String message, double time) throws IOException {
        Stage informStage = new Stage();
        informStage.initStyle(StageStyle.TRANSPARENT);
        HBox informPanel = new HBox();
        Scene informScene = new Scene(informPanel,450,90);
        Label inform = new Label(message);
        inform.setStyle("-fx-font-size : 16; ");
        informScene.setFill(null);
        informPanel.setAlignment(Pos.CENTER_LEFT);
        informPanel.getChildren().addAll(inform);
        informPanel.setSpacing(30);
        informPanel.setPadding(new Insets(0,0,0,20));


        informScene.setOnKeyPressed(e->{
            if (e.getCode().equals(KeyCode.ENTER)||e.getCode().equals(KeyCode.ESCAPE))
                informStage.close();
        });

        PauseTransition delay = new PauseTransition(javafx.util.Duration.seconds(time));
        delay.setOnFinished( event -> informStage.close() );
        delay.play();

        informStage.setScene(informScene);
        informStage.show();

    }
}

