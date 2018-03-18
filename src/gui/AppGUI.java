package gui;


import controller.BuzzwordController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by kkyeo on 11/12/2016.
 */
public class AppGUI {

    public enum GuiState {
        HOMESCREEN,
        AFTERLOGIN,
        GAMEPLAY,
        LEVELSELECTION,
        GAMEPLAY_PLAYING,
        GAMEPLAY_PAUSE,
        GAMEFINISHED_CLEAR,
        GAMEFINISHED_FAIL,
        GAMEFINISHED_FAIL_NEXTLEVEL,
        HELPSCREEN,
        PROFILESETTING
    }

    final KeyCombination keyComb4Login = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN);
    final KeyCombination keyComb4CreateProfile = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
    final KeyCombination keyComb4PlayGame = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN);
    final KeyCombination keyComb4ReplayGame = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
    final KeyCombination keyComb4ReturnHome = new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN);
    final KeyCombination keyComb4CloseApplication = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
    final KeyCombination keyComb4NextLevel = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN);


    protected BuzzwordController controller;
    protected Stage primaryStage;
    protected Scene primaryScene;
    protected BorderPane appPane;     // container to display the whole GUI components //Borderpane으로 해야 창 크기 조절시 배경도 자연스럽게 늘어남
    protected VBox toolBar;           // toolbar for game buttons
    protected VBox mainPane;          // container for the main components
    protected GridPane headPane;          // container to display the heading
    protected Label guiHeadingLabel;  // workspace (GUI) heading label
    protected GridPane bodyPane;      // container to display main game;
    protected GridPane alphabetArea; // area for alphabets //창조절시 알파벳 4x4유지해야 하므로 gridPane
    protected Pane modePane;
    protected GuiState guiState;

    //Screen & Login & Help...
    protected GameplayScreen gameplayScreen;
    protected HomeScreen homeScreen;
    protected LevelSelectionScreen levelSelectionScreen;
    protected Help helpScreen;
    protected ProfileSettings profileSettingsScreen;

    public void setGameplayScreen(GameplayScreen gameplayScreen){this.gameplayScreen = gameplayScreen;}
    public void setLevelSelectionScreen(LevelSelectionScreen levelSelectionScreen) {this.levelSelectionScreen = levelSelectionScreen;}
    public void setHelpScreen(Help helpScreen) {this.helpScreen = helpScreen;}
    public void setProfileSettingsScreen(ProfileSettings profileSettingsScreen) {this.profileSettingsScreen = profileSettingsScreen;}
    public GameplayScreen getGameplayScreen() {return gameplayScreen;}
    public LevelSelectionScreen getLevelSelectionScreen() {return levelSelectionScreen;}
    public Help getHelpScreen() {return helpScreen;}
    public ProfileSettings getProfileSettingsScreen() {return profileSettingsScreen;}

    public HomeScreen getHomeScreen() { return homeScreen; }


    public VBox getToolBar() {return toolBar;}
    public void setToolBar(VBox toolBar) {this.toolBar = toolBar;}
    public GridPane getBodyPane() {return bodyPane;}
    public void setBodyPane(GridPane bodyPane) {this.bodyPane = bodyPane;}

    public GuiState getGuiState() {return guiState;}
    public void setGuiState(GuiState guiState) {this.guiState = guiState;}

    public AppGUI(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    public void setController(BuzzwordController controller) {
        this.controller = controller;
    }

    // set initial window

    public void initializeWindow() throws IOException
    {
        File file = new File("././resources/images/icon.png");//두칸 상위로 올라가서 다시내려오는 상대경로
        primaryStage.getIcons().add(new Image(file.toURI().toString()));
        primaryStage.setTitle("BuzzWord");

        FileInputStream input = new FileInputStream("././resources/images/exit.png");
        Image image = new Image(input);
        Label exitLabel = new Label();
        Tooltip exitTip = new Tooltip("Exit BuzzWord game\n\"Ctrl + Q\"");
        exitTip.setStyle("-fx-font-size:20");
        exitLabel.setGraphic(new ImageView((image)));
        exitLabel.setTooltip(exitTip);
        exitLabel.setOnMouseClicked (e->{
            try {
                controller.confirmExit();
            }
            catch(Exception exception){
                exception.printStackTrace();
            }
        });

        appPane = new BorderPane();
        toolBar = new VBox();
        mainPane = new VBox();
        headPane = new GridPane();
        bodyPane = new GridPane();
        alphabetArea = new GridPane();
        modePane = new Pane();

        toolBar.setPrefSize(150,600);
        toolBar.setPadding(new Insets(100,5,0,15));
        toolBar.setSpacing(15);
        toolBar.setStyle("-fx-background-color : #BBBBBB");
        guiHeadingLabel = new Label("BuzzWord");
        guiHeadingLabel.setTextFill(Color.web("#333333"));
        guiHeadingLabel.setFont(new Font("Cambria", 35));
        guiHeadingLabel.setAlignment(Pos.CENTER); //글자 가운데 정렬
        guiHeadingLabel.setPrefSize(750,45);
        mainPane.setStyle("-fx-background-color : #AAAAAA");
        bodyPane.setHgap(10);
        bodyPane.setVgap(10);
        bodyPane.setPadding(new Insets(10,10,10,10));
        FlowPane temp = new FlowPane();
        temp.getChildren().addAll(exitLabel);
        temp.setAlignment(Pos.TOP_RIGHT);
        headPane.add(temp,2,0);
        headPane.add(guiHeadingLabel,1,1);
        headPane.setPrefSize(750,45);

        appPane.setCenter(mainPane);
        mainPane.getChildren().add(headPane);
        primaryScene = new Scene(appPane, 900, 645); //초기 게임창 사이즈 900, 700
        //primaryScene.getStylesheets().add("css/buzzword.css"); Scene 에 씌우면 전체 다 덮힘

        //초기 시작은 homeScreen부터
        homeScreen = new HomeScreen();
        homeScreen.initHomeScreen();;
        controller.setHomeScreen();
        activateShortcut();

        //bodyPane.setStyle("-fx-background-color : BLACK"); //bodypane 영역 확인용
        mainPane.getChildren().add(bodyPane);
        appPane.setLeft(toolBar);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(primaryScene);
        primaryStage.show();

    }

    public void activateShortcut()
    {

        if(guiState.equals(GuiState.HOMESCREEN))
        primaryScene.setOnKeyPressed((KeyEvent event) ->    //이 문장 때문에 매번 상태 바뀔때마다 activateShortcut 혹은
        {                                                   //reactivate 해야한다. primaryScene에 적용되는 이벤트헨들링이
            if (keyComb4Login.match(event)){                //추가 혹은 달라지므로! 참고로 reactivateshortcut 하나만 만들어도
                System.out.println("shortcut \"ctrl+L\" : Popped Login-window up");//되는데 함수 이름차이로 의미 전달 하고자 함
                try{
                    controller.popLoginUp();
                }
                catch(Exception exception){
                    exception.printStackTrace();
                }

            }
            else if (keyComb4CreateProfile.match(event)){
                System.out.println("shortcut \"ctrl+shit+P\" : Create New Profile");
                try{
                    controller.popCreateProfileUp();
                }
                catch(Exception exception){
                    exception.printStackTrace();
                }
            }
            else if (keyComb4CloseApplication.match(event)){
                try {controller.confirmExit();}
                catch(Exception exception){exception.printStackTrace();}
                System.out.println("shortcut \"ctrl+Q\" : Try Exit");
            }
            else if (keyComb4ReturnHome.match(event)){
                try {controller.returnHomeScreen();}
                catch(Exception exception){exception.printStackTrace();}
            }
        });
    }
    public void reActivateShortcut()
    {
        if (guiState.equals(GuiState.HOMESCREEN))
            primaryScene.setOnKeyPressed((KeyEvent event) ->
            {
                if (keyComb4Login.match(event)) {
                    System.out.println("shortcut \"ctrl+L\" : Popped Login Window up");
                    try {
                        controller.popLoginUp();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                } else if (keyComb4CreateProfile.match(event)) {
                    System.out.println("shortcut \"ctrl+shit+P\" : Create New Profile");
                    try {
                        controller.popCreateProfileUp();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                } else if (keyComb4CloseApplication.match(event)) {
                    try {
                        controller.confirmExit();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    System.out.println("shortcut \"ctrl+Q\" : Try Exit");
                }
                else if (keyComb4ReturnHome.match(event)){
                    try {controller.returnHomeScreen();}
                    catch(Exception exception){exception.printStackTrace();}
                }
            });
        else if (guiState.equals(GuiState.AFTERLOGIN))
            primaryScene.setOnKeyPressed((KeyEvent event) -> {
                if (keyComb4PlayGame.match(event)) {
                    System.out.println("shortcut \"ctrl+P\" : Game Start");
                    try {
                        controller.setGameplayScreen();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                } else if (keyComb4CloseApplication.match(event)) {
                    try {
                        controller.confirmExit();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    System.out.println("shortcut \"ctrl+Q\" : Try Exit");
                } else if (keyComb4ReturnHome.match(event)){
                    try {controller.returnHomeScreen();}
                    catch(Exception exception){exception.printStackTrace();}
                }
            });
        else if(guiState.equals(GuiState.GAMEPLAY) || guiState.equals(GuiState.PROFILESETTING)
                || guiState.equals(GuiState.HELPSCREEN)||guiState.equals(GuiState.LEVELSELECTION)||
                guiState.equals(GuiState.GAMEPLAY_PAUSE))
            primaryScene.setOnKeyPressed((KeyEvent event) -> {
                if (keyComb4ReturnHome.match(event)) {
                    System.out.print("shortcut \"ctrl+H\" : ");
                    try {
                        controller.returnHomeScreen();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                } else if (keyComb4CloseApplication.match(event)) {
                    try {
                        controller.confirmExit();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    System.out.println("shortcut \"ctrl+Q\" : Try Exit");
                }
            });
        else if (guiState.equals(GuiState.GAMEPLAY_PLAYING))
                primaryScene.setOnKeyPressed((KeyEvent event) -> {
                    if (keyComb4ReturnHome.match(event)) {
                        System.out.print("shortcut \"ctrl+H\" : ");
                        try {
                            controller.returnHomeScreen();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }

                    } else if (keyComb4CloseApplication.match(event)) {
                        try {
                            controller.confirmExit();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        System.out.println("shortcut \"ctrl+Q\" : Try Exit");
                    } else if (keyComb4ReplayGame.match(event)) {
                        try {
                            controller.replayGame();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        System.out.println("shortcut \"ctrl+R\" : Restart the game on this level");
                    }
                });
            else if (guiState.equals(GuiState.GAMEFINISHED_CLEAR) || guiState.equals(GuiState.GAMEFINISHED_FAIL_NEXTLEVEL))
                primaryScene.setOnKeyPressed((KeyEvent event) -> {
                    if (keyComb4ReturnHome.match(event)) {
                        System.out.print("shortcut \"ctrl+H\" : ");
                        try {
                            controller.returnHomeScreen();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    } else if (keyComb4CloseApplication.match(event)) {
                        try {
                            controller.confirmExit();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        System.out.println("shortcut \"ctrl+Q\" : Try Exit");
                    } else if (keyComb4ReplayGame.match(event)) {
                        try {
                            controller.replayGame();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        System.out.println("shortcut \"ctrl+R\" : Restart the game on this level");
                    } else if (keyComb4NextLevel.match(event)) {
                        try {
                            controller.nextLevelPlay();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        System.out.println("shortcut \"ctrl+>\" : Next Level");
                    }
                });

    }



    public Stage getWindow() { return primaryStage; }
    public Scene getPrimaryScene() { return primaryScene; }

}
