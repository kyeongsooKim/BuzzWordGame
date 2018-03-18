package gui;

import controller.BuzzwordController;
import data.GameData;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Shadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

/**
 * Created by kkyeo on 11/10/2016.
 */
public class GameplayScreen {

    private final static int PointsFor3LenWords = 3;
    private final static int PointsFor4LenWords = 4;
    private final static int PointsFor5LenWords = 5;
    private final static int PointsFor6LenWords = 7;
    private final static int PointsFor7LenWords = 10;
    private final static int PointsForLongWords = 15; //8 자리 이상 단어
    private final static int SIZE = 4;
    private static final Integer countDownTime = 60 + 1;
    private int timeSeconds;
    private Timeline timeline;
    private GridPane alphabetArea; // area for alphabets창조절시 알파벳 4x4유지해야 하므로 gridPane
    private StackPane alphabeticAreaStackPane;
    private GridPane gameComponentsPane;
    private VBox toolBar;
    private Pane modePane;
    private Pane levelPane;
    private Button profileLabel;
    private Button homeBt;

    // each buttons for playing /replaying/next LEvel/ save
    private Label play;
    private Label replay;
    private Label nextLevel;
    private Label save;

    private HBox buttonCollection;
    private HBox targetBox;
    private Label targetPoints;
    private Label remainingTime;

    private Circle[][] wordCircle = new Circle[SIZE][SIZE];
    private StackPane[][] wordUnit = new StackPane[SIZE][SIZE];
    private Label[][] wordLabel = new Label[SIZE][SIZE];
    private char[][] words;

    private String preIDSaver = new String();
    private Line [][] hLines = new Line[4][3];
    private Line [][] vLines = new Line[3][4];
    private Line [][] diagnalLines = new Line[3][3];
    private Line [][] diagnalLines2 = new Line[3][3];

    private HBox guessedLetter;           // 마우스 드레그할때 실시간으로 마우스 지나간 알파벳 뛰우는 HBox
    private ArrayList<Label> guessedLetterUnit = new ArrayList<Label>();
    private GridPane totalPointsPane;     // 합계점수 뛰우는 pane
    private Label total;                  // total 글씨 띄우는 라벨
    private Label totalPoints;            // 사용자가 얻은 total points 실시간으로 뛰울 라밸 -(*)
    private int totalPointsNumber = 0;        // total points 실제 담는 int 변수
    private Label totalPointsBackground;  // total Points background

    private VBox resultPanel;          //찾은 단어들, total 점수 뛰우는 최종 틀
    private StackPane scoreBoardCast;
    private GridPane scoreBoard;          //scoreboard

    private ScrollPane scoreBoardPane;    //scoreboardpane
    private ArrayList<Label> foundWords = new ArrayList<Label>();
    private Label foundWordsBackground;   //찾은 단어들 뛰울 배경




