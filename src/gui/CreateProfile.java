package gui;

/**
 * Created by kkyeo on 12/10/2016.
 */


import data.GameData;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.Security;
import java.time.Duration;
import java.util.Optional;

/**
 * Created by kkyeo on 11/10/2016.
 */
public class CreateProfile {

    Stage subStage;
    Scene profileScene;
    TextField IDInput;
    PasswordField PWInput;
    StackPane IDField;
    StackPane PWField;
    GridPane profilePane;
    Label ID;
    Label PW;
    JSONObject newProfile; //새로운 profile 저장할 객체
    String algorithm = "MD5"; //비밀번호 encrypte 할 알고리즘

    public CreateProfile(GameData gameData)
    {

        subStage = new Stage();
        subStage.initStyle(StageStyle.TRANSPARENT);
        Line underbar1 = new Line();
        underbar1.setStrokeWidth(1.3);
        underbar1.setStroke(Color.WHITE);
        underbar1.setStartX(0);
        underbar1.setEndX(190);
        underbar1.setTranslateY(13); //평행이동
        Line underbar2 = new Line();
        underbar2.setStrokeWidth(1.3);
        underbar2.setStroke(Color.WHITE);
        underbar2.setStartX(0);
        underbar2.setEndX(190);
        underbar2.setTranslateY(13); //

        IDInput = new TextField();
        IDInput.setStyle("-fx-text-inner-color: white; -fx-background-color : transparent; -fx-cursor : text;" +
                "-fx-font-size:17");
        IDInput.setPrefSize(200,70);
        PWInput = new PasswordField();
        PWInput.setStyle("-fx-text-inner-color: white; -fx-background-color : transparent; " +
                "-fx-font-size:17");
        PWInput.setPrefSize(200,70);
        PWInput.setDisable(true);

        IDField = new StackPane();
        IDField.getChildren().addAll(underbar1, IDInput);
        IDField.setStyle("-fx-background-color : transparent");
        PWField = new StackPane();
        PWField.getChildren().addAll(underbar2, PWInput);
        PWField.setStyle("-fx-background-color : transparent");

        ID = new Label("Create New Profile Name");
        ID.setFont(new Font(15));
        ID.setTextFill(Color.WHITE);
        ID.setPrefSize(200,70);
        PW = new Label("Input Profile Password");
        PW.setFont(new Font(15));
        PW.setTextFill(Color.WHITE);
        PW.setPrefSize(200,70);

        profilePane = new GridPane();
        profilePane.setStyle("-fx-background-color : rgba(0, 0, 0, 0.7)");
        profilePane.add(IDField,1,0);
        profilePane.add(PWField,1,1);
        profilePane.add(ID,0,0);
        profilePane.add(PW,0,1);
        profilePane.setPadding(new Insets(22,0,0,20));

        profileScene = new Scene(profilePane, 500, 200);
        profileScene.setFill(null);

        //ESC 누르면 나가짐
        profileScene.setOnKeyPressed((KeyEvent event) ->{
            if (event.getCode().equals(KeyCode.ESCAPE))
                subStage.close();
        });

        //Enter 치면 넘어가는동작
        IDInput.setOnKeyPressed((KeyEvent event) ->{
            if (event.getCode().equals(KeyCode.ENTER)){
                IDInput.setDisable(true);
                PWInput.setDisable(false);
            }
        });


        PWInput.setOnKeyPressed((KeyEvent event) ->{
            if (event.getCode().equals(KeyCode.ENTER)) {
                PWInput.setDisable(true);

                try // 이미 아이디가 존재 할 경우 profile 생성 x
                {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(new FileReader("././saved/" + IDInput.getText() + ".json"));
                    informProfileCreationFail(IDInput.getText());
                    IDInput.deleteText(0, IDInput.getLength());
                    PWInput.deleteText(0, PWInput.getLength());
                    IDInput.setDisable(false);

                } catch (Exception e)// 이미 아이디가 존재하지 않을때 새로운 profile 생성
                {
                    try{
                        newProfile = new JSONObject();
                        newProfile.put("Name", IDInput.getText()); //아이디 저장
                        String EncrypetedPW = PWEncrypting(PWInput.getText()); //비밀번호 암호화
                        newProfile.put("Password", EncrypetedPW); //비밀번호 저장
                        newProfile.put("Recent Mode", "English Dictionary"); //새로운 아이디 생성시 초기 모드는 English Dictionary
                        newProfile.put("Max Level on English Dictionary", new Integer(1));
                        newProfile.put("Max Level on First Name", new Integer(1));
                        newProfile.put("Max Level on Last Name", new Integer(1));
                        newProfile.put("Max Level on Shakespeare", new Integer(1));
                        JSONArray list1 = new JSONArray();
                        JSONArray list2 = new JSONArray();
                        JSONArray list3 = new JSONArray();
                        JSONArray list4 = new JSONArray();

                        for (int i = 0; i < 8 ; i++)
                        {
                            list1.add(0);
                            list2.add(0);
                            list3.add(0);
                            list4.add(0);
                        }
                        newProfile.put("Max Points of English Dictionary", list1);
                        newProfile.put("Max Points of First Name", list2);
                        newProfile.put("Max Points of Last Name", list3);
                        newProfile.put("Max Points of Shakespeare", list4);

                        try(FileWriter file = new FileWriter("././saved/"+ IDInput.getText() +".json")) //왜 꼭 이렇게 해야되는가? ㅡㅡ? 나중에 이유 찾자
                        {
                            file.write(newProfile.toJSONString());
                            System.out.println("Successfully Copied JSON Object to File...");
                            System.out.println("JSON Object: " + newProfile);
                        }
                        catch(Exception es){
                            es.printStackTrace();
                        }

                        gameData.setCreatedProfile(true);
                        gameData.loadUserInfo(IDInput.getText()); //사용자 정보 불러오기
                        subStage.close();
                        informProfileCreation();
                    }
                    catch(Exception exception){

                    }

                }

            }

        });
        subStage.setScene(profileScene);
        subStage.initModality(Modality.APPLICATION_MODAL); // 이 줄과 show(); 대신 showAndWait() 함으로써
        subStage.showAndWait();                            // parentStage 비활성화시킴.
    }

