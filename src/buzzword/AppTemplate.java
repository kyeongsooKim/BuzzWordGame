package buzzword;


import controller.BuzzwordController;
import data.GameData;
import gui.AppGUI;
import javafx.application.Application;
import javafx.stage.Stage;

public class AppTemplate extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{


        AppGUI gui;
        BuzzwordController controller ; // main controller
        GameData gamedata;

        gui = new AppGUI(primaryStage);
        gamedata = new GameData();
        gamedata.initializeGamedata();
        controller = new BuzzwordController(gui, gamedata);
        gui.setController(controller);
        gui.initializeWindow();


    }

}
