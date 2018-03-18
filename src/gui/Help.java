package gui;

import controller.BuzzwordController;
import data.GameData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by kkyeo on 12/10/2016.
 */
public class Help {

    private ScrollPane helpPane;
    private VBox toolBar;
    private Button homeBt;
    private Label inform;

    public ScrollPane getHelpPane() {return helpPane;}

    public VBox getToolBar() {
        return toolBar;
    }

    public void initHelpScreen(GameData gameData) {

        toolBar = new VBox();
        helpPane = new ScrollPane();
        helpPane.setPadding(new Insets(13,13,13,13));
        inform = new Label();


        homeBt = new ToolbarButton(" Home");
        Tooltip homeBtTip = new Tooltip("Go back to HOME menu.");
        homeBtTip.setStyle("-fx-font-size:20");
        homeBt.setTooltip(homeBtTip);

        helpPane.setContent(inform);
        helpPane.setPrefSize(730,600);
        helpPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); //가로 스크롤바 없애기
        setHelpToolBar(); //툴바 기본세팅



        inform.setStyle("-fx-font-size : 20; ");
        inform.setText("< Game Rule >\n" +
                "   Player can choose 4 different game mode. Regardless of game mode, player\n" +
                "should find words with minimum 3letters until level 5.\n" +
                "   Notice that the minimum becomes 4 instead of 3 from level 6 in any mode!\n" +
                "   Every game in different level has different target points. To reach this\n" +
                "points, you would find more words or longer words hidden in the words panel.\n" +
                "   As you move on next level, target points increase by 10 points. Earning\n" +
                "points by finding words are described below." +
                "\n  *words with 3letters : 3points" +
                "\n  *words with 4letters : 4points\n  *words with 5letters : 5points\n  *words with 6letters : 7points" +
                "\n  *words with 7letters : 10points\n  *words with more than 8 letters : 15points.\n" +
                "\n< Short Cut Information >\n" +
                "You can use short cut in specific situation\n" +
                "  - Ctrl + Alt + P : Create New profile.\n" +
                "  - Ctrl + L : Login.\n" +
                "  - Ctrl + P : Play the Game\n" +
                "  - Ctrl + R : Replay the Game immediately.\n" +
                "  - Ctrl + > : Next Level.\n" +
                "  - Ctrl + H : return to Home Screen.\n" +
                "  - Ctrl + Q : Quit the application.\n\n" +
                "Developer : Kyeongsoo Kim\n" +
                "E-mail : followyourinnercompass@gmail.com");

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
                controller.returnHomeScreen();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

}
