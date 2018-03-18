package gui;

import buzzword.Buzzword;
import controller.BuzzwordController;
import data.GameData;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

import java.awt.*;
import java.awt.Image;
import java.awt.geom.RoundRectangle2D;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.scene.control.Menu;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Created by kkyeo on 11/10/2016.
 */
public class HomeScreen {

    private GridPane alphabetArea; // area for alphabets창조절시 알파벳 4x4유지해야 하므로 gridPane
    private VBox toolBar;
    private Pane emptyPane;
    private Button profileSettingBt;
    private Button helpBt;
    private Button loginBt;
    private Button createProfileBt;
    private Button profileLabel;
    private Menu levelSelectionBt;
    private Button gameStartBt;
    private GridPane homeScreenPane;

    private Circle[] wordCircle = new Circle[16];
    private StackPane[] wordUnit = new StackPane[16];
    private Label[] words = new Label[16];
    private MenuItem item1;
    private MenuItem item2;
    private MenuItem item3;
    private MenuItem item4;
    private MenuBar temp;




    public void initHomeScreen() {
        loginBt = new ToolbarButton("  Login");
        createProfileBt = new ToolbarButton("  New Profile");
        profileSettingBt = new ToolbarButton("  View/Edit Profile");
        helpBt = new ToolbarButton("  Help");

        alphabetArea = new GridPane();
        toolBar = new VBox();
        emptyPane = new Pane();
        homeScreenPane = new GridPane();

        Tooltip loginTip = new Tooltip("start Buzzword game with your ID");
        loginTip.setStyle("-fx-font-size:20");
        loginBt.setTooltip(loginTip);

        Tooltip EditProfileTip = new Tooltip("check or edit your profile");
        EditProfileTip.setStyle("-fx-font-size:20");
        profileSettingBt.setTooltip(EditProfileTip);

        Tooltip helpTip = new Tooltip("need any help?");
        helpTip.setStyle("-fx-font-size:20");
        helpBt.setTooltip(helpTip);

        Tooltip createProfileTip = new Tooltip("Input your info and start the game!");
        createProfileTip.setStyle("-fx-font-size:20");
        createProfileBt.setTooltip(createProfileTip);


        emptyPane.setPrefSize(400, 40);
        alphabetArea.setPadding(new Insets(0, 0, 0, 50)); // 상 우 하 좌 패드넣기
        //alphabetArea.setStyle("-fx-background-color : FF0099"); //alphabetArea 영역 확인용
        alphabetArea.setPrefSize(450, 330);
        alphabetArea.setVgap(15); //element들 수직간 거리
        alphabetArea.setHgap(30); //element들 수직간 거리

        setHomescreenAlphabet();// 동그라미 안에 buzzword 넣기

    }

    public GridPane getHomeScreenPane() { return homeScreenPane;}

    public VBox getToolBar(){return this.toolBar;}
    public GridPane getAlphabeticArea(){return this.alphabetArea;}
    public Pane getEmptyPane(){ return this.emptyPane;}
    public Button getLoginBt(){ return this.loginBt;}
    public Button getCreateProfileBt(){ return this.createProfileBt;}


    public void initilizeToolBar(){
        toolBar.getChildren().clear();
        toolBar.getChildren().setAll(createProfileBt, loginBt);
        homeScreenPane.getChildren().clear();
        homeScreenPane.add(alphabetArea,0,1);
        homeScreenPane.add(emptyPane,0,0);
    }

    public void nextToolBar(GameData gameData, BuzzwordController controller) throws IOException{
        profileLabel = new ToolbarButton("  "+gameData.getID()+"  ◀");
        Tooltip logoutTip = new Tooltip("Log out button");
        logoutTip.setStyle("-fx-font-size:20");
        profileLabel.setTooltip(logoutTip);

        levelSelectionBt = new Menu();

        item1 = new MenuItem("English Dictionary");
        item2 = new MenuItem("First Name");
        item3 = new MenuItem("Last Name");
        item4 = new MenuItem("Shakespeare");
        levelSelectionBt.getItems().addAll(item1,item2,item3,item4);
        levelSelectionBt.setText("Select Mode ▼ ");//▼▲
        levelSelectionBt.setStyle("-fx-background-color : transparent;" +
                " -fx-padding:13");

        temp = new MenuBar(levelSelectionBt);
        Rectangle rect = new Rectangle(120, 40);
        rect.setArcHeight(20);
        rect.setArcWidth(20);
        temp.setStyle("-fx-background-color : #888888;");
        temp.setShape(rect);


        gameStartBt= new ToolbarButton("  Start Playing");
        Tooltip gameStarttip = new Tooltip("Start game on the mode you selected before");
        gameStarttip.setStyle("-fx-font-size:20");
        gameStartBt.setTooltip(gameStarttip);
        toolBar.getChildren().clear();
        toolBar.getChildren().setAll(profileLabel,profileSettingBt,temp,gameStartBt,helpBt);
        resetHandlers(controller);
    }

