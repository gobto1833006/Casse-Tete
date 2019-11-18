import com.sun.prism.image.ViewPort;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
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

    public BorderPane borderPane;
    public int nbPiece;
    public Image image2;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Break Head");
        Menu importer = new Menu("Importer");
        MenuItem loadImage = new MenuItem("Load image");
        importer.getItems().addAll(loadImage);
        borderPane = new BorderPane();
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(1000);


        Image image = null;
        ArrayList<ImageView> arrayList = new ArrayList<>();


        loadImage.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Veuillez sélectionner une image");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image png", "*.png"), new FileChooser.ExtensionFilter("Image gif", "*.gif"), new FileChooser.ExtensionFilter("Image jpg", "*.jpg"));
            File fichier = fileChooser.showOpenDialog(primaryStage);

            image2 = image;

            try {
                image2 = new Image(new FileInputStream(fichier.getPath()));
            } catch (Exception e) {

            }


            arrayList.clear();
            arrayList.addAll(setImage(image2, nbPiece));
            GridPane gridPane = melanger(arrayList, (int) Math.sqrt(nbPiece));
            gridPane.setAlignment(Pos.CENTER);
            borderPane.setCenter(gridPane);


        });


        Scene scene = new Scene(borderPane);
        scene.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.M) {
                GridPane gridPane = melanger(arrayList, (int) Math.sqrt(nbPiece));
                gridPane.setAlignment(Pos.CENTER);
                borderPane.setCenter(gridPane);
                if (verification(arrayList)) {
                    alarm(arrayList, (int) Math.sqrt(nbPiece));
                }
            }
        });

        ToggleGroup toggleGroup = new ToggleGroup();
        Menu menuPiece = new Menu("Nombre de pièce");
        nbPiece = 9;
        for (int i = 2; i <= 10; i++) {
            RadioMenuItem radioMenuItem = new RadioMenuItem(Integer.toString(i * i) + " pièces");
            radioMenuItem.setToggleGroup(toggleGroup);
            if (radioMenuItem.getText().equals("9 pièces")) {
                radioMenuItem.setSelected(true);
            }
            menuPiece.getItems().add(radioMenuItem);
            radioMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
                nbPiece = Integer.parseInt(radioMenuItem.getText().split(" ")[0]);
                if (arrayList.size()!=0) {
                    arrayList.clear();
                    arrayList.addAll(setImage(image2,nbPiece));
                    GridPane gridPane = melanger(arrayList, (int) Math.sqrt(nbPiece));
                    gridPane.setAlignment(Pos.CENTER);
                    borderPane.setCenter(gridPane);
                }
            });
        }


        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(importer, menuPiece);
        borderPane.setTop(menuBar);
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
                rectangle2DTableau[((int) Math.sqrt(nbPiece)) * i + j] = new Rectangle2D(largeurPiece * j, hauteurPiece * i, largeurPiece, hauteurPiece);
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
            imageViews.get(i).setX((double) i);
            imageViews.get(i).setY((double) i);

        }


        for (int i = 0; i < imageViews.size(); i++) {
            ClipboardContent contenu = new ClipboardContent();
            int content = i;
            imageViews.get(i).setOnDragDetected(event1 -> {
                Dragboard dragboard = imageViews.get(content).startDragAndDrop(TransferMode.COPY);
                contenu.putImage(imageViews.get(content).snapshot(new SnapshotParameters(), null));
                dragboard.setContent(contenu);
            });
            imageViews.get(i).setOnDragOver(event -> {
                event.acceptTransferModes(TransferMode.COPY);
            });
            imageViews.get(i).setOnDragDropped(event -> {

                ImageView source = (ImageView) event.getGestureSource();
                ImageView target = (ImageView) event.getGestureTarget();
                double y = source.getY();
                source.setY(target.getY());
                target.setY(y);
                Rectangle2D image2 = source.getViewport();
                source.setViewport(target.getViewport());
                target.setViewport(image2);

                if (verification(imageViews)) {
                    alarm(imageViews, (int) Math.sqrt(nbPiece));
                }

            });
        }
        return imageViews;
    }

    public GridPane melanger(ArrayList<ImageView> arrayList, float nbPiece) {
        GridPane gridPane = new GridPane();

        for (int i = 0; i < nbPiece * nbPiece; i++) {
            randomizer(arrayList, nbPiece);
        }

        for (int i = 0; i < arrayList.size(); i++) {
            gridPane.add(arrayList.get(i), (int) (i % nbPiece), (int) (i / nbPiece));
        }
        return gridPane;
    }

    public void randomizer(ArrayList<ImageView> arrayList, float nbPiece) {
        int index1 = (int) (Math.random() * nbPiece * nbPiece);
        int index2 = (int) (Math.random() * nbPiece * nbPiece);
        Rectangle2D rectangle2D = arrayList.get(index1).getViewport();
        double lyl = arrayList.get(index1).getY();
        arrayList.get(index1).setViewport(arrayList.get(index2).getViewport());
        arrayList.get(index1).setY(arrayList.get(index2).getY());
        arrayList.get(index2).setViewport(rectangle2D);
        arrayList.get(index2).setY(lyl);
    }

    public boolean verification(ArrayList<ImageView> arrayList) {

        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).getX() != arrayList.get(i).getY()) {
                return false;
            }
            ;
        }
        return true;
    }

    public void alarm(ArrayList<ImageView> arrayList, float nbPiece) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("BRAVO!!!");
        alert.setHeaderText("VOUS AVEZ GAGNÉ");
        alert.setContentText("Souhaitez vous rejouer?");
        ButtonType resultat = alert.showAndWait().get();
        if (resultat == ButtonType.OK) {
            alert.close();
            GridPane gridPane = melanger(arrayList, nbPiece);
            gridPane.setAlignment(Pos.CENTER);
            borderPane.setCenter(gridPane);
        } else {
            System.exit(0);
        }
    }

}