     public void initGameplayScreen(GameData gameData, BuzzwordController controller) {

        alphabetArea = new GridPane();
        alphabeticAreaStackPane = new StackPane();
        toolBar = new VBox();
        gameComponentsPane = new GridPane();
        profileLabel = new ToolbarButton("  "+gameData.getID() + "  ◀");
        Tooltip logoutTip = new Tooltip("Hi "+gameData.getID() +". You can log out by clicking this button.\n");
        logoutTip.setStyle("-fx-font-size:20");
        profileLabel.setTooltip(logoutTip);

        homeBt = new ToolbarButton("  Home");
        Tooltip homeBtTip = new Tooltip("Go back to HOME menu.");
        homeBtTip.setStyle("-fx-font-size:20");
        homeBt.setTooltip(homeBtTip);

        remainingTime = new Label();
        scoreBoard = new GridPane();

        guessedLetter = new HBox();

        Label mode = new Label(" GameMode : " + gameData.getGameMode());
        mode.setStyle(" -fx-text-fill: #111111;");
        mode.setFont(new Font("Cambria", 20));
        mode.setAlignment(Pos.CENTER_RIGHT);
        mode.setPrefSize(400, 30);
        modePane = new Pane(mode);
        modePane.setPrefSize(400, 30);

        Label level = new Label("LEVEL : " + String.valueOf(gameData.getGameLevel()));
        level.setStyle("-fx-text-fill : white; -fx-font-size:30 ;");
        level.setAlignment(Pos.CENTER_RIGHT);
        level.setPrefSize(400, 30);
        levelPane = new Pane(level);
        levelPane.setPrefSize(400, 30);

        try{
            play = new Label();
            FileInputStream input = new FileInputStream("././resources/images/startnow.png");
            Image image = new Image(input);
            Tooltip playTip = new Tooltip("Start game. \nYou will have 45seconds to get the target points");
            playTip.setStyle("-fx-font-size:20");
            play.setTooltip(playTip);
            play.setGraphic(new ImageView((image)));

            replay = new Label();
            FileInputStream input2 = new FileInputStream("././resources/images/replay.png");
            Image image2 = new Image(input2);
            Tooltip replayTip = new Tooltip("Give up this game and restart");
            replayTip.setStyle("-fx-font-size:20");
            replay.setTooltip(replayTip);
            replay.setGraphic(new ImageView((image2)));
            replay.setDisable(true);
            replay.setVisible(false);

            nextLevel = new Label();
            FileInputStream input3 = new FileInputStream("././resources/images/nextlevel.png");
            Image image3 = new Image(input3);
            Tooltip nextLevelTip = new Tooltip("You can move on nextLevel");
            nextLevelTip.setStyle("-fx-font-size:20");
            nextLevel.setTooltip(nextLevelTip);

            nextLevel.setGraphic(new ImageView((image3)));
            nextLevel.setDisable(true);
            nextLevel.setVisible(false);

            save = new Label();
            FileInputStream input4 = new FileInputStream("././resources/images/save.png");
            Image image4 = new Image(input4);
            Tooltip saveTip = new Tooltip("You can save this level");
            saveTip.setStyle("-fx-font-size:20");
            save.setTooltip(saveTip);
            save.setGraphic(new ImageView((image4)));
            save.setDisable(true); //show unly when player succeed the game.
            save.setVisible(false);

        }
        catch(Exception e){e.printStackTrace();}
        buttonCollection = new HBox();
        buttonCollection.getChildren().addAll(save,replay,play,nextLevel);
        buttonCollection.setPrefSize(370, 30);
        buttonCollection.setAlignment(Pos.CENTER_RIGHT);
        buttonCollection.setSpacing(10);


        //남은 시간 라벨

         timeSeconds = countDownTime;
        Rectangle rect = new Rectangle();
        Tooltip remainingTimeTip = new Tooltip("Time left to get the target points");
        remainingTimeTip.setStyle("-fx-font-size:20");
        rect.setArcHeight(15);
        rect.setArcWidth(15);
        Label remainingTimeBackground = new Label();
        remainingTimeBackground.setPrefSize(200,30);
        remainingTimeBackground.setShape(rect);
        remainingTimeBackground.setTooltip(remainingTimeTip);
        remainingTimeBackground.setStyle("-fx-background-color:#CCCCCC; -fx-text-fill : red; -fx-font-size:15");
        remainingTime.setPrefSize(200,30);
        remainingTime.setStyle("-fx-background-color:#CCCCCC; -fx-text-fill : red; -fx-font-size:14");
        remainingTime.setAlignment(Pos.CENTER);
        StackPane remainingTimeLabel = new StackPane(remainingTimeBackground,remainingTime);

        //targt points Label
        targetPoints = new Label();
        targetPoints.setText(String.valueOf(gameData.getGameLevel()*10)); // targetPoints 설정!

        targetPoints.setStyle("-fx-text-fill: WHITE;-fx-font-size:17; -fx-font-weight: bold");
        Label tempLabel = new Label("Target : ");
        tempLabel.setStyle("-fx-text-fill: WHITE; -fx-font-size:17");
        targetBox = new HBox(tempLabel,targetPoints);
        targetBox.setAlignment(Pos.CENTER);
        targetBox.setStyle("-fx-background-color : #006600");
        targetBox.setPrefSize(100,30);

        //guessedLetter
        guessedLetter.setAlignment(Pos.CENTER);
        guessedLetter.setPrefSize(100,25);
        guessedLetter.setStyle("-fx-background-color : #006600");
        guessedLetter.setSpacing(10);
        guessedLetter.setAlignment(Pos.CENTER_LEFT);
        guessedLetter.setPadding(new Insets(0,0,0,10));



        scoreBoardPane = new ScrollPane();
        scoreBoardCast = new StackPane();
        scoreBoardCast.setPrefSize(200,230);
        scoreBoardPane.setContent(scoreBoardCast);
        scoreBoardPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); //가로 스크롤바 없애기
        scoreBoardPane.setPrefSize(200,240);
         scoreBoardPane.setStyle("-fx-background-color: radial-gradient(radius 100%, derive(-fx-base,20%), derive(-fx-base,-20%));");
        foundWordsBackground = new Label();
        //foundWordsBackground.setPrefSize(200,260);
        foundWordsBackground.setStyle("-fx-background-color:#003300");
        Line separator = new Line();
        separator.setTranslateX(2);
        separator.setEndY(1000);

