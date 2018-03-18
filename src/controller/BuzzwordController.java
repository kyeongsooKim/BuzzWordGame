package controller;

import buzzword.AppTemplate;
import com.sun.org.apache.bcel.internal.classfile.Code;
import com.sun.xml.internal.bind.v2.runtime.property.Property;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import data.GameData;
import gui.*;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.effect.Shadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jdk.nashorn.api.scripting.JSObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.Option;
import java.awt.*;
import java.io.*;
import java.util.*;


/**
 * Created by kkyeo on 11/12/2016.
 */
public class BuzzwordController {

    private AppGUI gui; //shared reference to the main App GUI
    private GameData gamedata;
    private String preInputSaver;
    int totalPointsNumber;
    private final static int PointsFor3LenWords = 3;
    private final static int PointsFor4LenWords = 4;
    private final static int PointsFor5LenWords = 5;
    private final static int PointsFor6LenWords = 7;
    private final static int PointsFor7LenWords = 10;
    private final static int PointsForLongWords = 15; //8 자리 이상 단어


    public BuzzwordController(AppGUI gui, GameData gameData) {
        this.gui = gui;
        this.gamedata = gameData;
    }

    public AppGUI getGui(){
        return gui;
    }

    public GameData getGamedata(){
        return gamedata;
    }

    public void setHomeScreen() throws IOException{
        gui.getHomeScreen().initilizeToolBar(); //Homescreen toolbar을 로그인 전으로 리셋
        gui.getToolBar().getChildren().clear(); //gui toolbar부터 비우고
        gui.getToolBar().getChildren().setAll(gui.getHomeScreen().getToolBar().getChildren());
        gui.getBodyPane().getChildren().clear(); // gui bodypane 부터 비우고
        gui.getBodyPane().getChildren().add(gui.getHomeScreen().getHomeScreenPane());
        gui.setGuiState(AppGUI.GuiState.HOMESCREEN);
        gui.getHomeScreen().setupHandlers(this);
        System.out.println(gui.getGuiState());
    }

    public void returnHomeScreen() throws IOException{

        if (gui.getGuiState() == AppGUI.GuiState.HOMESCREEN)
        {
            gui.getHomeScreen().initilizeToolBar(); //Homescreen toolbar을 로그인 전으로 리셋
            gui.getToolBar().getChildren().clear(); //gui toolbar부터 비우고
            gui.getToolBar().getChildren().setAll(gui.getHomeScreen().getToolBar().getChildren());
            gui.getBodyPane().getChildren().clear(); // gui bodypane 부터 비우고
            gui.getBodyPane().getChildren().add(gui.getHomeScreen().getHomeScreenPane());
            gui.getHomeScreen().setupHandlers(this);
            gui.reActivateShortcut();
        }
        else if (gui.getGuiState() == AppGUI.GuiState.GAMEPLAY_PLAYING || gui.getGuiState() == AppGUI.GuiState.GAMEPLAY_PAUSE)
        {
            gui.getGameplayScreen().getTimeline().getKeyFrames().clear(); //thread 제거 !!!!!!
            gui.getHomeScreen().initilizeToolBar(); //Homescreen toolbar을 로그인 전으로 리셋
            gui.getHomeScreen().nextToolBar(gamedata, this);
            gui.getToolBar().getChildren().clear(); //gui toolbar부터 비우고
            gui.getToolBar().getChildren().setAll(gui.getHomeScreen().getToolBar().getChildren());
            gui.getBodyPane().getChildren().clear(); // gui bodypane 부터 비우고
            gui.getBodyPane().getChildren().add(gui.getHomeScreen().getHomeScreenPane());
            gui.getHomeScreen().setupHandlers(this);
            gui.setGuiState(AppGUI.GuiState.AFTERLOGIN);    // 상태 바뀔때마다
            gui.reActivateShortcut();                       // reActivate 항상 시켜준다!!
        }
        else {
            gui.getHomeScreen().initilizeToolBar(); //Homescreen toolbar을 로그인 전으로 리셋
            gui.getHomeScreen().nextToolBar(gamedata, this);
           gui.getToolBar().getChildren().clear(); //gui toolbar부터 비우고
            gui.getToolBar().getChildren().setAll(gui.getHomeScreen().getToolBar().getChildren());
            gui.getBodyPane().getChildren().clear(); // gui bodypane 부터 비우고
            gui.getBodyPane().getChildren().add(gui.getHomeScreen().getHomeScreenPane());
            gui.getHomeScreen().setupHandlers(this);
            gui.setGuiState(AppGUI.GuiState.AFTERLOGIN);    // 상태 바뀔때마다
            gui.reActivateShortcut();                       // reActivate 항상 시켜준다!!
        }
    }

    //로그 아웃, 모든 상태 HOMESCREEN 때로
    public void logout() throws IOException{
        gui.getHomeScreen().initilizeToolBar();
        gui.getToolBar().getChildren().clear();
        gui.getToolBar().getChildren().setAll(gui.getHomeScreen().getToolBar().getChildren());
        gui.getBodyPane().getChildren().clear();
        gui.getBodyPane().getChildren().add(gui.getHomeScreen().getHomeScreenPane());
        gui.setGuiState(AppGUI.GuiState.HOMESCREEN);
        gui.getHomeScreen().setupHandlers(this);
        gamedata.setLoggedin(false);
        gamedata.initializeGamedata();
        gui.reActivateShortcut();
        System.out.println("Logged off");
    }

    public void setupLevelSelectionScreen() throws IOException//select game Mode and level
    {
        gui.setLevelSelectionScreen(new LevelSelectionScreen()); // GameplayScreen 객체 생성
        gui.getLevelSelectionScreen().initLevelSelectionScreen(gamedata); // 초기화
        gui.getToolBar().getChildren().clear(); //기존 툴바 삭제
        gui.getToolBar().getChildren().addAll(gui.getLevelSelectionScreen().getToolBar().getChildren());
        gui.getBodyPane().getChildren().clear();
        gui.getBodyPane().getChildren().add(gui.getLevelSelectionScreen().getLevelSelectionPane());
        gui.setGuiState(AppGUI.GuiState.LEVELSELECTION);
        gui.getLevelSelectionScreen().setupHandlers(this); //핸들러처리
        gui.getLevelSelectionScreen().setLevelOption(this); //선택 가능한 버튼만 활성화
        gui.reActivateShortcut();
    }


