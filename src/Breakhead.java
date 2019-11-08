import com.sun.prism.image.ViewPort;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderPaneBuilder;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Breakhead extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Break Head");
        Menu importer = new Menu("Importer");
        MenuItem loadImage = new MenuItem("Load image");
        importer.getItems().addAll(loadImage);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(importer);
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(menuBar);

        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(1000);

        int nbPiece = 9;
        Image image = null;
        ArrayList<ImageView> arrayList = new ArrayList<>();
        ArrayList<ImageView> verification = new ArrayList<>();


        loadImage.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Veuillez sélectionner une image");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image png", "*.png"), new FileChooser.ExtensionFilter("Image gif", "*.gif"), new FileChooser.ExtensionFilter("Image jpg", "*.jpg"));
            File fichier = fileChooser.showOpenDialog(primaryStage);

            Image image2 = image;

            try {
                image2 = new Image(new FileInputStream(fichier.getPath()));
            } catch (Exception e) {

            }


            arrayList.clear();
            arrayList.addAll(setImage(image2, nbPiece));
            verification.addAll(setImage(image2, nbPiece));
            GridPane gridPane = melanger(setImage(image2, nbPiece), nbPiece);
            gridPane.setAlignment(Pos.CENTER);
            borderPane.setCenter(gridPane);



        });


        Scene scene = new Scene(borderPane);
        scene.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.M) {
                GridPane gridPane = melanger(arrayList, nbPiece);
                gridPane.setAlignment(Pos.CENTER);
                borderPane.setCenter(gridPane);
            }
        });


        if (verification.equals(arrayList)&&image!=null) {
            System.out.println("VOUS AVEZ GAGNÉ");
        }
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public ArrayList<ImageView> setImage(Image image, int nbPiece) {
        ArrayList<ImageView> imageViews = new ArrayList<>();
        double largeurPiece = image.getWidth() / Math.sqrt(nbPiece);
        double hauteurPiece = image.getHeight() / Math.sqrt(nbPiece);
        int largeur = (int) Math.sqrt(nbPiece);
        Rectangle2D[] rectangle2DTableau = new Rectangle2D[nbPiece];
        for (int i = 0; i < largeur; i++) {
            for (int j = 0; j < largeur; j++) {
                rectangle2DTableau[(i * 3) + j] = new Rectangle2D(largeurPiece * j, hauteurPiece * i, largeurPiece, hauteurPiece);
            }
        }


        for (int i = 0; i < nbPiece; i++) {
            imageViews.add(i, new ImageView());
            imageViews.get(i).setPreserveRatio(true);
            if (image.getHeight() < image.getWidth()) {
                imageViews.get(i).setFitWidth(700 / largeur);
            } else {
                imageViews.get(i).setFitHeight(700 / largeur);
            }

            imageViews.get(i).setViewport(rectangle2DTableau[i]);
            imageViews.get(i).setImage(image);
        }


        for (int i = 0; i < imageViews.size(); i++) {
            ClipboardContent contenu = new ClipboardContent();
            int content = i;
            imageViews.get(i).setOnDragDetected(event1 -> {
                Dragboard dragboard = imageViews.get(content).startDragAndDrop(TransferMode.COPY);
                contenu.putImage(imageViews.get(content).getImage());
                dragboard.setContent(contenu);
            });
            imageViews.get(i).setOnDragOver(event -> {
                event.acceptTransferModes(TransferMode.COPY);
            });
            imageViews.get(i).setOnDragDropped(event -> {
                ImageView source = (ImageView) event.getGestureSource();
                ImageView target = (ImageView) event.getGestureTarget();
                Rectangle2D image2 = source.getViewport();
                source.setViewport(target.getViewport());
                target.setViewport(image2);

            });
        }

        return imageViews;
    }

    public GridPane melanger(ArrayList<ImageView> arrayList, int nbPiece) {
        GridPane gridPane = new GridPane();

        Collections.shuffle(arrayList);
        for (int i = 0; i < (int) Math.sqrt(nbPiece); i++) {
            for (int j = 0; j < (int) Math.sqrt(nbPiece); j++) {
                gridPane.add(arrayList.get(3 * i + j), i, j);
            }
        }
        return gridPane;
    }

}
