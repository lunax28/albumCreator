import com.equilibriummusicgroup.albumCreator.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;


public class AlbumCreatorController {

    @FXML
    private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="upcTextArea"
    private TextArea upcTextArea; // Value injected by FXMLLoader

    @FXML // fx:id="chooseFolderButton"
    private Button chooseFolderButton; // Value injected by FXMLLoader

    @FXML // fx:id="folderPathTextField"
    private TextField folderPathTextField; // Value injected by FXMLLoader

    @FXML // fx:id="itmspCheckbox"
    private CheckBox itmspCheckbox; // Value injected by FXMLLoader

    @FXML // fx:id="songsNumberTextField"
    private TextField songsNumberTextField; // Value injected by FXMLLoader

    @FXML
    private File selectedDirectoryPath;


    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert upcTextArea != null : "fx:id=\"upcTextArea\" was not injected: check your FXML file 'albumCreatorHome.fxml'.";
        assert chooseFolderButton != null : "fx:id=\"chooseFolderButton\" was not injected: check your FXML file 'albumCreatorHome.fxml'.";
        assert folderPathTextField != null : "fx:id=\"folderPathTextField\" was not injected: check your FXML file 'albumCreatorHome.fxml'.";
        assert itmspCheckbox != null : "fx:id=\"itmspCheckbox\" was not injected: check your FXML file 'albumCreatorHome.fxml'.";
        assert songsNumberTextField != null : "fx:id=\"songsNumberTextField\" was not injected: check your FXML file 'albumCreatorHome.fxml'.";

    }

    public void setModel(Model model) {
        this.model = model;
    }


    @FXML
    void createAlbumButton(ActionEvent event) {

        if(this.upcTextArea.getText().isEmpty()){
            displayWarningAlert("Insert a valid list of UPC first!");
            return;
        }

        if(this.folderPathTextField.getText().isEmpty()){
            displayWarningAlert("No folder selected!");
            return;
        }

        ButtonType bt = displayConfirmationAlert("Before continuing, make sure you have:\n- Inserted your album covers\n- Placed your tracks inside a folder named Track");

        if(bt == ButtonType.OK){
            System.out.println("IT WORKS!");
        } else{
            System.out.println("CANCELLED");
            return;
        }

        createFolders();

/*
        ButtonType bt2 = displayConfirmationAlert("Sorting?");

        if(bt2 == ButtonType.OK){
            System.out.println("IT WORKS!");
        } else{
            System.out.println("CANCELLED");
            return;
        }
*/

        sortTracks();

        //TODO
        //sortAlbumCovers();
        //createItmspPackage();

    }


    private void createFolders(){

        Scanner scanner = new Scanner(this.upcTextArea.getText());
        while (scanner.hasNextLine()) {

            String upc = scanner.nextLine();
            if (!upc.matches("[0-9]{13}")) {
                displayErrorMessage("UPC format error!");
                return;
            }

            System.out.println("new folder: " + selectedDirectoryPath + "/" + upc);

            boolean success = (new File(selectedDirectoryPath + "/"+ upc)).mkdirs();
            if (!success) {
                displayErrorMessage("Error while creating a folder!");
                return;
            }
        }

    }

    private void sortTracks(){

        File tracksDir = new File(selectedDirectoryPath + "/tracks");

        if(!tracksDir.exists()){
            displayErrorMessage("tracks folder not created!!");
            return;
        }

        int songsPerAlbum = Integer.parseInt(this.songsNumberTextField.getText());

        File[] listOfTrackFiles = tracksDir.listFiles();

        File[] listOfDir = selectedDirectoryPath.listFiles();
/*        for (File f : listOfDir){
            System.out.println("dir: " + f.getName());
        }*/

        List<Integer> randList = new ArrayList<>();

        for(int i = 0; i < listOfTrackFiles.length; i++){
            randList.add(i);
        }

        System.out.println("original randList: " + randList);

        //first file is _DS STORE!!!!
        for(int i = 0; i < listOfDir.length; i++){

            if(listOfDir[i].isDirectory() && listOfDir[i].getName().matches("[0-9]{13}")) {

                for (int z = 0; z < songsPerAlbum; z++) {

                    int indexRandom = ThreadLocalRandom.current().nextInt(0, randList.size());
                    System.out.println("indexRandom: " + indexRandom);

                    int indexRandFile = randList.get(indexRandom);

                    System.out.println("FILE NAME: " + listOfTrackFiles[indexRandFile]);

                    if (listOfTrackFiles[indexRandFile].isFile()) {
                        System.out.println("MOVING TO: " + listOfDir[i].getAbsolutePath() + "/"+ listOfTrackFiles[indexRandFile].getName()) ;

                        try {
                            Files.move(Paths.get(listOfTrackFiles[indexRandFile].getAbsolutePath()), Paths.get(listOfDir[i].getAbsolutePath() + "/"+ listOfTrackFiles[indexRandFile].getName()), StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("File " + listOfTrackFiles[indexRandFile].getName());
                    } else if (listOfTrackFiles[indexRandFile].isDirectory()) {
                        System.out.println("Directory " + listOfTrackFiles[indexRandFile].getName());
                    }

                    randList.remove(indexRandom);
                    System.out.println("randList updated: " + randList);

                }
            }
        }
    }



    @FXML
    void chooseFolder(ActionEvent event) {

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Folder");
        String userDir = System.getProperty("user.home");
        File defaultDirectory = new File(userDir + "/Desktop");
        chooser.setInitialDirectory(defaultDirectory);
        this.selectedDirectoryPath = chooser.showDialog(new Stage());

        if(selectedDirectoryPath != null){
            this.folderPathTextField.setText(selectedDirectoryPath.getAbsolutePath().toString());
        }
    }

    private void displayErrorMessage(String textMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Warning!");
        alert.setContentText(textMessage);
        alert.showAndWait();
        return;
    }


    public void displayWarningAlert(String textMessage) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning!");
        alert.setContentText(textMessage);
        alert.showAndWait();
        return;

    }

    public ButtonType displayConfirmationAlert(String textMessage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Wait!");
        alert.setContentText(textMessage);
        alert.getResult();
        alert.showAndWait();
        return alert.getResult();
    }

}