    public void setupHelpScreen() throws IOException
    {
        gui.setHelpScreen(new Help()); // GameplayScreen 객체 생성
        gui.getHelpScreen().initHelpScreen(gamedata);
        gui.getToolBar().getChildren().clear(); //기존 툴바 삭제
        gui.getToolBar().getChildren().addAll(gui.getHelpScreen().getToolBar().getChildren());
        gui.getBodyPane().getChildren().clear();
        gui.getBodyPane().getChildren().add(gui.getHelpScreen().getHelpPane());
        gui.setGuiState(AppGUI.GuiState.HELPSCREEN);
        gui.getHelpScreen().setupHandlers(this);
        gui.reActivateShortcut();

    }

    public void setupProfileSettingsScreen() throws IOException
    {
        gui.setProfileSettingsScreen(new ProfileSettings()); // GameplayScreen 객체 생성
        gui.getProfileSettingsScreen().initProfileSettingsScreen(gamedata);
        gui.getToolBar().getChildren().clear(); //기존 툴바 삭제
        gui.getToolBar().getChildren().addAll(gui.getProfileSettingsScreen().getToolBar().getChildren());
        gui.getBodyPane().getChildren().clear();
        gui.getBodyPane().getChildren().add(gui.getProfileSettingsScreen().getProfileSettingsPane());
        gui.setGuiState(AppGUI.GuiState.PROFILESETTING);
        gui.getProfileSettingsScreen().setupHandlers(this);
        gui.reActivateShortcut();
    }

    public void setGameplayScreen() throws IOException//play game
    {
        gui.setGameplayScreen(new GameplayScreen()); // GameplayScreen 객체 생성
        gui.getGameplayScreen().initGameplayScreen(gamedata, this); // 초기화
        gui.getToolBar().getChildren().clear(); //기존 툴바 삭제
        gui.getToolBar().getChildren().addAll(gui.getGameplayScreen().getToolBar().getChildren());
        //gui.setToolBar(gui.getGameplayScreen().getToolBar()); //$$$$$$******은 왜 안돼는지 꼭 밝히자 ******$$$$$$
        gui.getBodyPane().getChildren().clear();
        gui.getBodyPane().getChildren().add(gui.getGameplayScreen().getGameComponentsPane());
        gui.setGuiState(AppGUI.GuiState.GAMEPLAY);
        gui.getGameplayScreen().setupHandlers(this); //핸들러처리
        gui.reActivateShortcut();
        gui.getGameplayScreen().freezeGameplayScreen();
        if (gamedata.getGameLevel() < 6)
            popupSimpleMessage(gamedata.getGameLevel()+"level \""+gamedata.getGameMode()+"\"mode \n"
                +"Start with \"PLAY NOW\" button below!", 5);
        else
            popupSimpleMessage("Minimum 4 length letters can be chosen on level "+gamedata.getGameLevel()+"\n"+
                    "Start with \"PLAY NOW\" button below!", 6);
    }

