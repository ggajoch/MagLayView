package pl.gajoch.layview.gui.GUI_3D.gradient;

import com.sun.javafx.geom.Vec3d;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import pl.gajoch.layview.graphics3d.options.GradientPoint;
import pl.gajoch.layview.graphics3d.options.HintGradient;
import pl.gajoch.layview.gui.GUI_3D.gradientPoint.GradientPointEditor;
import pl.gajoch.layview.utils.GUIUtils;
import pl.gajoch.layview.utils.gui.RichPane;
import pl.gajoch.layview.utils.gui.RichTextField;

import javax.swing.*;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static pl.gajoch.layview.utils.GUIUtils.showErrorMessage;


public class GradientEditorController {
    private GradientPointEditor editor;
    private JFrame frame;
    private SortedSet<GradientPoint> gradientPoints;
    private HintGradient originalGradient;
    private SimpleObjectProperty<HintGradient> gradientToEdit;
    private Vec3dTextField referenceVector;
    private volatile double minHint, maxHint;

    @FXML
    private ChoiceBox<GradientPoint> choiceBox;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button clearButton;
    @FXML
    private Pane colorPane;
    @FXML
    private Pane gradientViewPane;
    @FXML
    private TextField xRefTextField;
    @FXML
    private TextField yRefTextField;
    @FXML
    private TextField zRefTextField;
    @FXML
    private TextField minVectorTextField;
    @FXML
    private TextField maxVectorTextField;

