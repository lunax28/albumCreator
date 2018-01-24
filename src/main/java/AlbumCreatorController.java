import com.equilibriummusicgroup.albumCreator.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;


public class AlbumCreatorController {

    @FXML
    private Model model;


    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="upcTextArea"
    private TextArea upcTextArea; // Value injected by FXMLLoader

    @FXML // fx:id="songsNumberTextField"
    private TextField songsNumberTextField; // Value injected by FXMLLoader

    @FXML // fx:id="itmspCheckbox"
    private CheckBox itmspCheckbox; // Value injected by FXMLLoader

    @FXML
    void createAlbumButton(ActionEvent event) {

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert upcTextArea != null : "fx:id=\"upcTextArea\" was not injected: check your FXML file 'albumCreatorHome.fxml'.";
        assert songsNumberTextField != null : "fx:id=\"songsNumberTextField\" was not injected: check your FXML file 'albumCreatorHome.fxml'.";
        assert itmspCheckbox != null : "fx:id=\"itmspCheckbox\" was not injected: check your FXML file 'albumCreatorHome.fxml'.";

    }

    public void setModel(Model model) {
        this.model = model;
    }
}