    // 게임 시작하면 Key input 가능하게 함.
    public void keyInputEnable()
    {
            Shadow borderGlow = new Shadow();
            borderGlow.setColor(javafx.scene.paint.Color.RED);
            borderGlow.setWidth(2);
            borderGlow.setHeight(2);
            HBox guessedLetter = gui.getGameplayScreen().getGuessedLetter();
            ArrayList<Label> guessedLetterUnit = gui.getGameplayScreen().getGuessedLetterUnit();
            Line diagnalLines[][] = gui.getGameplayScreen().getDiagnalLines();
            Line diagnalLines2[][] = gui.getGameplayScreen().getDiagnalLines2();
            Line vLines[][] = gui.getGameplayScreen().getvLines();
            Line hLines[][] = gui.getGameplayScreen().gethLines();
            Circle wordCircle[][] =   gui.getGameplayScreen().getWordCircle();
            StackPane wordUnit[][] =   gui.getGameplayScreen().getWordUnit();
            Label wordLabel[][] = gui.getGameplayScreen().getWordLabel();
            ArrayList<String> latestCollection = new ArrayList<String>();
            ArrayList<String> currentCollection = new ArrayList<String>();
            ArrayList<String> tempCollection = new ArrayList<String>();
            GridPane scoreBoard = gui.getGameplayScreen().getScoreBoard();
            Label totalPoints = gui.getGameplayScreen().getTotalPoints();


                gui.getPrimaryScene().setOnKeyTyped((KeyEvent event) ->{

                    if (gui.getGuiState() == AppGUI.GuiState.GAMEPLAY_PLAYING) //게임이 실제 플레이 상태일때만 먹힘/ pause 나 게임 play 버튼 누르기 전에는 안먹힘
                    {
                        totalPointsNumber = gui.getGameplayScreen().getTotalPointsNumber();

                        if (event.getCharacter().equals("\r")) //엔터 치면 싹 제거
                        {
                            StringBuilder strb = new StringBuilder("");
                            for(int k = 0; k < guessedLetterUnit.size();k++){
                                strb.append(guessedLetterUnit.get(k).getText());
                            }
                            String guessedWords = strb.toString();

                            Iterator<String> iterator = gamedata.getHiddenWordsList().iterator();
                            while (iterator.hasNext())
                            {
                                if (iterator.next().equals(guessedWords))
                                {
                                    iterator.remove(); //항목 제거
                                    gamedata.getAnswerList().add(guessedWords);
                                    switch (guessedWords.length())
                                    {
                                        case 3 : totalPointsNumber += PointsFor3LenWords;
                                            scoreBoard.add(new Label(String.valueOf(PointsFor3LenWords)),1,gamedata.getAnswerList().size()-1); break;
                                        case 4 : totalPointsNumber += PointsFor4LenWords;
                                            scoreBoard.add(new Label(String.valueOf(PointsFor4LenWords)),1,gamedata.getAnswerList().size()-1);break;
                                        case 5 : totalPointsNumber += PointsFor5LenWords;
                                            scoreBoard.add(new Label(String.valueOf(PointsFor5LenWords)),1,gamedata.getAnswerList().size()-1);break;
                                        case 6 : totalPointsNumber += PointsFor6LenWords;
                                            scoreBoard.add(new Label(String.valueOf(PointsFor6LenWords)),1,gamedata.getAnswerList().size()-1);break;
                                        case 7 : totalPointsNumber += PointsFor7LenWords;
                                            scoreBoard.add(new Label(String.valueOf(PointsFor7LenWords)),1,gamedata.getAnswerList().size()-1);break;
                                    }
                                    if (guessedWords.length() >= 8){
                                        scoreBoard.add(new Label(String.valueOf(PointsForLongWords)),gamedata.getAnswerList().size(),gamedata.getAnswerList().size()-1);
                                        totalPointsNumber += PointsForLongWords;
                                    }
                                    gui.getGameplayScreen().setTotalPointsNumber(totalPointsNumber);
                                    totalPoints.setText(String.valueOf(totalPointsNumber));
                                    scoreBoard.add(new Label(guessedWords), 0 ,gamedata.getAnswerList().size()-1);

                                }
                            }
                            guessedLetter.getChildren().clear();
                            guessedLetterUnit.clear();

                            for (int v = 0 ; v <4 ; v++){
                                for (int w = 0; w<4; w++){
                                    wordUnit[w][v].setId(v +","+ w +","+0);
                                    wordCircle[w][v].setEffect(null);
                                    if (w<3)
                                        hLines[v][w].setEffect(null);
                                    if (v<3)
                                        vLines[v][w].setEffect(null);
                                    if (v<3 && w <3){
                                        diagnalLines[v][w].setEffect(null);
                                        diagnalLines2[v][w].setEffect(null);
                                    }
                                }
                            }
                            latestCollection.clear();
                            tempCollection.clear();
                            currentCollection.clear();
                        }

                        String guess = event.getCharacter();
                        Boolean isCountedOnce = false;

                        if (guessedLetterUnit.size() < 1) //최초입력
                        {
                            for(int i = 0; i <4 ; i++){
                                for (int j = 0; j<4; j++){
                                    if(((Label)gui.getGameplayScreen().getWordLabel()[j][i]).getText().equals(guess))
                                    {
                                        ((Circle)gui.getGameplayScreen().getWordCircle()[j][i]).setEffect(borderGlow);
                                        ((StackPane)gui.getGameplayScreen().getWordUnit()[j][i]).setId(i+","+j+","+1);
                                        latestCollection.add(new String(j+","+i));

                                        if (guessedLetterUnit.size() < 1){
                                            gui.getGameplayScreen().getGuessedLetterUnit().add(new Label(guess));
                                            gui.getGameplayScreen().getGuessedLetter().getChildren().add(gui.getGameplayScreen().getGuessedLetterUnit().get(0));
                                        }
                                    }
                                }
                            }
                            preInputSaver = guess;
                        }
                        else //최초 입력 후 입력
                        {
                            for (int p = 0; p < 4; p++) {
                                for (int q = 0; q < 4; q++) {
                                    if (wordLabel[q][p].getText().equals(guess) && wordUnit[q][p].getId().charAt(4) == '0')//한번도 들리지않은 현재 입력 찾기
                                    {

                                        for (int r = 0; r < 4; r++) {
                                            for (int s = 0; s < 4; s++) {
                                                if (latestCollection.contains(new String(s+","+r)) && //이전에 지나갔던 녀석중
                                                        (((p + 1 == r || p - 1 == r) && (q + 1 == s || q - 1  == s)) || (Math.abs(p - r) <= 1 && Math.abs(q - s) <= 1))) //인접
                                                {
                                                    ((Circle)gui.getGameplayScreen().getWordCircle()[q][p]).setEffect(borderGlow);
                                                    ((StackPane)gui.getGameplayScreen().getWordUnit()[q][p]).setId(p+","+q+","+1);
                                                    currentCollection.add(wordUnit[q][p].getId());
                                                    tempCollection.add(new String(q+","+p));

                                                    if (isCountedOnce == false){
                                                        guessedLetterUnit.add(new Label(guess));
                                                        if (guessedLetterUnit.size() < 12)
                                                            guessedLetter.getChildren().add(guessedLetterUnit.get(guessedLetterUnit.size()-1));
                                                        isCountedOnce = true;
                                                    }
                                                    if (p + 1 == r && q == s)
                                                        vLines[p][q].setEffect(borderGlow);
                                                    else if (p - 1 == r&& q == s)
                                                        vLines[p-1][q].setEffect(borderGlow);
                                                    else if (p == r && q+ 1 == s)
                                                        hLines[p][q].setEffect(borderGlow);
                                                    else if (p == r && q -1 == s)
                                                        hLines[p][q-1].setEffect(borderGlow);
                                                    else if (p + 1 == r && q+ 1 == s)
                                                        diagnalLines2[p][q].setEffect(borderGlow);
                                                    else if (p - 1 == r && q -1 == s)
                                                        diagnalLines2[p-1][q-1].setEffect(borderGlow);
                                                    else if (p - 1 == r && q+ 1 == s)
                                                        diagnalLines[p-1][q].setEffect(borderGlow);
                                                    else if (p + 1 == r && q -1 == s)
                                                        diagnalLines[p][q-1].setEffect(borderGlow);
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                            if (isCountedOnce == true){
                                preInputSaver = guess;
                                latestCollection.clear();
                                latestCollection.addAll(tempCollection);
                                tempCollection.clear();
                                deletePath();
                                currentCollection.clear();
                            }
                            else { //인접한걸 찾지못하였을 경우 곳바로 싹 제거
                                guessedLetter.getChildren().clear();
                                guessedLetterUnit.clear();

                                for (int v = 0 ; v <4 ; v++){
                                    for (int w = 0; w<4; w++){
                                        wordUnit[w][v].setId(v +","+ w +","+0);
                                        wordCircle[w][v].setEffect(null);
                                        if (w<3)
                                            hLines[v][w].setEffect(null);
                                        if (v<3)
                                            vLines[v][w].setEffect(null);
                                        if (v<3 && w <3){
                                            diagnalLines[v][w].setEffect(null);
                                            diagnalLines2[v][w].setEffect(null);
                                        }
                                    }
                                }
                                latestCollection.clear();
                                tempCollection.clear();
                                currentCollection.clear();
                            }

                        }
                    }

                });

    }

    public void mouseDragDisable(){

            System.out.println("start!");
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++)
                    ((StackPane)(gui.getGameplayScreen().getWordUnit()[i][j])).setDisable(true);

            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++){
                    if (((StackPane)(gui.getGameplayScreen().getWordUnit()[i][j])).isDisable());

                }

    }

    public void deletePath(){
        Shadow borderGlow = new Shadow();
        borderGlow.setColor(javafx.scene.paint.Color.RED);
        borderGlow.setWidth(2);
        borderGlow.setHeight(2);
        HBox guessedLetter = gui.getGameplayScreen().getGuessedLetter();
        ArrayList<Label> guessedLetterUnit = gui.getGameplayScreen().getGuessedLetterUnit();
        Line diagnalLines[][] = gui.getGameplayScreen().getDiagnalLines();
        Line diagnalLines2[][] = gui.getGameplayScreen().getDiagnalLines2();
        Line vLines[][] = gui.getGameplayScreen().getvLines();
        Line hLines[][] = gui.getGameplayScreen().gethLines();
        Circle wordCircle[][] =   gui.getGameplayScreen().getWordCircle();
        StackPane wordUnit[][] =   gui.getGameplayScreen().getWordUnit();
        Label wordLabel[][] = gui.getGameplayScreen().getWordLabel();
        int savePassInfo[][] = new int[4][4];
        ArrayList<String> latestCollection = new ArrayList<String>();
        ArrayList<String> tempCollection = new ArrayList<String>();

        for (int v = 0 ; v <4 ; v++){
            for (int w = 0; w<4; w++){
                savePassInfo[w][v] = wordUnit[w][v].getId().charAt(4);
                wordUnit[w][v].setId(w +","+ v +","+0);
                wordCircle[w][v].setEffect(null);
                if (w<3)
                    hLines[v][w].setEffect(null);
                if (v<3)
                    vLines[v][w].setEffect(null);
                if (v<3 && w <3){
                    diagnalLines[v][w].setEffect(null);
                    diagnalLines2[v][w].setEffect(null);
                }
            }

        }

        for (int i = guessedLetterUnit.size(); i > 1; i-- )
        {
            for (int p = 0; p < 4; p++)
            {
                for (int q = 0; q < 4; q++) {
                    if (wordLabel[q][p].getText().equals(guessedLetterUnit.get(i-1).getText()) && savePassInfo[q][p] == '1')
                    {
                        //savePassInfo 의 1 인 후보 놈들 중에서 이 전 순환에서 wordUnit[q][p].getId().charAt(4) == '1' 인 녀석만 채택.
                        if ((wordUnit[q][p].getId().charAt(4) == '1' && latestCollection.contains(new String(q+","+p))) || i == guessedLetterUnit.size())
                        {
                            ((Circle)gui.getGameplayScreen().getWordCircle()[q][p]).setEffect(borderGlow);
                            ((StackPane)gui.getGameplayScreen().getWordUnit()[q][p]).setId(q+","+p+","+1);
                            for (int r = 0; r < 4; r++) {
                                for (int s = 0; s < 4; s++) {
                                    if ( wordLabel[s][r].getText().equals(guessedLetterUnit.get(i-2).getText())  &&  savePassInfo[s][r] == '1'
                                            && (((p + 1 == r || p - 1 == r) && (q + 1 == s || q - 1  == s)) || (Math.abs(p - r) <= 1 && Math.abs(q - s) <= 1))){

                                        ((Circle)gui.getGameplayScreen().getWordCircle()[s][r]).setEffect(borderGlow);
                                        ((StackPane)gui.getGameplayScreen().getWordUnit()[s][r]).setId(s+","+r+","+1);
                                        tempCollection.add(new String(s+","+r));

                                        if (p + 1 == r && q == s)
                                            vLines[p][q].setEffect(borderGlow);
                                        else if (p - 1 == r&& q == s)
                                            vLines[p-1][q].setEffect(borderGlow);
                                        else if (p == r && q+ 1 == s)
                                            hLines[p][q].setEffect(borderGlow);
                                        else if (p == r && q -1 == s)
                                            hLines[p][q-1].setEffect(borderGlow);
                                        else if (p + 1 == r && q+ 1 == s)
                                            diagnalLines2[p][q].setEffect(borderGlow);
                                        else if (p - 1 == r && q -1 == s)
                                            diagnalLines2[p-1][q-1].setEffect(borderGlow);
                                        else if (p - 1 == r && q+ 1 == s)
                                            diagnalLines[p-1][q].setEffect(borderGlow);
                                        else if (p + 1 == r && q -1 == s)
                                            diagnalLines[p][q-1].setEffect(borderGlow);
                                    }

                                }
                            }
                        }
                    }

                }
            }
            latestCollection.clear();
            latestCollection.addAll(tempCollection);
            tempCollection.clear();
        }
    }

    public void gameCleared(){
        getGui().setGuiState(AppGUI.GuiState.GAMEFINISHED_CLEAR);
        gui.reActivateShortcut();
        System.out.println("Game Cleared!");
        String mode = new String(gamedata.getGameMode());
        long maxLevel = gamedata.getGameLevel();

        switch (mode){
            case "English Dictionary" : maxLevel = gamedata.getMaxLevel4Edic(); break;
            case "First Name" : maxLevel = gamedata.getmaxLevel4FirstName(); break;
            case "Last Name" : maxLevel = gamedata.getmaxLevel4LastName(); break;
            case "Shakespeare" : maxLevel = gamedata.getmaxLevel4Shakespeare();
        }

        // maxLevel 깻을 경우만 save button 띄워야됨
        if (maxLevel > gamedata.getGameLevel())
        {
            try{
                gui.getGameplayScreen().getNextLevel().setVisible(true);
                gui.getGameplayScreen().getNextLevel().setDisable(false);

            }
            catch(Exception exception)
            {
                exception.printStackTrace();
            }
        }
        else if (gamedata.getGameLevel() < 8)
        {

            try{
                    gui.getGameplayScreen().getNextLevel().setVisible(true);
                    gui.getGameplayScreen().getNextLevel().setDisable(false);
                    gui.getGameplayScreen().getSave().setVisible(true);       //Save Button 일단 구현 안한다.
                    gui.getGameplayScreen().getSave().setDisable(false);
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(new FileReader("././saved/"+ gamedata.getID()+ ".json"));
                    JSONObject jsonObject = (JSONObject)obj;

                    //새 모드 열림
                    jsonObject.replace("Max Level on "+gamedata.getGameMode(),gamedata.getGameLevel(),gamedata.getGameLevel()+1);

                    try(FileWriter file = new FileWriter("././saved/"+ gamedata.getID() +".json")) //왜 꼭 이렇게 해야되는가? ㅡㅡ? 나중에 이유 찾자
                    {
                        file.write(jsonObject.toJSONString());
                        System.out.println("New level in this mode is open.");

                    }
                    gamedata.renewUserInfo(); //변경된사항 그대로 갱신.

            }
            catch (Exception e)
            {

            }

        }
        popupResultMessage(true);
        gui.reActivateShortcut(); //상태 바뀌면 shortcut 재설정


    }

    public void gameFailed()
    {

        System.out.println("Game Failed!");
        String mode = new String(gamedata.getGameMode());
        long maxLevel = gamedata.getGameLevel();

        switch (mode){
            case "English Dictionary" : maxLevel = gamedata.getMaxLevel4Edic(); break;
            case "First Name" : maxLevel = gamedata.getmaxLevel4FirstName(); break;
            case "Last Name" : maxLevel = gamedata.getmaxLevel4LastName(); break;
            case "Shakespeare" : maxLevel = gamedata.getmaxLevel4Shakespeare();
        }

        //클리어 하지 못해도 저장되어있는 maxLevel 까지 시도 할 수는 있다.
        if (maxLevel > gamedata.getGameLevel())
        {
            System.out.println("But you can still try next level! Since you have already reached level" + maxLevel + "before");
            gui.getGameplayScreen().getNextLevel().setVisible(true);
            gui.getGameplayScreen().getNextLevel().setDisable(false);
            getGui().setGuiState(AppGUI.GuiState.GAMEFINISHED_FAIL_NEXTLEVEL);

        }
        else
        {
            getGui().setGuiState(AppGUI.GuiState.GAMEFINISHED_FAIL);
        }
        popupResultMessage(false);
        gui.reActivateShortcut(); //상태 바뀌면 shortcut 재설정

    }

    public void nextLevelPlay() throws IOException{
        gamedata.setGameLevel(gamedata.getGameLevel() + 1);
        gui.setGameplayScreen(new GameplayScreen()); // GameplayScreen 객체 생성
        gui.getGameplayScreen().initGameplayScreen(gamedata, this); // 초기화
       // gui.getToolBar().getChildren().clear(); //기존 툴바 삭제
      //  gui.getToolBar().getChildren().addAll(gui.getGameplayScreen().getToolBar().getChildren());
        gui.getBodyPane().getChildren().clear();
        gui.getBodyPane().getChildren().add(gui.getGameplayScreen().getGameComponentsPane());
        gui.setGuiState(AppGUI.GuiState.GAMEPLAY);
        gui.getGameplayScreen().setupHandlers(this); //핸들러처리
        gui.reActivateShortcut();
        gui.getGameplayScreen().freezeGameplayScreen();
        if (gamedata.getGameLevel() < 6)
            popupSimpleMessage(gamedata.getGameLevel()+"level \""+gamedata.getGameMode()+"\"mode \n"
                    +"Start with \"PLAY NOW\" button below!", 5);
        else
            popupSimpleMessage("Minimum 4 length letters can be chosen on level "+gamedata.getGameLevel()+"\n"+
                    "Start with \"PLAY NOW\" button below!", 6);
    }

    public void replayGame() throws IOException{
        gui.getGameplayScreen().getTimeline().getKeyFrames().clear(); //thread 제거 !!!!!!
        gui.setGameplayScreen(new GameplayScreen()); // GameplayScreen 객체 생성 //일단 reinitgameplayscreen 함수가 미완성됬기에 새 객체 형성하자
        gui.getGameplayScreen().initGameplayScreen(gamedata, this); // 초기화
        gui.getBodyPane().getChildren().clear();
        gui.getBodyPane().getChildren().add(gui.getGameplayScreen().getGameComponentsPane());
        gui.setGuiState(AppGUI.GuiState.GAMEPLAY_PLAYING);
        gui.getGameplayScreen().setupHandlers(this); //핸들러처리
        gui.reActivateShortcut();
        FileInputStream input = new FileInputStream("././resources/images/pause.png");
        Image image = new Image(input);
        Tooltip pauseTip = new Tooltip("Pause the game");
        pauseTip.setStyle("-fx-font-size:20");
        gui.getGameplayScreen().getPlay().setTooltip(pauseTip);
        gui.getGameplayScreen().getPlay().setGraphic(new ImageView((image)));
        //unvisible & disable 이였던 요소들 수정
        gui.getGameplayScreen().getReplay().setDisable(false); //replay 할 수 있도록
        gui.getGameplayScreen().getReplay().setVisible(true);
        gui.getGameplayScreen().getTotal().setVisible(true);
        gui.getGameplayScreen().getScoreBoard().setVisible(true);
        gui.getGameplayScreen().getTotalPoints().setVisible(true);
        gui.getGameplayScreen().getFoundWordsBackground().setStyle("-fx-background-color :#006600");
        gui.getGameplayScreen().getSave().setVisible(false);
        gui.getGameplayScreen().getSave().setDisable(true);
        gui.getGameplayScreen().getNextLevel().setVisible(false);
        gui.getGameplayScreen().getNextLevel().setDisable(true);
        popupSimpleMessage("Restart the game!",1);
    }

    public void playGame() throws IOException{
        gui.getGameplayScreen().unfreezeGameplayScreen();
        FileInputStream input = new FileInputStream("././resources/images/pause.png");
        Image image = new Image(input);
        Tooltip pauseTip = new Tooltip("Pause the game");
        pauseTip.setStyle("-fx-font-size:20");
        gui.getGameplayScreen().getPlay().setTooltip(pauseTip);
        gui.getGameplayScreen().getPlay().setGraphic(new ImageView((image)));//unvisible & disable 이였던 요소들 수정
        gui.getGameplayScreen().getReplay().setDisable(false); //replay 할 수 있도록
        gui.getGameplayScreen().getReplay().setVisible(true);
        gui.getGameplayScreen().getScoreBoard().setVisible(true);
        gui.getGameplayScreen().getTotal().setVisible(true);
        gui.getGameplayScreen().getTotalPoints().setVisible(true);
        gui.getGameplayScreen().getFoundWordsBackground().setStyle("-fx-background-color :#006600");
        gui.setGuiState(AppGUI.GuiState.GAMEPLAY_PLAYING);//게임진행중 상태로 변경
        gui.reActivateShortcut(); //그에 맞게 shortcut 다시 설정 (사실상 shortcut 변화는 없음)
        System.out.println("Play game. Start countdown.");
    }

    public void resumeGame() throws IOException{
        gui.getGameplayScreen().unfreezeGameplayScreen();
        FileInputStream input = new FileInputStream("././resources/images/pause.png");
        Image image = new Image(input);
        Tooltip pauseTip = new Tooltip("Pause the game");
        pauseTip.setStyle("-fx-font-size:20");
        gui.getGameplayScreen().getPlay().setTooltip(pauseTip);
        gui.getGameplayScreen().getPlay().setGraphic(new ImageView((image)));
        //unvisible & disable 이였던 요소들 수정
        gui.getGameplayScreen().getReplay().setDisable(false);
        gui.getGameplayScreen().getReplay().setVisible(true);
        gui.getGameplayScreen().getScoreBoard().setVisible(true);
        gui.getGameplayScreen().unfreezeGameplayScreen();
        gui.getGameplayScreen().getTotal().setVisible(true);
        gui.getGameplayScreen().getTotalPoints().setVisible(true);
        gui.getGameplayScreen().getFoundWordsBackground().setStyle("-fx-background-color: #006600");
        gui.setGuiState(AppGUI.GuiState.GAMEPLAY_PLAYING);//게임진행중 상태로 변경
        gui.reActivateShortcut();
        System.out.println("Resume game");
    }

    public void pauseGame() throws IOException{
        gui.getGameplayScreen().freezeGameplayScreen();
        FileInputStream input = new FileInputStream("././resources/images/start.png");
        Image image = new Image(input);
        Tooltip resumeTip = new Tooltip("Resume the game");
        resumeTip.setStyle("-fx-font-size:20");
        gui.getGameplayScreen().getPlay().setTooltip(resumeTip);
        gui.getGameplayScreen().getPlay().setGraphic(new ImageView((image)));
        //unvisible & disable 이였던 요소들 수정
        gui.getGameplayScreen().getReplay().setDisable(true);
        gui.getGameplayScreen().getReplay().setVisible(false);
        gui.getGameplayScreen().getScoreBoard().setVisible(false);
        gui.getGameplayScreen().getTotal().setVisible(false);
        gui.getGameplayScreen().getTotalPoints().setVisible(false);
        gui.getGameplayScreen().getFoundWordsBackground().setStyle("-fx-background-color :#003300");
        gui.setGuiState(AppGUI.GuiState.GAMEPLAY_PAUSE);//게임진행중 상태로 변경
        gui.reActivateShortcut();
        System.out.println("Pause game");
        popupSimpleMessage("Game has been paused.\nResume it with \"Play button\" below.",5);

        for (int v = 0 ; v <4 ; v++){
            for (int w = 0; w<4; w++){
                gui.getGameplayScreen().getWordUnit()[w][v].setId(v +","+ w +","+0);
                gui.getGameplayScreen().getWordCircle()[w][v].setEffect(null);
                if (w<3)
                    gui.getGameplayScreen().gethLines()[v][w].setEffect(null);
                if (v<3)
                    gui.getGameplayScreen().getvLines()[v][w].setEffect(null);
                if (v<3 && w <3){
                    gui.getGameplayScreen().getDiagnalLines()[v][w].setEffect(null);
                    gui.getGameplayScreen().getDiagnalLines2()[v][w].setEffect(null);
                }
            }
        }
        gui.getGameplayScreen().getGuessedLetterUnit().clear();
        gui.getGameplayScreen().getGuessedLetter().getChildren().clear();
    }

    public void popLoginUp() throws IOException{
        new Login(gamedata);

        if(gamedata.getLoggedin() == true)
        {
            gui.getHomeScreen().nextToolBar(gamedata,this);
            gui.getToolBar().getChildren().clear(); //gui toolbar부터 비우고
            gui.getToolBar().getChildren().setAll(gui.getHomeScreen().getToolBar().getChildren());
            gui.setGuiState(AppGUI.GuiState.AFTERLOGIN);
            gui.reActivateShortcut();
        }

      //  gui.getHomeScreen().getLoginBt().setDisable(true);            // parentStage 자체를 비활성화시켜서 필요없는 두 줄
      //  gui.getHomeScreen().getCreateProfileBt().setDisable(true);    //
    }

    public void popCreateProfileUp() throws IOException
    {
        new CreateProfile(gamedata);
        if(gamedata.getcreatedProfile() == true)
        {
            gui.getHomeScreen().nextToolBar(gamedata,this);
            gui.getToolBar().getChildren().clear(); //gui toolbar부터 비우고
            gui.getToolBar().getChildren().setAll(gui.getHomeScreen().getToolBar().getChildren());
            gui.setGuiState(AppGUI.GuiState.AFTERLOGIN);
            gui.reActivateShortcut();
        }
    }

    public void renewHighestPoints(){
        switch (gamedata.getGameMode()){
            case "English Dictionary" :
                if (gamedata.getMaxPoints4EachEdic()[(int)(gamedata.getGameLevel() - 1)] < gui.getGameplayScreen().getTotalPointsNumber())
                {
                    try {
                        //새로운 점수 갱신
                        JSONParser parser = new JSONParser();
                        Object obj = parser.parse(new FileReader("././saved/" + gamedata.getID() + ".json"));
                        JSONObject jsonObject = (JSONObject) obj;
                        gamedata.getMaxPoints4EachEdic()[(int)gamedata.getGameLevel()-1] = gui.getGameplayScreen().getTotalPointsNumber();
                        JSONArray list1 = new JSONArray();


                        for (int i = 0; i < 8 ; i++){
                            list1.add(gamedata.getMaxPoints4EachEdic()[i]);
                           // System.out.println(list1.get(i));
                        }

                        jsonObject.put("Max Points of English Dictionary", list1);

                        try(FileWriter file = new FileWriter("././saved/"+ gamedata.getID() +".json")) //왜 꼭 이렇게 해야되는가? ㅡㅡ? 나중에 이유 찾자
                        {
                            file.write(jsonObject.toJSONString());
                            System.out.println("Highest points has saved in English Dictionary Mode");
                        }
                        gamedata.renewUserInfo(); //변경된사항 그대로 갱신.
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                break;
            case "First Name" :
                if (gamedata.getMaxPoints4EachFirstName()[(int)(gamedata.getGameLevel() - 1)] < gui.getGameplayScreen().getTotalPointsNumber())
                {
                    try {
                        JSONParser parser = new JSONParser();
                        Object obj = parser.parse(new FileReader("././saved/" + gamedata.getID() + ".json"));
                        JSONObject jsonObject = (JSONObject) obj;
                        gamedata.getMaxPoints4EachFirstName()[(int)gamedata.getGameLevel()-1] = gui.getGameplayScreen().getTotalPointsNumber();


                        JSONArray list2 = new JSONArray();

                        for (int i = 0; i < 8 ; i++)
                            list2.add(gamedata.getMaxPoints4EachFirstName()[i]);
                        jsonObject.put("Max Points of First Name", list2);

                        try(FileWriter file = new FileWriter("././saved/"+ gamedata.getID() +".json")) //왜 꼭 이렇게 해야되는가? ㅡㅡ? 나중에 이유 찾자
                        {
                            file.write(jsonObject.toJSONString());
                            System.out.println("Highest points has saved in First Name Mode");
                        }
                        gamedata.renewUserInfo(); //변경된사항 그대로 갱신.
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                break;
            case "Last Name" :
                if (gamedata.getMaxPoints4EachLastName()[(int)(gamedata.getGameLevel() - 1)] < gui.getGameplayScreen().getTotalPointsNumber())
                {
                    try {
                        //새로운 점수 갱신
                        JSONParser parser = new JSONParser();
                        Object obj = parser.parse(new FileReader("././saved/" + gamedata.getID() + ".json"));
                        JSONObject jsonObject = (JSONObject) obj;
                        gamedata.getMaxPoints4EachLastName()[(int)gamedata.getGameLevel()-1] = gui.getGameplayScreen().getTotalPointsNumber();

                        JSONArray list3 = new JSONArray();

                        for (int i = 0; i <8 ; i++)
                            list3.add(gamedata.getMaxPoints4EachLastName()[i]);
                        jsonObject.put("Max Points of Last Name", list3);

                        try(FileWriter file = new FileWriter("././saved/"+ gamedata.getID() +".json")) //왜 꼭 이렇게 해야되는가? ㅡㅡ? 나중에 이유 찾자
                        {
                            file.write(jsonObject.toJSONString());
                            System.out.println("Highest points has saved in Last Name Mode");
                        }
                        gamedata.renewUserInfo(); //변경된사항 그대로 갱신.
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                break;
            case "Shakespeare" :
                if (gamedata.getMaxPoints4EachShakespeare()[(int)(gamedata.getGameLevel() - 1)] < gui.getGameplayScreen().getTotalPointsNumber())
                {
                    try {
                        //새로운 점수 갱신
                        JSONParser parser = new JSONParser();
                        Object obj = parser.parse(new FileReader("././saved/" + gamedata.getID() + ".json"));
                        JSONObject jsonObject = (JSONObject) obj;
                        gamedata.getMaxPoints4EachShakespeare()[(int)gamedata.getGameLevel()-1] = gui.getGameplayScreen().getTotalPointsNumber();

                        JSONArray list4 = new JSONArray();

                        for (int i = 0; i < 8 ; i++)
                            list4.add(gamedata.getMaxPoints4EachShakespeare()[i]);

                        jsonObject.put("Max Points of Shakespeare", list4);

                        try(FileWriter file = new FileWriter("././saved/"+ gamedata.getID() +".json")) //왜 꼭 이렇게 해야되는가? ㅡㅡ? 나중에 이유 찾자
                        {
                            file.write(jsonObject.toJSONString());
                            System.out.println("Highest points has saved in Shakespeare Mode");
                        }
                        gamedata.renewUserInfo(); //변경된사항 그대로 갱신.
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                break;
        }
    }

    public boolean isHighestPoints(){
        switch (gamedata.getGameMode()){
            case "English Dictionary" :{
                if (gamedata.getMaxPoints4EachEdic()[(int)(gamedata.getGameLevel() - 1)] < gui.getGameplayScreen().getTotalPointsNumber())
                    return true;
                break;
            }
            case "First Name" :{
                if (gamedata.getMaxPoints4EachFirstName()[(int)(gamedata.getGameLevel() - 1)] < gui.getGameplayScreen().getTotalPointsNumber())
                    return true;
                break;
            }
            case "Last Name" :
                if (gamedata.getMaxPoints4EachLastName()[(int)(gamedata.getGameLevel() - 1)] < gui.getGameplayScreen().getTotalPointsNumber())
                    return true;
                break;
            case "Shakespeare" :
                if (gamedata.getMaxPoints4EachShakespeare()[(int)(gamedata.getGameLevel() - 1)] < gui.getGameplayScreen().getTotalPointsNumber())
                    return true;
                break;
        }
        return false;

    }

    public long getHighestPoints(){
        switch (gamedata.getGameMode()){
            case "English Dictionary" :
                return gamedata.getMaxPoints4EachEdic()[(int)(gamedata.getGameLevel() - 1)];
            case "First Name" :
                return gamedata.getMaxPoints4EachFirstName()[(int)(gamedata.getGameLevel() - 1)];
            case "Last Name" :
                return gamedata.getMaxPoints4EachLastName()[(int)(gamedata.getGameLevel() - 1)];
            case "Shakespeare" :
                return gamedata.getMaxPoints4EachShakespeare()[(int)(gamedata.getGameLevel() - 1)];
        }
        return 0;
    }

    public void confirmLogout() throws IOException{
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.setHeaderText(null);
        alert.setContentText("Are you sure to log out?");
        alert.getDialogPane().setStyle("-fx-font-size : 18; ");

        FileInputStream input = new FileInputStream("././resources/images/logout.png");
        alert.setGraphic(new ImageView(new Image(input)));

        ButtonType buttonLogout = new ButtonType("Yes", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().set(0, buttonLogout);
        alert.getButtonTypes().set(1, buttonNo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonLogout){
            logout();
        }
        else
        {
            alert.close();
        }
    }

    public void confirmExit() throws IOException{
        if (gui.getGuiState().equals(AppGUI.GuiState.GAMEPLAY_PLAYING))
        {
            gui.getGameplayScreen().freezeGameplayScreen();
            gui.getGameplayScreen().getScoreBoard().setVisible(false);
            gui.getGameplayScreen().getTotal().setVisible(false);
            gui.getGameplayScreen().getTotalPoints().setVisible(false);
            gui.getGameplayScreen().getFoundWordsBackground().setStyle("-fx-background-color :#003300");
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.setHeaderText(null);
        alert.setContentText("Are you sure to exit the application?");
        alert.getDialogPane().setStyle("-fx-font-size : 18; ");

        FileInputStream input = new FileInputStream("././resources/images/exit2.png");
        alert.setGraphic(new ImageView(new Image(input)));

        ButtonType buttonExit = new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().set(0,buttonExit);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonExit){
            gui.getWindow().close();
        }
        else
        {
            if (gui.getGuiState().equals(AppGUI.GuiState.GAMEPLAY_PLAYING))
            {
                gui.getGameplayScreen().unfreezeGameplayScreen();
                gui.getGameplayScreen().getScoreBoard().setVisible(true);
                gui.getGameplayScreen().getTotal().setVisible(true);
                gui.getGameplayScreen().getTotalPoints().setVisible(true);
                gui.getGameplayScreen().getFoundWordsBackground().setStyle("-fx-background-color :#006600");
            }
            alert.close();
        }

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

    public void popupResultMessage(Boolean success) {
        Stage resultStage = new Stage();
        resultStage.initStyle(StageStyle.TRANSPARENT);
        VBox wholePanel = new VBox();
        Scene resultScene = new Scene(wholePanel,470,200);

        Label Result = new Label();
        Label MaximumPoints = new Label();
        Label CurrentPoints = new Label();
        Result.setStyle("-fx-font-size : 18; ");
        MaximumPoints.setStyle("-fx-font-size : 20; -fx-font-fill :  RED;");
        CurrentPoints.setStyle("-fx-font-size : 25;-fx-font-fill :  RED ");
        Button okBt = new Button("   Close   ");
        okBt.setStyle("-fx-font-size : 16; ");
        okBt.setOnMouseClicked(e->{resultStage.close();});
        okBt.setAlignment(Pos.CENTER_RIGHT);
        wholePanel.getChildren().addAll(Result,CurrentPoints,MaximumPoints, okBt);

        if (isHighestPoints() && success)
        {
            if(gamedata.getGameLevel() == 8){
                Result.setText("Congratulations! You've cleared the last level!");
                CurrentPoints.setText("Total Points(Highest so far) : " + gui.getGameplayScreen().getTotalPointsNumber());
                wholePanel.getChildren().remove(2);
                renewHighestPoints(); //점수 갱신
            }
            else{
                Result.setText("Level clear! and You got highest points on this level!");
                CurrentPoints.setText("Total Points : " + gui.getGameplayScreen().getTotalPointsNumber());
                wholePanel.getChildren().remove(2);
                renewHighestPoints(); //점수 갱신
            }

        }
        else if (success && (!isHighestPoints()))
        {
            if (gamedata.getGameLevel() == 8){
                Result.setText("Congratulations! You've cleared the last level!");
                CurrentPoints.setText("Total Points : "+gui.getGameplayScreen().getTotalPointsNumber());
                MaximumPoints.setText("(Highest points on this level : "+ getHighestPoints() +")");
            }
            else{
                Result.setText("Level clear!");
                CurrentPoints.setText("Total Points : "+gui.getGameplayScreen().getTotalPointsNumber());
                MaximumPoints.setText("(Highest points on this level : "+ getHighestPoints() +")");
            }

        }
        else if ((!success) && isHighestPoints()) {
            Result.setText("Mission Failure.. But you got highest points on this level!");
            CurrentPoints.setText("Total Points : " + gui.getGameplayScreen().getTotalPointsNumber());
            wholePanel.getChildren().remove(2);
            renewHighestPoints(); //점수 갱신
        }
        else if (!success && !isHighestPoints())
        {
            Result.setText("Mission Failure..");
            CurrentPoints.setText("Total Points : "+gui.getGameplayScreen().getTotalPointsNumber());
            MaximumPoints.setText("(Highest points on this level : "+ getHighestPoints() +")");

        }


        wholePanel.setSpacing(10);
        wholePanel.setAlignment(Pos.CENTER);
        wholePanel.setPadding(new Insets(10,10,10,10));
        resultScene.setFill(null);
        resultScene.setOnKeyPressed(e->{
            if (e.getCode().equals(KeyCode.ENTER)||e.getCode().equals(KeyCode.ESCAPE))
                resultStage.close();
        });

        resultStage.setScene(resultScene);
        resultStage.show();                        // parentStage 비활성화시킴.
    }
}