    //초기 핸들러
    public void setupHandlers(BuzzwordController controller) throws IOException{
        handleLogin(controller);
        handleNewProfile(controller);
        handleHelp(controller);
    }

    //로그인 혹은 아이디 생성 후 핸들러 다시 처리
    public void resetHandlers(BuzzwordController controller) throws IOException{
        handleLogout(controller);
        handleStartPlaying(controller);
        handleModeSelection(controller);
        handleHelp(controller);
        handleProfileSettings(controller);
    }

    public void handleModeSelection(BuzzwordController controller) {
        EventHandler<ActionEvent> action = changeTabPlacement(controller);
        item1.setOnAction(action);
        item2.setOnAction(action);
        item3.setOnAction(action);
        item4.setOnAction(action);
    }

    //메뉴 이벤트 헨들링
    private EventHandler<ActionEvent> changeTabPlacement(BuzzwordController controller) {
        return new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                MenuItem mItem = (MenuItem) event.getSource();
                String side = mItem.getText();
                if ("English Dictionary".equalsIgnoreCase(side)) {
                    try{

                        GameData gamedata = controller.getGamedata();
                        String temp =gamedata.getGameMode();
                        gamedata.setGameMode("English Dictionary");
                        System.out.println("Selected "+ gamedata.getGameMode());
                        gamedata.dictionary2hashSet(); //모드 선택할때마다 hashset 셋팅.
                        JSONParser parser = new JSONParser();
                        Object obj = parser.parse(new FileReader("././saved/"+ gamedata.getID()+ ".json"));
                        JSONObject jsonObject = (JSONObject)obj;
                        jsonObject.replace("Recent Mode", temp ,gamedata.getGameMode());

                        try(FileWriter file = new FileWriter("././saved/"+ gamedata.getID() +".json")) //왜 꼭 이렇게 해야되는가? ㅡㅡ? 나중에 이유 찾자
                        {
                            file.write(jsonObject.toJSONString());
                        }
                        gamedata.renewUserInfo(); //변경된사항 그대로 갱신.
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }

                    try{ informSelectedMode(controller); }catch (Exception exception){exception.printStackTrace();}
                } else if ("First Name".equalsIgnoreCase(side)) {
                    try{
                        GameData gamedata = controller.getGamedata();
                        String temp =gamedata.getGameMode();
                        gamedata.setGameMode("First Name");
                        System.out.println("Selected "+ gamedata.getGameMode());
                        gamedata.dictionary2hashSet(); //모드 선택할때마다 hashset 셋팅.
                        JSONParser parser = new JSONParser();
                        Object obj = parser.parse(new FileReader("././saved/"+ gamedata.getID()+ ".json"));
                        JSONObject jsonObject = (JSONObject)obj;
                        jsonObject.replace("Recent Mode", temp ,gamedata.getGameMode());

                        try(FileWriter file = new FileWriter("././saved/"+ gamedata.getID() +".json")) //왜 꼭 이렇게 해야되는가? ㅡㅡ? 나중에 이유 찾자
                        {
                            file.write(jsonObject.toJSONString());
                        }
                        gamedata.renewUserInfo(); //변경된사항 그대로 갱신.
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }

                    try{ informSelectedMode(controller); }catch (Exception exception){exception.printStackTrace();}
                } else if ("Last Name".equalsIgnoreCase(side)) {
                    try{
                        GameData gamedata = controller.getGamedata();
                        String temp =gamedata.getGameMode();
                        gamedata.setGameMode("Last Name");
                        System.out.println("Selected "+ gamedata.getGameMode());
                        gamedata.dictionary2hashSet(); //모드 선택할때마다 hashset 셋팅.
                        JSONParser parser = new JSONParser();
                        Object obj = parser.parse(new FileReader("././saved/"+ gamedata.getID()+ ".json"));
                        JSONObject jsonObject = (JSONObject)obj;
                        jsonObject.replace("Recent Mode", temp ,gamedata.getGameMode());

                        try(FileWriter file = new FileWriter("././saved/"+ gamedata.getID() +".json")) //왜 꼭 이렇게 해야되는가? ㅡㅡ? 나중에 이유 찾자
                        {
                            file.write(jsonObject.toJSONString());
                        }
                        gamedata.renewUserInfo(); //변경된사항 그대로 갱신.
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }

                    try{ informSelectedMode(controller); }catch (Exception exception){exception.printStackTrace();}
                } else if ("Shakespeare".equalsIgnoreCase(side)) {
                    try{
                        GameData gamedata = controller.getGamedata();
                        String temp =gamedata.getGameMode();
                        gamedata.setGameMode("Shakespeare");
                        System.out.println("Selected "+ gamedata.getGameMode());
                        gamedata.dictionary2hashSet(); //모드 선택할때마다 hashset 셋팅.
                        JSONParser parser = new JSONParser();
                        Object obj = parser.parse(new FileReader("././saved/"+ gamedata.getID()+ ".json"));
                        JSONObject jsonObject = (JSONObject)obj;
                        jsonObject.replace("Recent Mode", temp ,gamedata.getGameMode());

                        try(FileWriter file = new FileWriter("././saved/"+ gamedata.getID() +".json")) //왜 꼭 이렇게 해야되는가? ㅡㅡ? 나중에 이유 찾자
                        {
                            file.write(jsonObject.toJSONString());
                        }
                        gamedata.renewUserInfo(); //변경된사항 그대로 갱신.
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }

                    try{ informSelectedMode(controller); }catch (Exception exception){exception.printStackTrace();}
                }
            }
        };
    }

    public void informSelectedMode(BuzzwordController controller) throws IOException {
        Stage informStage = new Stage();
        informStage.initStyle(StageStyle.TRANSPARENT);

        HBox informPanel = new HBox();
        Scene informScene = new Scene(informPanel,450,80);
        Label inform = new Label("You have selected "+ "\""+controller.getGamedata().getGameMode().toString()+"\""+" mode.");
        inform.setStyle("-fx-font-size : 15; ");
        informScene.setFill(null);
        FileInputStream input = new FileInputStream("././resources/images/icon.png");
        javafx.scene.image.Image image = new javafx.scene.image.Image(input);
        Label imageLabel = new Label();
        imageLabel.setGraphic(new ImageView((image)));
        informPanel.setAlignment(Pos.CENTER_LEFT);
        informPanel.setPadding(new Insets(0,0,0,20));
        informPanel.getChildren().addAll(imageLabel,inform);
        informPanel.setSpacing(20);

        informScene.setOnKeyPressed(e->{
            if (e.getCode().equals(KeyCode.ENTER)||e.getCode().equals(KeyCode.ESCAPE))
                informStage.close();
        });

        PauseTransition delay = new PauseTransition(javafx.util.Duration.seconds(1.9));
        delay.setOnFinished( event -> informStage.close() );
        delay.play();

        informStage.setScene(informScene);
        informStage.show();

    }


    public void handleStartPlaying(BuzzwordController controller) throws IOException{
            gameStartBt.setOnMouseClicked(e ->{
                try{
                    controller.setupLevelSelectionScreen();
                    System.out.println("Game Start");
                }
                catch(Exception exception)
                {exception.printStackTrace();}
        });
    }

    public void handleHelp(BuzzwordController controller) throws IOException{
        helpBt.setOnMouseClicked(e ->{
            try{
                controller.setupHelpScreen();
                System.out.println("Help Screen");
            }
            catch(Exception exception)
            {exception.printStackTrace();}
        });
    }

    public void handleProfileSettings(BuzzwordController controller) throws IOException{
        profileSettingBt.setOnMouseClicked(e ->{
            try{
                controller.setupProfileSettingsScreen();
                System.out.println("Profile Settings");
            }
            catch(Exception exception)
            {exception.printStackTrace();}
        });
    }


    public void handleLogout(BuzzwordController controller) throws IOException{
        profileLabel.setOnMouseClicked(e ->{
                    try{controller.confirmLogout();}
                    catch(Exception exception){exception.printStackTrace();}
        });
    }

    public void handleLogin(BuzzwordController controller) throws IOException{
        loginBt.setOnMouseClicked(e -> {
            try{controller.popLoginUp();}
            catch(Exception exception)
            {exception.printStackTrace();}
        });

    }

    public void handleNewProfile(BuzzwordController controller) throws IOException{
        createProfileBt.setOnMouseClicked(e -> {
            try {
                controller.popCreateProfileUp();
            }
            catch(Exception exception){
                exception.printStackTrace();
            }
        });
    }


    private void setHomescreenAlphabet()
    {
        for (int i = 0; i < 16; i++) {
            wordCircle[i] = new Circle();
            wordCircle[i].setRadius(35);
            wordCircle[i].setFill(Color.web("#555555"));
            words[i] = new Label();
            words[i].setTextFill(Color.web("#DDDDDD"));
            words[i].setFont(new javafx.scene.text.Font(17));
            wordUnit[i] = new StackPane();
            wordUnit[i].getChildren().addAll(wordCircle[i], words[i]);

            if (i / 4 == 0)
                alphabetArea.add(wordUnit[i], i, 0);
            else if (i / 4 == 1)
                alphabetArea.add(wordUnit[i], i - 4, 1);
            else if (i / 4 == 2)
                alphabetArea.add(wordUnit[i], i - 8, 2);
            else if (i / 4 == 3)
                alphabetArea.add(wordUnit[i], i - 12, 3);
        }

        words[0].setText("B");
        words[1].setText("U");
        words[4].setText("Z");
        words[5].setText("Z");
        words[10].setText("W");
        words[11].setText("O");
        words[14].setText("R");
        words[15].setText("D");
    }

}


