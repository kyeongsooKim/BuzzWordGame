package gui;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import controller.BuzzwordController;
import data.GameData;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by kkyeo on 11/10/2016.
 */
public class LevelSelectionScreen {
    private GridPane alphabetArea; // area for alphabets창조절시 알파벳 4x4유지해야 하므로 gridPane
    private GridPane levelSelectionPane;
    private VBox toolBar;
    private Pane modePane;
    private Button profileLabel;
    private Button homeBt;
    private Circle[] wordCircle = new Circle[8];
    private StackPane[] wordUnit = new StackPane[8];
    private Label[] words = new Label[8];
    private Tooltip activated = new Tooltip("You can play this level");
    private Tooltip inactivated = new Tooltip("You cannot play this level");


    public VBox getToolBar() {
        return toolBar;
    }

    public Pane getModePane() {
        return modePane;
    }

    public GridPane getLevelSelectionPane() {
        return levelSelectionPane;
    }


    public void initLevelSelectionScreen(GameData gameData) {

        alphabetArea = new GridPane();
        toolBar = new VBox();
        levelSelectionPane = new GridPane();
        profileLabel = new ToolbarButton(" "+gameData.getID() + "  ◀");
        Tooltip logoutTip = new Tooltip("Hi "+gameData.getID() +". You can log out by clicking this button.\n");
        logoutTip.setStyle("-fx-font-size:20");
        profileLabel.setTooltip(logoutTip);

        homeBt = new ToolbarButton(" Home");
        Tooltip homeBtTip = new Tooltip("Go back to HOME menu.");
        homeBtTip.setStyle("-fx-font-size:20");
        homeBt.setTooltip(homeBtTip);

        Label mode = new Label(" SelectedMode : " + gameData.getGameMode());
        mode.setStyle(" -fx-text-fill: #111111;");
        mode.setFont(new Font("Cambria", 20));
        mode.setAlignment(Pos.CENTER_RIGHT);
        mode.setPrefSize(400, 30);
        modePane = new Pane(mode);
        modePane.setPrefSize(400, 30);

        levelSelectionPane.setHgap(10);
        levelSelectionPane.setVgap(20);
        levelSelectionPane.add(modePane, 0, 0);
        levelSelectionPane.add(alphabetArea, 0, 1);

        alphabetArea.setPadding(new Insets(0, 0, 0, 50)); // 상 우 하 좌 패드넣기
        alphabetArea.setPrefSize(450, 330);
        alphabetArea.setVgap(15); //element들 수직간 거리
        alphabetArea.setHgap(30); //element들 수직간 거리

        initLevelOption();
        setLevelSelectionToolBar(); //툴바 기본세팅
    }

    public void setLevelSelectionToolBar() {

        toolBar.getChildren().setAll(profileLabel, homeBt);
        toolBar.setPrefSize(150, 600);
        toolBar.setPadding(new Insets(100, 5, 0, 15));
        toolBar.setSpacing(15);
        toolBar.setStyle("-fx-background-color : #BBBBBB");
    }
    public void setLevelOption(BuzzwordController controller) {
        String mode = new String(controller.getGamedata().getGameMode());
        long max = 1;

        switch (mode){
            case "English Dictionary" : max = controller.getGamedata().getMaxLevel4Edic(); break;
            case "First Name" : max = controller.getGamedata().getmaxLevel4FirstName(); break;
            case "Last Name" : max = controller.getGamedata().getmaxLevel4LastName(); break;
            case "Shakespeare" : max = controller.getGamedata().getmaxLevel4Shakespeare();
        }

        for (int i = 0; i < max ; i++)
        {
            wordCircle[i].setFill(Color.WHITE);
            words[i].setTextFill(Color.BLACK);
            wordUnit[i].setDisable(false);
            words[i].setTooltip(activated);
        }

    }

    //초기화 : 1단계 빼고 다 비활성화 시켜놈
    private void initLevelOption() {
        for (int i = 0; i < 8; i++) {


            wordCircle[i] = new Circle();
            wordCircle[i].setRadius(35);
            wordCircle[i].setFill(Color.web("#555555"));
            words[i] = new Label();
            words[i].setAlignment(Pos.CENTER);
            words[i].setPrefSize(65,65);
            words[i].setText(String.valueOf(i + 1));
            words[i].setTextFill(Color.web("#DDDDDD"));
            words[i].setFont(new javafx.scene.text.Font(17));
            wordUnit[i] = new StackPane();
            wordUnit[i].getChildren().addAll(wordCircle[i], words[i]);
            wordUnit[i].setAlignment(Pos.CENTER);
            wordUnit[i].setDisable(true); //비활성화

            if (i == 0)
                words[i].setTooltip(activated);
            else
                words[i].setTooltip(inactivated);


            if (i / 4 == 0)
                alphabetArea.add(wordUnit[i], i, 0);
            else if (i / 4 == 1)
                alphabetArea.add(wordUnit[i], i - 4, 1);
        }
    }

    public void setupHandlers(BuzzwordController controller) throws IOException {
        handleLogout(controller);
        handleHome(controller);
        handleLevelSelect(controller);
    }

    public void handleLevelSelect(BuzzwordController controller) throws IOException{
        for(int i = 0; i<8 ; i++)
        {
            int temp = Integer.parseInt(words[i].getText());
            wordUnit[i].setOnMouseClicked(e ->{
                controller.getGamedata().setGameLevel(temp);
                try {
                    controller.setGameplayScreen();
                }
                catch (Exception exception){exception.printStackTrace();}
            });
        }
    }


    public void handleLogout(BuzzwordController controller) {
        profileLabel.setOnMouseClicked(e -> {
            try {
                controller.confirmLogout();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });
    }

    public void handleHome(BuzzwordController controller) {
        homeBt.setOnMouseClicked(e -> {
            try {
                controller.returnHomeScreen();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}