        scoreBoard.setVisible(false);//처음에는 스코어보드 안보이게!scoreBoard
        scoreBoard.setPadding(new Insets(5,5,5,5));
         scoreBoard.setHgap(100);


        total = new Label("TOTAL POINTS");
        totalPoints = new Label("0");
         total.setStyle(" -fx-text-fill: WHITE");
         totalPoints.setStyle(" -fx-text-fill: WHITE");
        total.setVisible(false);  //처음에는 안보이게
        totalPoints.setVisible(false); //처음에는 안보이게
        totalPointsPane = new GridPane();
        totalPointsPane.add(total,0,0);
        totalPointsPane.add(totalPoints,1,0);
        totalPointsPane.setAlignment(Pos.CENTER_LEFT);
        totalPointsPane.setHgap(60);
        totalPointsPane.setPadding(new Insets(0,0,0,10));
        totalPointsPane.setPrefSize(200,20);
        totalPointsBackground = new Label();
        totalPointsBackground.setPrefSize(200,22);
        totalPointsBackground.setAlignment(Pos.BASELINE_CENTER);
        totalPointsBackground.setStyle("-fx-background-color:#003300");
        Line separator2 = new Line();
        separator2.setTranslateX(3);
        separator2.setStartY(-1);
        separator2.setEndY(20);
        StackPane totalPointsCast = new StackPane(); // contains earning score.
        totalPointsCast.getChildren().addAll(totalPointsBackground,separator2,totalPointsPane);

        scoreBoardCast.getChildren().addAll(separator,scoreBoard);
        resultPanel = new VBox();
        resultPanel.setPrefSize(200,275);
        resultPanel.getChildren().addAll(scoreBoardPane, totalPointsCast);

        //alphabet area
        alphabeticAreaStackPane.getChildren().add(alphabetArea);
        alphabeticAreaStackPane.setPrefSize(450, 330);
        alphabeticAreaStackPane.setAlignment(Pos.TOP_LEFT);
        alphabetArea.setPadding(new Insets(0, 0, 0, 50)); // 상 우 하 좌 패드넣기
        alphabetArea.setPrefSize(450, 330);
        alphabetArea.setVgap(15); //element들 수직간 거리
        alphabetArea.setHgap(30); //element들 수직간 거리


        gameComponentsPane.setHgap(10);
        gameComponentsPane.setVgap(20);
        gameComponentsPane.add(modePane, 0, 0);
        gameComponentsPane.add(alphabeticAreaStackPane, 0, 1);
        gameComponentsPane.add(levelPane, 0, 2);
        gameComponentsPane.add(buttonCollection, 0, 3);
        gameComponentsPane.add(remainingTimeLabel, 1, 0);
        VBox tempBox = new VBox(); //guessedLetter 이랑
        tempBox.setSpacing(30);
        tempBox.getChildren().addAll(guessedLetter,resultPanel);
        gameComponentsPane.add(tempBox,1,1);
        gameComponentsPane.add(targetBox,1,2);