    public void setup(JFrame frame, SimpleObjectProperty<HintGradient> gradient) {
        this.frame = frame;
        referenceVector = new Vec3dTextField(xRefTextField, yRefTextField, zRefTextField);
        setGradient(gradient);
        recalculateView();
        choiceBox.getSelectionModel().selectFirst();
        recalculateColor();
        recalculateGradient();
        editor = new GradientPointEditor();
        minHint = gradient.get().getHintMin();
        maxHint = gradient.get().getHintMax();

        ContextMenu contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("Get prediction");
        item1.setOnAction(e -> RichTextField.of(minVectorTextField).set(minHint));
        contextMenu.getItems().add(item1);
        minVectorTextField.setContextMenu(contextMenu);

        contextMenu = new ContextMenu();
        item1 = new MenuItem("Get prediction");
        item1.setOnAction(e -> RichTextField.of(maxVectorTextField).set(maxHint));

        contextMenu.getItems().add(item1);
        maxVectorTextField.setContextMenu(contextMenu);

        choiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            recalculateColor(newValue);
        });

        Arrays.asList(xRefTextField, yRefTextField, zRefTextField,
                maxVectorTextField, minVectorTextField)
                .forEach(e -> e.textProperty().addListener(ignore -> recalculateOutput()));
    }

    public void setHints(double minHint, double maxHint) {
        this.minHint = minHint;
        this.maxHint = maxHint;
    }

    private void setGradient(SimpleObjectProperty<HintGradient> gradient) {
        gradientToEdit = gradient;
        originalGradient = new HintGradient(gradientToEdit.getValue());
        this.gradientPoints = new TreeSet<>(originalGradient.getPoints());
        recalculateView();

        referenceVector.set(originalGradient.getReference());

        RichTextField.of(minVectorTextField).set(originalGradient.getMinVector());
        RichTextField.of(maxVectorTextField).set(originalGradient.getMaxVector());
    }

    private void recalculateView() {
        ObservableList<GradientPoint> DescriptionList = FXCollections.observableArrayList();
        DescriptionList.addAll(gradientPoints.stream().collect(Collectors.toList()));
        choiceBox.setItems(DescriptionList);
    }

    private void recalculateColor(Number active) {
        try {
            RichPane.of(colorPane).setFill(choiceBox.getItems().get(active.intValue()));
        } catch (Exception ignored) {
        }
    }

    private void recalculateColor() {
        try {
            GradientPoint point = choiceBox.getValue();
            RichPane.of(colorPane).setFill(point);
            colorPane.setVisible(true);
        } catch (Exception ignore) {
            colorPane.setVisible(false);
        }
    }

    private void recalculateGradient() {
        if (gradientPoints.isEmpty()) {
            gradientViewPane.setVisible(false);
            colorPane.setVisible(false);
            return;
        } else {
            gradientViewPane.setVisible(true);
            colorPane.setVisible(true);
        }

        RichPane.of(gradientViewPane).setFill(gradientToEdit.getValue().getPaint());
    }

    private GradientPoint askForPoint(GradientPoint actualPoint) {
        return editor.exec(actualPoint);
    }

    private boolean addPoint(GradientPoint point) {
        if (point == null)
            return true;
        if (gradientPoints.contains(point)) {
            showErrorMessage("Duplicate values", "Cannot insert point with already existing value!");
            return false;
        }
        gradientPoints.add(point);
        return true;
    }

    private boolean editPoint(GradientPoint prev, GradientPoint now) {
        if (prev == null) {
            return addPoint(now);
        }
        if (now.toString().equals(prev.toString())) {
            gradientPoints.remove(prev);
            return addPoint(now);
        } else {
            boolean successful = addPoint(now);
            if (successful) {
                gradientPoints.remove(prev);
            }
            return successful;
        }
    }

    @FXML
    private void recalculateOutput() {
        try {
            finalRecalculateGradientOutput();
        } catch (Exception ignored) {
        }
    }

    private void finalRecalculateGradientOutput() {
        HintGradient temp = new HintGradient(gradientToEdit.getValue());
        Vec3dTextField reference = new Vec3dTextField(xRefTextField, yRefTextField, zRefTextField);
        temp.setReference(reference.get());

        temp.setMinVector(RichTextField.of(minVectorTextField).getDouble());
        temp.setMaxVector(RichTextField.of(maxVectorTextField).getDouble());

        temp.clear();
        gradientPoints.forEach(temp::add);
        gradientToEdit.setValue(temp);
    }

    @FXML
    private void add_handler() {
        try {
            GradientPoint point = new GradientPoint();
            do {
                point = askForPoint(point);
            } while (!addPoint(point));
            recalculateView();
            choiceBox.getSelectionModel().select(point);
            recalculateOutput();
            recalculateColor();
            recalculateGradient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void edit_handler() {
        try {
            GradientPoint original = choiceBox.getValue();
            GradientPoint point = new GradientPoint(original);
            do {
                point = askForPoint(point);
            } while (!editPoint(original, point));
            recalculateView();
            choiceBox.getSelectionModel().select(point);
            recalculateOutput();
            recalculateColor();
            recalculateGradient();
        } catch (Exception ignored) {
        }
    }

    @FXML
    private void delete_handler() {
        try {
            GradientPoint point = choiceBox.getValue();
            gradientPoints.remove(point);
            recalculateView();
            choiceBox.getSelectionModel().selectFirst();
            recalculateOutput();
            recalculateColor();
            recalculateGradient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clear_handler() {
        try {
            gradientPoints.clear();
            recalculateView();
            recalculateOutput();
            recalculateColor();
            recalculateGradient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ok_handler() {
        try {
            finalRecalculateGradientOutput();
            GUIUtils.closeJFrame(frame);
        } catch (NumberFormatException e) {
            String[] x = e.getMessage().split("\"");
            String name = "";
            if (x.length > 1)
                name = x[1];
            showErrorMessage("Bad number format!", "Cannot parse \"" + name + "\"");
        }
    }

    @FXML
    private void cancel_handler() {
        gradientToEdit.setValue(originalGradient);
        GUIUtils.closeJFrame(frame);
    }

    private static class Vec3dTextField {
        private final RichTextField x, y, z;

        public Vec3dTextField(RichTextField x, RichTextField y, RichTextField z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vec3dTextField(TextField x, TextField y, TextField z) {
            this.x = RichTextField.of(x);
            this.y = RichTextField.of(y);
            this.z = RichTextField.of(z);
        }

        public void set(Vec3d vector) {
            x.set(vector.x);
            y.set(vector.y);
            z.set(vector.z);
        }

        public Vec3d get() throws NumberFormatException {
            return new Vec3d(x.getDouble(), y.getDouble(), z.getDouble());
        }
    }
}
