package gui;

import data.GameData;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import sun.awt.AWTAccessor;
import sun.awt.SunHints;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Optional;
import java.util.Stack;

/**
 * Created by kkyeo on 11/12/2016.
 */
public class Login {

    Stage subStage;
    Scene profileScene;
    TextField IDInput;
    PasswordField PWInput;
    StackPane IDField;
    StackPane PWField;
    GridPane profilePane;
    Label ID;
    Label PW;
    GameData gameData;
    String algorithm = "MD5";

    public Login(GameData gameData) {
        this.gameData = gameData;
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
        IDInput.setPrefSize(200, 70);
        PWInput = new PasswordField();
        PWInput.setStyle("-fx-text-inner-color: white; -fx-background-color : transparent; " +
                "-fx-font-size:17");
        PWInput.setPrefSize(200, 70);
        PWInput.setDisable(true);

        IDField = new StackPane();
        IDField.getChildren().addAll(underbar1, IDInput);
        IDField.setStyle("-fx-background-color : transparent");
        PWField = new StackPane();
        PWField.getChildren().addAll(underbar2, PWInput);
        PWField.setStyle("-fx-background-color : transparent");

        ID = new Label("Profile Name");
        ID.setFont(new Font(20));
        ID.setTextFill(Color.WHITE);
        ID.setPrefSize(200, 70);
        PW = new Label("Profile Password");
        PW.setFont(new Font(20));
        PW.setTextFill(Color.WHITE);
        PW.setPrefSize(200, 70);

        profilePane = new GridPane();
        profilePane.setStyle("-fx-background-color : rgba(0, 0, 0, 0.7)");
        profilePane.add(IDField, 1, 0);
        profilePane.add(PWField, 1, 1);
        profilePane.add(ID, 0, 0);
        profilePane.add(PW, 0, 1);
        profilePane.setPadding(new Insets(22, 0, 0, 20));

        profileScene = new Scene(profilePane, 500, 200);
        profileScene.setFill(null);

        //ESC 누르면 나가짐
        profileScene.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                subStage.close();
            }

        });

        //Enter 치면 넘어가는동작
        IDInput.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                IDInput.setDisable(true);
                PWInput.setDisable(false);
            }
        });

        PWInput.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                PWInput.setDisable(true);
                validateProfile(IDInput.getText(), PWInput.getText());
            }

        });

        subStage.setScene(profileScene);
        subStage.initModality(Modality.APPLICATION_MODAL); // 이 줄과 show(); 대신 showAndWait() 함으로써
        subStage.showAndWait();                            // parentStage 비활성화시킴.
    }



    public void validateProfile(String ID, String PW) {
        System.out.println("validating credentials...");
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader("././saved/"+ ID + ".json"));
            JSONObject jsonObject = (JSONObject)obj;
            String name = (String) jsonObject.get("Name");
            String password = (String) jsonObject.get("Password");
            String EncryptedPW = stringEncrypting(PW); //암호화 시킨뒤 비교
                      if (name.equals(ID) && password.equals(EncryptedPW))
            {
                subStage.close();

                gameData.setLoggedin(true);
                gameData.loadUserInfo(ID); // 사용자 정보 불러오기
                System.out.println("Logged in");
                try {
                    informLoginSuccess();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else //ID이름 파일은 찾았지만 password 잘못 입력 wrong Password input warning
            {
                System.out.println("Wrong password. try again");
                IDInput.deleteText(0, IDInput.getLength());
                PWInput.deleteText(0, PWInput.getLength());
                try {
                    alertWrongProfile("Password is not correct");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                IDInput.setDisable(false);
            }
        } catch (Exception except) //ID 이름의 파일 자체를 못 찾았으므로 wrong ID input warning
        {
            try {

                System.out.println("Wrong ID. try again");
                IDInput.deleteText(0, IDInput.getLength());
                PWInput.deleteText(0, PWInput.getLength());
                try {
                    alertWrongProfile("There is no ID such that.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                IDInput.setDisable(false);

            } catch (Exception ex) {}
        }

    }

    public void alertWrongProfile(String message) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.setHeaderText(null);
        alert.setContentText(message);
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

    public void informLoginSuccess() throws IOException {
        Stage informStage = new Stage();
        informStage.initStyle(StageStyle.TRANSPARENT);

        HBox informPanel = new HBox();
        Scene informScene = new Scene(informPanel, 330, 80);
        Label inform = new Label("Logged in\n" + "Welcome " + gameData.getID().toString() + "!");
        inform.setStyle("-fx-font-size : 18; ");
        informScene.setFill(null);
        FileInputStream input = new FileInputStream("././resources/images/success.png");
        Image image = new Image(input);
        Label imageLabel = new Label();
        imageLabel.setGraphic(new ImageView((image)));
        informPanel.setAlignment(Pos.CENTER);
        informPanel.getChildren().addAll(imageLabel, inform);
        informPanel.setSpacing(30);

        informScene.setOnKeyPressed
                (e -> {
            if (e.getCode().equals(KeyCode.ENTER) || e.getCode().equals(KeyCode.ESCAPE))
                informStage.close();
        });

        PauseTransition delay = new PauseTransition(javafx.util.Duration.seconds(2.8));
        delay.setOnFinished(event -> informStage.close());
        delay.play();

        informStage.setScene(informScene);
        informStage.show();
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
}