        setGameAlphabet(gameData);
         setTimer(controller, gameData); //셋 타이머가 setGameAlphabet() 다음에 와야지. 알파벳 셋팅할때 무한루프에서 시간을
                                        // 오래 소모 할수도 있으므로!
        setGameplayToolBar(); //툴바 기본세팅

    }


    public void handleReplayButton(BuzzwordController controller) throws  IOException{
        replay.setOnMouseClicked(e->{
            try {
                controller.replayGame();
            }
            catch(Exception x){x.printStackTrace();}
        });
    }

    public void handlePlayButton(BuzzwordController controller) throws IOException{
        play.setOnMouseClicked(e->{
            try{
                if (controller.getGui().getGuiState().equals(controller.getGui().guiState.GAMEPLAY))
                    controller.playGame();
                else if(controller.getGui().getGuiState().equals(controller.getGui().guiState.GAMEPLAY_PLAYING))
                    controller.pauseGame();
                else if(controller.getGui().getGuiState().equals(controller.getGui().guiState.GAMEPLAY_PAUSE))
                    controller.resumeGame();
            }
            catch(Exception x){x.printStackTrace();}
        });
    }

    public void freezeGameplayScreen(){
        for (int i=0 ; i<4; i++)
        {
            for(int j=0; j<4;j++){
                wordCircle[i][j].setFill(Color.web("#222222"));
                wordLabel[i][j].setVisible(false); //단어들 사라지게
                wordUnit[i][j].setDisable(true); // 알파벳 못누르게
                timeline.stop();
            }

        }
    }

    public void unfreezeGameplayScreen(){
        for (int i=0 ; i<4; i++)
        {
            for(int j=0; j<4;j++) {
                wordCircle[i][j].setFill(Color.web("#555555"));
                wordLabel[i][j].setVisible(true); //단어들 다시 보이게
                wordUnit[i][j].setDisable(false); // 알파벳 다시 누를 수 있게
                timeline.play(); //시간다시 가게
            }
        }
    }

    public void setTimer(BuzzwordController controller, GameData gamedata){
        timeline = new Timeline();

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), //keyframe = 일종의 tread
                        new EventHandler<ActionEvent>() {
                            public void handle(ActionEvent event)
                            {
                                timeSeconds--;
                                // update timerLabel
                                remainingTime.setText("Time remaining : "+ String.valueOf(timeSeconds)+" seconds");
                                if (timeSeconds <= 0) {
                                    timeline.stop();
                                    finishGame(controller, gamedata);

                                }
                            }

                        }));
        timeline.playFromStart();
    }

    //time's up game finish
    public void finishGame(BuzzwordController controller, GameData gamedata)
    {
        remainingTime.setText("Time remaining : 0 seconds"); //종료 눌렀을때 time 떨어지는 나타나는 버그 막기 위해서.
        Boolean isCleared = false;
        play.setDisable(true);

        //마우스드래그 /키보드 입력하다가 게임 끝날경우 대비
        guessedLetter.getChildren().clear();
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


        controller.mouseDragDisable(); //마우스 이벤트처리 안되도록! (마우스 이벤트처리는 예외적으로 그냥 알파벳 세팅할때 자동 으로 되도록했돠 ㅠㅠ 쏘리욤)
        if ( totalPointsNumber >= Integer.valueOf(targetPoints.getText())){
            controller.gameCleared();
            isCleared = true;
        }
        else{
            controller.gameFailed();
        }

        showHiddenWordsList(gamedata);
        popFinishMsgUp(isCleared);

    }

    //게임 끝나고 최고점수 획득했으면 그 내용 알림
    public void popFinishMsgUp(Boolean isCleared){

    };

    //정답 리스트 모두 알림
    public void  showHiddenWordsList(GameData gamedata){
        Iterator<String> iterator =  gamedata.getHiddenWordsList().iterator();
        ArrayList<Label> answerList = new ArrayList<Label>();
        ArrayList<Label> scoreList = new ArrayList<Label>();
        long score = 0;
        int i = gamedata.getAnswerList().size();
        int j = 0;
        System.out.print("Left Hidden words : ");
        while(iterator.hasNext())
        {

            String words = iterator.next();
            System.out.print("\""+words+"\", ");
            switch (words.length())
            {
                case 3 : score = PointsFor3LenWords; break;
                case 4 : score = PointsFor4LenWords; break;
                case 5 : score = PointsFor5LenWords; break;
                case 6 : score = PointsFor6LenWords; break;
                case 7 : score = PointsFor7LenWords; break;
            }
            if (words.length() >= 8)
                score = PointsForLongWords;


            answerList.add(new Label(words));
            answerList.get(j).setTextFill(Color.web("#FF0066"));
            scoreList.add(new Label(String.valueOf(score)));
            scoreList.get(j).setTextFill(Paint.valueOf("#FF0066"));
            scoreBoard.add(answerList.get(j),0,i );
            scoreBoard.add(scoreList.get(j),1,i );
            i++;
            j++;
        }
        System.out.println("");
    };

    public void setGameplayToolBar() {

        toolBar.getChildren().setAll(profileLabel, homeBt);
        toolBar.setPrefSize(150, 600);
        toolBar.setPadding(new Insets(100, 5, 0, 15));
        toolBar.setSpacing(15);
        toolBar.setStyle("-fx-background-color : #BBBBBB");
    }

    //단어 셋팅관련 함수
    public boolean findWord(String word) {
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (this.findWord(word, row, col)) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean findWord(String word, int row, int col) {
        if (word.equals("")) {
            return true;
        }
        else if (row < 0 || row >= 4 ||
                col < 0 || col >= 4||
                this.words[row][col] != word.charAt(0)) {
            return false;
        }
        else {
            char safe = this.words[row][col];
            this.words[row][col] = '*';
            String rest = word.substring(1, word.length());
            boolean result = this.findWord(rest, row-1, col-1) ||
                    this.findWord(rest, row-1,   col) ||
                    this.findWord(rest, row-1, col+1) ||
                    this.findWord(rest,   row, col-1) ||
                    this.findWord(rest,   row, col+1) ||
                    this.findWord(rest, row+1, col-1) ||
                    this.findWord(rest, row+1,   col) ||
                    this.findWord(rest, row+1, col+1);
            this.words[row][col] = safe;
            return result;
        }
    }
    public boolean doubleCheck(String input){
        boolean checkValue = true;
        int treshold;
        for(int i = 'a'; i < 'a'+27 ; i++)
        {
            treshold = 2;
            for (int k = input.length(); k > 0; k--){
                if(String.valueOf(input.charAt(k-1)).equals(String.valueOf((char)i)))
                    treshold --;

            }
            if (treshold <= 0){
                checkValue = false;
                break;
            }

        }
        return checkValue;
    }

    //유효한 buzzword BOARD 생성하는 함수
    public void wordsGeneration(GameData gameData)
    {
        words = new char[SIZE][SIZE];
        long validWordsPoints;
        long validWordsNumber;
        boolean isSameLetterAdjacent;


        while(true)//유효한 words 이 깔릴때 까지 무한루프 돌림
        {
            validWordsPoints = 0;
            validWordsNumber = 0;
            isSameLetterAdjacent = false;

            gameData.getHiddenWordsList().clear();
            gameData.getAnswerList().clear();

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j<4; j++){
                    this.words[i][j] = (char)('a' + (int)(Math.random()*26)); //단어 랜덤생성
                }
            }

            //improve efficiency
            for(int i = 'a'; i < 'a'+27 ; i++){
                String letter = String.valueOf((char)i);
                String adjacentSameLetters = letter + letter;
                if (findWord(adjacentSameLetters))
                    isSameLetterAdjacent = true;
            }


            try {

                String sCurrentLine;
                Iterator<String> iterator = gameData.getHashSet().iterator();

                while (iterator.hasNext())
                {
                    sCurrentLine = iterator.next();
                    if(findWord(sCurrentLine.toLowerCase()) == true && doubleCheck(sCurrentLine.toLowerCase()))
                    {
                        if (gameData.getGameLevel() < 6 && sCurrentLine.length() >=3 && sCurrentLine.length() < 12) {
                            validWordsNumber +=1;
                            gameData.getHiddenWordsList().add(sCurrentLine.toLowerCase());
                            switch (sCurrentLine.length())
                            {
                                case 3 : validWordsPoints += PointsFor3LenWords; break;
                                case 4 : validWordsPoints += PointsFor4LenWords; break;
                                case 5 : validWordsPoints += PointsFor5LenWords; break;
                                case 6 : validWordsPoints += PointsFor6LenWords; break;
                                case 7 : validWordsPoints += PointsFor7LenWords; break;
                            }
                            if (sCurrentLine.length() >= 8)
                                validWordsPoints += PointsForLongWords;
                        }
                        else if (gameData.getGameLevel() >= 6 && sCurrentLine.length() >= 4 && sCurrentLine.length() < 12) //6레벨 이상부터는 단어길이 4이상
                        {
                            validWordsNumber += 1;
                            gameData.getHiddenWordsList().add(sCurrentLine.toLowerCase());
                            switch (sCurrentLine.length())
                            {
                                case 4 : validWordsPoints += PointsFor4LenWords; break;
                                case 5 : validWordsPoints += PointsFor5LenWords; break;
                                case 6 : validWordsPoints += PointsFor6LenWords; break;
                                case 7 : validWordsPoints += PointsFor7LenWords; break;
                            }
                            if (sCurrentLine.length() >= 8)
                                validWordsPoints += PointsForLongWords;
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            int extraBoundary = 40;
            if (gameData.getGameMode().equals("First Name")) //First name은 집합 자체가 너무 작다.
                extraBoundary = 5;
            //랜덤생성된 판에서 단어를 targetPoints 이상 찾을 수 있어야 게임이 가능
            // + extraBoundary 한 이유는 충분히 단어들이 생겨야 찾을 수 있으므로
            int lowerBoundary = Integer.parseInt(targetPoints.getText()) + extraBoundary;
            int upperBoundary = Integer.parseInt(targetPoints.getText()) + 250;


            if ( !isSameLetterAdjacent && validWordsPoints >= lowerBoundary && validWordsPoints <= upperBoundary)
            {
                System.out.println("Maximum points you can earn is " + validWordsPoints + ".\nThere are "
                        + validWordsNumber +" valid words hidden in the Buzzword Board.");
                Iterator<String> iterator2 = gameData.getHiddenWordsList().iterator();
                while(iterator2.hasNext())
                    System.out.print("\""+iterator2.next()+ "\", ");
                System.out.println("");
                break;
            }

        }
    }
    private void setGameAlphabet(GameData gameData) {

        Label isDragging[][] = new Label[4][4];
        wordsGeneration(gameData);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j<4; j++){
                wordLabel[j][i] = new Label();
                wordLabel[j][i].setTextFill(Color.web("#DDDDDD"));
                wordLabel[j][i].setFont(new javafx.scene.text.Font(17));
                wordLabel[j][i].setText(String.valueOf(words[j][i]));
                wordCircle[j][i] = new Circle();
                wordCircle[j][i].setRadius(35);
                wordCircle[j][i].setFill(Color.web("#555555"));
                wordUnit[j][i] = new StackPane();
                wordUnit[j][i].getChildren().addAll(wordCircle[j][i], wordLabel[j][i]);
                alphabetArea.add(wordUnit[j][i], j, i);
                wordUnit[j][i].setId(i+","+j+","+0); //마지막 숫자 0 은 아직 안지나감을 의미.




                //Event handling for alhabet.
                wordUnit[j][i].setOnDragDetected(e->{
                    ((StackPane)e.getSource()).startFullDrag();
                    for (int p = 0; p <4; p++){
                        for (int q= 0; q<4; q++){
                            wordCircle[p][q].setEffect(null);
                            wordUnit[q][p].setId(p+","+q+","+0);
                        }
                    }
                    Shadow borderGlow = new Shadow();
                    borderGlow.setColor(Color.RED);
                    borderGlow.setWidth(2);
                    borderGlow.setHeight(2);
                    ((StackPane)e.getSource()).getChildren().get(0).setEffect(borderGlow);
                    preIDSaver = ((StackPane)e.getSource()).getId();
                 });

                wordUnit[j][i].setOnMouseDragEntered(e->{

                    Shadow borderGlow = new Shadow(); //Shadow 는 선 자체를 바꿔버림 ㅋㅋ/ DropShadow는 선 남기고 선밖으로 효과
                    borderGlow.setColor(Color.RED);
                    borderGlow.setWidth(2);
                    borderGlow.setHeight(2);
                    for (int p = 0; p <4; p++){
                        for (int q= 0; q<4; q++) {
                            if (new String(p+","+q+","+0).equals(((StackPane)e.getSource()).getId()))
                            {
                                char r = preIDSaver.charAt(0);
                                char s = preIDSaver.charAt(2);

                                if (((p + 49 == r|| p + 47 ==r) && (q + 49 == s|| q + 47 ==s)) //대각선 붙어 있어야
                                || (Math.abs(p + 48 - r) <= 1 && Math.abs(q + 48 - s) <= 1)) // 가로 세로로 붙어있어야
                                {
                                    ((StackPane)e.getSource()).getChildren().get(0).setEffect(borderGlow);
                                    ((StackPane)e.getSource()).setId(p+","+q+","+1); //한번 지나가면 1
                                    guessedLetterUnit.add(new Label(((Label)((StackPane)e.getSource()).getChildren().get(1)).getText()));
                                    if (guessedLetterUnit.size() < 12)
                                        guessedLetter.getChildren().add(guessedLetterUnit.get(guessedLetterUnit.size()-1));
                                    if (p + 49 == r && q + 48 == s)
                                        vLines[p][q].setEffect(borderGlow);
                                    else if (p + 47 == r&& q + 48 == s)
                                        vLines[p-1][q].setEffect(borderGlow);
                                    else if (p + 48 == r && q+ 49 == s)
                                        hLines[p][q].setEffect(borderGlow);
                                    else if (p + 48 == r && q+ 47 == s)
                                        hLines[p][q-1].setEffect(borderGlow);
                                    else if (p + 49 == r && q+ 49 == s)
                                        diagnalLines2[p][q].setEffect(borderGlow);
                                    else if (p + 47 == r && q+ 47 == s)
                                        diagnalLines2[p-1][q-1].setEffect(borderGlow);
                                    else if (p + 47 == r && q+ 49 == s)
                                        diagnalLines[p-1][q].setEffect(borderGlow);
                                    else if (p + 49 == r && q+ 47 == s)
                                        diagnalLines[p][q-1].setEffect(borderGlow);
                                    preIDSaver = p+","+q+","+1;
                                }
                            }
                        }
                    }


                });
                wordUnit[j][i].setOnMouseReleased(e->{
                    ((StackPane)e.getSource()).getChildren().get(0).setEffect(null);
                    StringBuilder strb = new StringBuilder("");
                    for(int k = 0; k < guessedLetterUnit.size();k++){
                        strb.append(guessedLetterUnit.get(k).getText());
                    }
                    String guessedWords = strb.toString();

                    Iterator<String> iterator = gameData.getHiddenWordsList().iterator();

                    while (iterator.hasNext())
                    {
                        if (iterator.next().equals(guessedWords))
                        {
                            iterator.remove(); //항목 제거
                            gameData.getAnswerList().add(guessedWords);
                            switch (guessedWords.length())
                            {
                                case 3 : totalPointsNumber += PointsFor3LenWords;
                                    scoreBoard.add(new Label(String.valueOf(PointsFor3LenWords)),1,gameData.getAnswerList().size()-1); break;
                                case 4 : totalPointsNumber += PointsFor4LenWords;
                                    scoreBoard.add(new Label(String.valueOf(PointsFor4LenWords)),1,gameData.getAnswerList().size()-1);break;
                                case 5 : totalPointsNumber += PointsFor5LenWords;
                                    scoreBoard.add(new Label(String.valueOf(PointsFor5LenWords)),1,gameData.getAnswerList().size()-1);break;
                                case 6 : totalPointsNumber += PointsFor6LenWords;
                                    scoreBoard.add(new Label(String.valueOf(PointsFor6LenWords)),1,gameData.getAnswerList().size()-1);break;
                                case 7 : totalPointsNumber += PointsFor7LenWords;
                                    scoreBoard.add(new Label(String.valueOf(PointsFor7LenWords)),1,gameData.getAnswerList().size()-1);break;
                            }
                            if (guessedWords.length() >= 8){
                                scoreBoard.add(new Label(String.valueOf(PointsForLongWords)),gameData.getAnswerList().size(),gameData.getAnswerList().size()-1);
                                totalPointsNumber += PointsForLongWords;
                            }
                            totalPoints.setText(String.valueOf(totalPointsNumber));
                            scoreBoard.add(new Label(guessedWords), 0 ,gameData.getAnswerList().size()-1);
                        }
                    }
                    guessedLetter.getChildren().clear();
                    guessedLetterUnit.clear();



                    //이펙트 모두 제거
                    for (int p = 0; p <4; p++){
                        for (int q= 0; q<4; q++){
                            wordCircle[p][q].setEffect(null);
                            wordUnit[q][p].setId(p+","+q+","+0); //다시 3번째 요소 0(안지나감)으로 리셋
                        }
                    }
                    for (int p = 0; p < 3; p++) {
                        for (int q = 0; q < 4; q++) {
                            hLines[q][p].setEffect(null);
                            vLines[p][q].setEffect(null);
                            if (q <3){
                                diagnalLines[p][q].setEffect(null);
                                diagnalLines2[p][q].setEffect(null);
                            }
                        }
                    }
                });
            }
        }
        setLines(); //선 배치
    }

    private void setLines() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                diagnalLines[i][j] = new Line();
                diagnalLines[i][j].setRotate(-72);
                diagnalLines[i][j].setStartX(0);
                diagnalLines[i][j].setEndX(53);
                diagnalLines[i][j].setStartY(0);
                diagnalLines[i][j].setEndY(32);
                diagnalLines2[i][j] = new Line();
                diagnalLines2[i][j].setRotate(-171);
                diagnalLines2[i][j].setStartX(0);
                diagnalLines2[i][j].setEndX(53);
                diagnalLines2[i][j].setStartY(0);
                diagnalLines2[i][j].setEndY(32);

                if (i == 0) {
                    diagnalLines[i][j].setTranslateX(110 + 29 * j + 71 * j);
                    diagnalLines[i][j].setTranslateY(64);
                    diagnalLines2[i][j].setTranslateX(106 + 29 * j + 71 * j);
                    diagnalLines2[i][j].setTranslateY(62);

                } else
                {
                    diagnalLines[i][j].setTranslateX(110 + 29 * j + 71 *j);
                    diagnalLines[i][j].setTranslateY(66 + 82 * i + 3 * (i - 1));
                    diagnalLines2[i][j].setTranslateX(106 + 29 * j + 71 * j);
                    diagnalLines2[i][j].setTranslateY(67 + 82 * i + 3 * (i - 1));
                }
                alphabeticAreaStackPane.getChildren().addAll(diagnalLines[i][j],diagnalLines2[i][j]);
            }
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                hLines[i][j] = new Line();
                hLines[i][j].setStartX(0);
                hLines[i][j].setEndX(29);

                if (i == 0)
                {
                    hLines[i][j].setTranslateX(120 + 29 * j + 71 * j);
                    hLines[i][j].setTranslateY(34);
                } else
                {
                    hLines[i][j].setTranslateX(120 + 29 * j + 71 * j);
                    hLines[i][j].setTranslateY(35 + 82 * i + 3 * (i - 1));
                }
                alphabeticAreaStackPane.getChildren().add(hLines[i][j]);
            }
        }
        for (int i = 0; i < 3; i++){
            for (int j=0; j<4; j++){
                vLines[i][j] = new Line();
                vLines[i][j].setStartY(0);
                vLines[i][j].setEndY(14);

                if (j == 0)
                {
                    vLines[i][j].setTranslateX(83);
                    vLines[i][j].setTranslateY(70 + 30 * i + 55 * i);
                } else
                {
                    vLines[i][j].setTranslateX(83 + 100 * j);
                    vLines[i][j].setTranslateY(70 + 30 * i + 55 * i);
                }
                alphabeticAreaStackPane.getChildren().add(vLines[i][j]);
            }
        }
    }

    public void setupHandlers(BuzzwordController controller) throws IOException {
        handleLogout(controller);
        handleHome(controller);
        handlePlayButton(controller);
        handleReplayButton(controller);
        handleKeyInput(controller);
        handleNextLevelButton(controller);
        handleSaveButton(controller);
    }

    public void handleNextLevelButton(BuzzwordController controller){
        nextLevel.setOnMouseClicked(e -> {
            try {
                controller.nextLevelPlay();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public void handleSaveButton(BuzzwordController controller){
        save.setOnMouseClicked(e -> {
            try {
                save.setVisible(false);
                save.setDisable(true);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public void handleKeyInput(BuzzwordController controller ) {
            controller.keyInputEnable();
       // if (controller.getGui().getGuiState().equals(controller.getGui().guiState.GAMEPLAY_PAUSE))
         //   controller.keyInputDisable();
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
        homeBt.setOnMouseClicked(e ->
        {
            try {
                controller.returnHomeScreen();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }


    public VBox getToolBar() {
        return toolBar;
    }
    public Label getPlay() {return play;}
    public void setPlay(Label play) {this.play = play;}
    public Label getReplay() {return replay;}
    public void setReplay(Label replay) {this.replay = replay;}
    public Label getNextLevel() {return nextLevel;}
    public Label getSave() {return save;}
    public HBox getGuessedLetter() {return guessedLetter;}
    public void setGuessedLetter(HBox guessedLetter) {this.guessedLetter = guessedLetter;}
    public GridPane getScoreBoard() {return scoreBoard;}
    public ScrollPane getscoreBoardPane() {return scoreBoardPane;}
    public Label getFoundWordsBackground() {return foundWordsBackground;}
    public Label getTotal() {return total;}
    public Label getTotalPoints() {return totalPoints;}
    public GridPane getGameComponentsPane() throws IOException{return gameComponentsPane;}

    public ArrayList<Label> getFoundWords() {
        return foundWords;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public StackPane[][] getWordUnit() {
        return wordUnit;
    }

    public Label[][] getWordLabel() {
        return wordLabel;
    }

    public Line[][] getDiagnalLines() {
        return diagnalLines;
    }

    public String getPreIDSaver() {
        return preIDSaver;
    }

    public Line[][] getDiagnalLines2() {
        return diagnalLines2;
    }

    public Line[][] gethLines() {
        return hLines;
    }

    public Line[][] getvLines() {
        return vLines;
    }

    public Circle[][] getWordCircle() {
        return wordCircle;
    }

    public ArrayList<Label> getGuessedLetterUnit() {
        return guessedLetterUnit;
    }

    public int getTotalPointsNumber() {
        return totalPointsNumber;
    }

    public void setTotalPointsNumber(int totalPointsNumber) {
        this.totalPointsNumber = totalPointsNumber;
    }
}