    public String PWEncrypting(String password){
        String result;
        StringBuilder encryptedPW = new StringBuilder();
        try{
            MessageDigest md = MessageDigest.getInstance(algorithm); //암호화 저장 algorithm = "mp5"

            byte[] plainText = password.getBytes();
            md.reset();
            md.update(plainText);
            byte[] encodedPassword = md.digest();


            for (int i = 0; i < encodedPassword.length; i++)
            {
                if ((encodedPassword[i] & 0xff) < 0x10)
                    encryptedPW.append("0");
                encryptedPW.append(Long.toString(encodedPassword[i] & 0xff, 16));
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        result = encryptedPW.toString();
        return result;
    }

    public void informProfileCreation() throws IOException {
        Stage informStage = new Stage();
        informStage.initStyle(StageStyle.TRANSPARENT);

        HBox informPanel = new HBox();
        Scene informScene = new Scene(informPanel,350,80);
        Label inform = new Label("New profile has been created!");
        inform.setStyle("-fx-font-size : 18; ");
        informScene.setFill(null);
        FileInputStream input = new FileInputStream("././resources/images/success.png");
        Image image = new Image(input);
        Label imageLabel = new Label();
        imageLabel.setGraphic(new ImageView((image)));
        informPanel.setAlignment(Pos.CENTER);
        informPanel.getChildren().addAll(imageLabel,inform);
        informPanel.setSpacing(10);

        informScene.setOnKeyPressed(e->{
            if (e.getCode().equals(KeyCode.ENTER)||e.getCode().equals(KeyCode.ESCAPE))
                informStage.close();
        });

        PauseTransition delay = new PauseTransition(javafx.util.Duration.seconds(2.6));
        delay.setOnFinished( event -> informStage.close() );
        delay.play();

        informStage.setScene(informScene);
        informStage.show();

    }

    public void informProfileCreationFail(String message) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.setHeaderText(null);
        alert.setContentText("ID " + message +" already exists. please choose another profile name.");
        alert.getDialogPane().setStyle("-fx-font-size : 18; ");

        FileInputStream input = new FileInputStream("././resources/images/wrong.png");
        alert.setGraphic(new ImageView(new Image(input)));

        ButtonType buttonTryAgain = new ButtonType("Try again", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType buttonHome = new ButtonType("Home", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().set(1, buttonHome);
        alert.getButtonTypes().set(0, buttonTryAgain);


        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonHome) {
            subStage.close();
        } else {
            alert.close();
        }
    }


}

