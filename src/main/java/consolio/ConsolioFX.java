/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package consolio;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * @version 2017/02/09 16:25:05
 */
public class ConsolioFX extends Application {

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Tabs");
        Group root = new Group();
        Scene scene = new Scene(root, 400, 250, Color.WHITE);

        TabPane tabPane = new TabPane();
        tabPane.tabMinWidthProperty().set(50);

        tabPane.setOnKeyPressed(e -> {
            moveRight(tabPane);
            System.out.println(Thread.currentThread());
        });

        BorderPane borderPane = new BorderPane();
        for (int i = 0; i < 5; i++) {
            Tab tab = new Tab();
            tab.setText("Tab" + i);
            HBox hbox = new HBox();
            hbox.getChildren().add(new Label("Tab" + i));
            hbox.setAlignment(Pos.CENTER);
            tab.setContent(hbox);
            tabPane.getTabs().add(tab);
        }

        // bind to take available space
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());

        borderPane.setCenter(tabPane);
        root.getChildren().add(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void moveRight(TabPane workBook) {
        Tab active = workBook.getSelectionModel().getSelectedItem();
        int cTabIndex = workBook.getSelectionModel().getSelectedIndex();
        int tabCount = workBook.getTabs().size();

        if (tabCount > 1 && cTabIndex > 0) {
            workBook.getTabs().remove(cTabIndex);
            workBook.getTabs().add(cTabIndex - 1, active);
        }
    }

    /**
     * <p>
     * Entry point.
     * </p>
     * 
     * @param args
     */
    public static void main(String[] args) {
        launch(ConsolioFX.class);
    }
}
