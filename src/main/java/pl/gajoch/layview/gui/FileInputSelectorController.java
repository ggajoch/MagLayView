package pl.gajoch.layview.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import pl.gajoch.layview.utils.OMFParser;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static pl.gajoch.layview.utils.GUIUtils.showErrorMessage;


public class FileInputSelectorController {
    // ----------------------------- Public API  -----------------------------

    public void setup(Stage stage, FileInput files) {
        this.stage = stage;
        this.fileChoiceBox.setConverter(new FileConverter());
        setFiles(files);
        recalculateView();
        closeButton.setVisible(true);
    }

    public final FileInput getFiles() {
        return this.files;
    }

    public FileInput parseFiles() {
        return files;
    }

    // -------------------------- Private variables  -------------------------

    private Stage stage;
    private FileInput files;

    // --------------------------- Private methods  --------------------------

    private class FileConverter extends StringConverter<File> {
        public String toString(File file) {
            try {
                return file.getName();
            } catch (Exception e) {
                return "";
            }
        }

        public File fromString(String s) {
            return new File(s);
        }
    }

    private void setFiles(FileInput files) {
        try {
            this.files = new FileInput(files);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recalculateView() {
        ObservableList<File> DescriptionList = FXCollections.observableArrayList();
        DescriptionList.addAll(files.inputFiles.stream().collect(Collectors.toList()));
        fileChoiceBox.setItems(DescriptionList);
        fileChoiceBox.getSelectionModel().selectLast();
    }

    private void showProgressBar(String text) {
        this.closeButton.setVisible(false);
        this.progressIndicator.setText(text);
    }

    private void hideProgressBar() {
        this.closeButton.setVisible(true);
        this.progressIndicator.setText("");
    }

    private void setProgress(double val) {
        this.progressBar.setProgress(val);
    }

    // ------------------------------- Objects  ------------------------------

    @FXML
    private ChoiceBox<File> fileChoiceBox;
    @FXML
    private Button addFileButton;
    @FXML
    private Button addFolderButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button closeButton;
    @FXML
    private TextField thresholdField;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressIndicator;


    // --------------------------- button handlers ---------------------------

    @FXML
    private void addFile_handler() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select omf file(s)...");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("OMF file", "*.omf"),
                new FileChooser.ExtensionFilter("All files", "*.*"));
        List<File> fileList = fileChooser.showOpenMultipleDialog(stage);
        new Thread() {
            public void run() {
                Platform.runLater(() -> showProgressBar("Adding ..."));
                double len = fileList.size();
                int iter = 0;

                for(File file : fileList) {
                    if( file == null )
                        continue;
                    if ( ! files.inputFiles.contains(file) ) {
                        files.inputFiles.add(file);
                        setProgress(iter++/len);
                    }
                }
                Platform.runLater(() -> {
                    recalculateView();
                    hideProgressBar();
                });
            }
        }.start();
    }

    @FXML
    private void addFolder_handler() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select folder...");
        File folder = directoryChooser.showDialog(stage);
        new Thread() {
            public void run() {
                Platform.runLater(() -> showProgressBar("Adding ..."));
                double len = 0;
                if (folder != null) {
                    for (File file : folder.listFiles()) {
                        if (!file.isDirectory() && isOMF(file)) {
                            len += 1;
                        }
                    }

                    int iter = 0;
                    for (File file : folder.listFiles()) {
                        if (!file.isDirectory() && isOMF(file)) {
                            if ( ! files.inputFiles.contains(file) ) {
                                files.inputFiles.add(file);
                                setProgress(iter++/len);
                            }
                        }
                    }
                }
                Platform.runLater(() -> {
                    recalculateView();
                    hideProgressBar();
                });
            }
        }.start();
    }

    private boolean isOMF(File file) {
        return file.getName().substring(file.getName().lastIndexOf('.')).equals(".omf");
    }

    @FXML
    private void remove_handler() {
        files.inputFiles.remove(fileChoiceBox.getValue());
        recalculateView();
    }

    @FXML
    private void clear_handler() {
        files.inputFiles.clear();
        recalculateView();
    }

    @FXML
    private void close_handler() {
        try {
            this.files.threshold = RichTextField.of(thresholdField).getDouble();;
        } catch (NumberFormatException ex) {
            showErrorMessage("Bad number format!", "Cannot parse \"" + thresholdField.getText() + "\"");
            return;
        }
        new Thread() {
            public void run() {
                Platform.runLater(() -> showProgressBar("Parsing ..."));
                files.omfDataList.clear();
                int iteration = 0;
                setProgress(0);
                double len = getFiles().inputFiles.size();
                for (File file : getFiles().inputFiles) {
                    files.omfDataList.add(new OMFParser().parseFile(file));
                    double x = iteration++;
                    x /= len;
                    setProgress(x);
                }
                Platform.runLater(() ->
                        stage.close());
            }
        }.start();
    }
}
