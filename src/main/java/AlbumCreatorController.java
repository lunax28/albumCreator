import com.equilibriummusicgroup.albumCreator.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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

    @FXML
    private int songsPerAlbum;

    private int upcCount;


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


    /**
     * Entry point method
     */
    @FXML
    void createAlbumButton(ActionEvent event) {
        //Check for correct entries
        if(this.upcTextArea.getText().isEmpty()){
            displayWarningAlert("Insert a valid list of UPC first!");
            return;
        }

        if(this.folderPathTextField.getText().isEmpty()){
            displayWarningAlert("No folder selected!");
            return;
        }

        //Ask for confirmation before proceeding
        ButtonType bt = displayConfirmationAlert("Before continuing, make sure you have:\n- Inserted your album covers\n- Placed your tracks inside a folder named Track");
        if(bt == ButtonType.OK){
            System.out.println("ButtonType.OK");
        } else{
            System.out.println("CANCELLED");
            return;
        }

        //Store the number of songs per album into a global variable
        try {
            this.songsPerAlbum = Integer.parseInt(this.songsNumberTextField.getText());
        } catch (NumberFormatException e) {
            displayExceptionDialog(e,"Enter a valid number!");
//            displayErrorMessage("Enter a valid number!");
            return;
        }

        System.out.println("createFolders()");
        createFolders();
        System.out.println("sortTracks()");

        try {
            sortTracks();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            displayExceptionDialog(e,e.getMessage());
            return;
        } catch (Exception e) {
            e.printStackTrace();
            displayExceptionDialog(e,e.getMessage());
            return;
        }

        System.out.println("sortAlbumCovers()");
        try {
            sortAlbumCovers();
        } catch (IOException e) {
            e.printStackTrace();
            displayExceptionDialog(e,"Failed to move an album cover!!");
            return;
        }

        if (!itmspCheckbox.isSelected()){
            createItmspPackage();
        }

    }

    /**
     * Helper method to choose the root folder where the albums will be saved to
     */
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

    /**
     * Helper method to create new folders
     */
    private void createFolders(){

        //For each UPC in the list create a folder
        Scanner scanner = new Scanner(this.upcTextArea.getText());
        while (scanner.hasNextLine()) {

            String upc = scanner.nextLine();
            //Check to make sure if the UPCs in the list are correctly formatted
            if (!upc.matches("[0-9]{13}")) {
                upcCount += 1;
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

    /**
     * Helper method to sort all the tracks in each folder
     */
    private void sortTracks() throws Exception {

        //Keep track of tracks folder
        File tracksDir = new File(selectedDirectoryPath + "/tracks");
        //if it doesn't exist, an error message is shown
        if(!tracksDir.exists()){
            displayErrorMessage("tracks folder not created!!");
            return;
        }

        //An array containing all the files inside the tracks folder
        File[] listOfTrackFiles = tracksDir.listFiles();

        if(listOfTrackFiles.length <= 1){
            throw new FileNotFoundException("No files in tracks folder!");
        }

        int totalSongs = this.songsPerAlbum * this.upcCount;

        if(totalSongs < listOfTrackFiles.length){
           throw new Exception("Insufficient track files!!");
        }


        //An array containing all the files and directories inside the root folder
        File[] listOfDir = selectedDirectoryPath.listFiles();

        /*for (File f : listOfDir){
            System.out.println("dir: " + f.getName());
        }*/

        //Create a list with an ordered list of ints up to the length of listOfTrackFiles
        List<Integer> randList = new ArrayList<>();
        for(int i = 0; i < listOfTrackFiles.length; i++){
            randList.add(i);
        }

        System.out.println("original randList: " + randList);

        //first file is _DS STORE!!!!
        for(int i = 0; i < listOfDir.length; i++){

            //for each upc folder inside the root folder
            if(listOfDir[i].isDirectory() && listOfDir[i].getName().matches("[0-9]{13}")) {

                //Choose this.songsPerAlbum tracks to place inside listOfDir[i] folder
                for (int z = 0; z < this.songsPerAlbum; z++) {

                    if(randList.size() < 1){
                        return;
                    }

                    //Pick a random number between 0 and randList's size
                    int indexRandom = ThreadLocalRandom.current().nextInt(0, randList.size());
                    System.out.println("indexRandom: " + indexRandom);

                    //Get the element inside randList corresponding to indexRandom index
                    int indexRandFile = randList.get(indexRandom);

                    System.out.println("FILE NAME: " + listOfTrackFiles[indexRandFile]);

                    //if the element of the listOfTrackFiles array on index: indexRandFile is file
                    if (listOfTrackFiles[indexRandFile].isFile()) {

                        //Move it to the listOfDir[i] folder
                        System.out.println("MOVING TO: " + listOfDir[i].getAbsolutePath() + "/"+ listOfTrackFiles[indexRandFile].getName());
                        try {
                            Files.move(Paths.get(listOfTrackFiles[indexRandFile].getAbsolutePath()), Paths.get(listOfDir[i].getAbsolutePath() + "/"+ listOfTrackFiles[indexRandFile].getName()), StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("File " + listOfTrackFiles[indexRandFile].getName());
                    } else if (listOfTrackFiles[indexRandFile].isDirectory()) {
                        System.out.println("Directory " + listOfTrackFiles[indexRandFile].getName());
                    }

                    //Remove the randList element at index: indexRandom
                    randList.remove(indexRandom);
                    System.out.println("randList updated: " + randList);

                }
            }
        }
    }

    /**
     * Helper method to place all the artwork inside their respective folder
     */
    private void sortAlbumCovers() throws IOException {

        File[] listOfDir = selectedDirectoryPath.listFiles();

        for(int i = 0; i < listOfDir.length-1; i++){

            System.out.println("listOfDir[i].getName(): " + listOfDir[i].getName());
            //if listOfDir[i] is a jpeg
            String ext = FilenameUtils.getExtension(listOfDir[i].getName());
            System.out.println("EXT: " + ext);

            if(ext.equals("jpg") || ext.equals("jpeg")){

                if(!checkImageSize(listOfDir[i])){

                    displayErrorMessage("Check image size!!! Not 3000x3000");
                    return;
                }

                System.out.println("COVER FROM: " + listOfDir[i].getAbsolutePath());
                System.out.println("COVER TO: " + selectedDirectoryPath + "/"+ stripExtension(listOfDir[i].getName()).toString()+ "/" + listOfDir[i].getName());


                Files.move(Paths.get(listOfDir[i].getAbsolutePath()), Paths.get(selectedDirectoryPath + "/"+ stripExtension(listOfDir[i].getName()).toString() + "/" + listOfDir[i].getName()), StandardCopyOption.REPLACE_EXISTING);



            }

        }

    }

    /**
     * Helper method to create iTunes Producer packages
     */
    private void createItmspPackage() {

        File[] listOfDir = selectedDirectoryPath.listFiles();

        for (int i = 0; i < listOfDir.length-1; i++) {
            if (listOfDir[i].isDirectory()) {
                File re = new File(listOfDir[i].getAbsolutePath().concat(".itmsp"));
                listOfDir[i].renameTo(re);
            }
        }
    }

    /**
     * Helper method to check an image's size
     */
    private boolean checkImageSize(File file) {
        double bytes = file.length();

        System.out.println("File Size: " + String.format("%.2f", bytes/1024) + "kb");

        try{

            BufferedImage image = ImageIO.read(file);
            int width = image.getWidth();
            int height = image.getHeight();

            System.out.println("Width: " + width);
            System.out.println("Height: " + height);

            if(width + height < 6000){
                return false;
            }

        } catch (Exception ex){
            ex.printStackTrace();
        }

        return true;
    }

    /**
     * Helper method to extract a file's extension
     */
    private String stripExtension(String str) {

        // Handle null case specially.
        if (str == null) {
            return null;
        }

        // Get position of last '.'.
        int pos = str.lastIndexOf(".");

        // If there wasn't any '.' just return the string as is.
        if (pos == -1) {
            return str;
        }

        // Otherwise return the string, up to the dot.
        return str.substring(0, pos);
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

    private void displayExceptionDialog(Throwable ex, String exceptionMessage) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText("Exception");
        alert.setContentText(exceptionMessage);

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

    /**
     * This method displays the name and the version number of the program,
     * when the About item menu is clicked.
     */
    @FXML
    public void aboutItemAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Album Creator v1.0");
        alert.setHeaderText("Album Creator v1.0\n");
        alert.setContentText("1) Inserire una lista di UPC;\n" +
                "2) Selezionare una cartella vuota nel desktop dentro la quale saranno salvati gli album;\n" +
        "3) Inserire il numero di tracce per album;\n" +
        "4) Inserire le copertine;\n" +
        "5) Decidere se trasformare le cartelle in pacchetti di iTunes Producer con il relativo checkbox;\n" +
        "6) Procedere alla creazione dei file.");
        alert.showAndWait();
    }

}