import com.equilibriummusicgroup.albumCreator.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;


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

        Scanner scanner = new Scanner(this.upcTextArea.getText());

        ButtonType bt = displayConfirmationAlert("Before continuing, make sure you have:\n- Inserted your album covers\n- Placed your tracks inside a folder named Track");

        if(bt == ButtonType.OK){
            System.out.println("IT WORKS!");
        } else{
            System.out.println("CANCELLED");
            return;
        }

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

        File trackDir = new File(selectedDirectoryPath + "/tracks");

        if(!trackDir.exists()){
            displayErrorMessage("tracks folder not created!!");
            return;
        }

        File[] listOfFiles = trackDir.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }

    }



    @FXML
    void chooseFolder(ActionEvent event) {

/*        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Folder");
        this.sourceFolderPath = fileChooser.showOpenDialog(new Stage());

        if(this.sourceFolderPath != null){
            this.folderPathTextField.setText(this.sourceFolderPath.getAbsolutePath().toString());
        }*/

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
}