/*
 * Copyright (c) 2013, 2014 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package moleculesampleapp;

import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.transform.Rotate;
import javafx.event.EventHandler;
import math.math.geometry.Rotation;
import math.math.geometry.RotationOrder;
import math.math.geometry.Vector3D;

/**
 *
 * @author cmcastil
 */
public class MoleculeSampleApp extends Application {

    Stage stage;
    Scene scene;


    @Override
    public void start(Stage primaryStage) {

        System.out.println("start()");

        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.RED);
        redMaterial.setSpecularColor(Color.DARKRED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.GREEN);
        greenMaterial.setSpecularColor(Color.gray(0.3));

        Sphere oxygenSphere = new Sphere(10);
        oxygenSphere.setMaterial(redMaterial);



        Arrow myArrow1 = new Arrow();
        myArrow1.setMaterial(greenMaterial);


        CameraView camera1 = new CameraView(384, 384, true, SceneAntialiasing.BALANCED);

        camera1.elements.getChildren().addAll(myArrow1);

        DArrow myArrow2 = new DArrow(30,5,10,10);
        myArrow2.setMaterial(redMaterial);


        CameraView camera2 = new CameraView(384, 384, true, SceneAntialiasing.BALANCED);

        camera2.elements.getChildren().addAll(myArrow2);

        Arrow myArrow3 = new Arrow();
        myArrow3.setMaterial(greenMaterial);


        CameraView camera3 = new CameraView(384, 384, true, SceneAntialiasing.BALANCED);

        camera3.elements.getChildren().addAll(myArrow3);

        Arrow myArrow4 = new Arrow();
        myArrow4.setMaterial(greenMaterial);


        CameraView camera4 = new CameraView(384, 384, true, SceneAntialiasing.BALANCED);

        camera4.elements.getChildren().addAll(myArrow4);



        GridPane gridPane = new GridPane();

        gridPane.add(camera1.getScene(), 0, 0);
        gridPane.add(camera2.getScene(), 1, 0);
        gridPane.add(camera3.getScene(), 0, 1);
        gridPane.add(camera4.getScene(), 1, 1);



        SubScene subScene = new SubScene(gridPane, 768, 768, true, SceneAntialiasing.BALANCED);
        VBox vbox = new VBox(subScene);
        scene = new Scene(vbox, 800, 800, true);

        scene.setFill(Color.LIGHTGRAY);

        stage = primaryStage;
        primaryStage.setTitle("Molecule Sample Application");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
