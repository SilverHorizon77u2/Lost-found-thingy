package org.example.lfrs_group_4_oop;

import org.example.lfrs_group_4_oop.database.DatabaseManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DatabaseManager.initializeDatabase();
        SceneManager.setStage(stage);
        SceneManager.showLogin();
    }
}
