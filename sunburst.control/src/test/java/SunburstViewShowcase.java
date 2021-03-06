import controls.sunburst.*;
import data.ISourceStrategy;
import data.SourceStrategyMockup;
import data.SourceStrategySQL;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.controlsfx.control.SegmentedButton;

/**
 * Created by IsNull on 17.04.14.
 */
public class SunburstViewShowcase extends  javafx.application.Application {


    /**
     * Launchable
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) throws Exception {

        stage.setTitle("Sunburst View");

        BorderPane pane = new BorderPane();

        SunburstView sunburstView = new SunburstView();

        // Create all the available color strategies once to be able to use them at runtime.
        ColorStrategyRandom colorStrategyRandom = new ColorStrategyRandom();
        ColorStrategySectorShades colorStrategyShades = new ColorStrategySectorShades();

        WeightedTreeItem<String> rootData = loadData();

        System.out.println("root children: ");
        for (WeightedTreeItem<String> eatable : rootData.getChildrenWeighted()){
            System.out.println(eatable.getValue() + ": " + eatable.getRelativeWeight());
        }

        sunburstView.setRootItem(rootData);
        sunburstView.setColorStrategy(colorStrategyShades);


        // Example Controls

        ToggleButton btnCSShades = new ToggleButton("Shades Color Strategy");
        btnCSShades.setOnAction(event -> {
            sunburstView.setColorStrategy(colorStrategyShades);
        });

        ToggleButton btnCSRandom = new ToggleButton("Random Color Strategy");
        btnCSRandom.setOnAction(event -> {
           sunburstView.setColorStrategy(colorStrategyRandom);
        });

        IColorStrategy colorStrategy = sunburstView.getColorStrategy();
        if(colorStrategy instanceof ColorStrategyRandom){
            btnCSRandom.setSelected(true);
        }else if(colorStrategy instanceof  ColorStrategySectorShades){
            btnCSShades.setSelected(true);
        }


        ToggleButton btnShowLegend = new ToggleButton("Show Legend");
        btnShowLegend.setSelected(true);
        btnShowLegend.setOnAction(event -> {
            //sunburstView.setLegendVisibility(true);
        });

        ToggleButton btnHideLegend = new ToggleButton("Hide Legend");
        btnHideLegend.setOnAction(event -> {
            //sunburstView.setLegendVisibility(false);
        });


        // Max Level drawn

        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(10);
        slider.setValue(sunburstView.getMaxDeepness());
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(5);
        slider.setMinorTickCount(1);
        slider.setBlockIncrement(1);

        slider.valueProperty().addListener(x -> sunburstView.setMaxDeepness((int)slider.getValue()));

        // Zoom level

        Slider zoomSlider = new Slider();
        zoomSlider.setMin(0.1);
        zoomSlider.setMax(3);
        zoomSlider.setValue(sunburstView.getScaleX());
        zoomSlider.setShowTickLabels(true);
        zoomSlider.setShowTickMarks(true);
        zoomSlider.setMajorTickUnit(0.5);
        zoomSlider.setMinorTickCount(1);
        zoomSlider.setBlockIncrement(0.1);

        zoomSlider.valueProperty().addListener(x -> {
            double scale = zoomSlider.getValue();
            sunburstView.setScaleX(scale);
            sunburstView.setScaleY(scale);
        });

        HBox toolbar = new HBox(20);
        BorderPane.setMargin(toolbar, new Insets(10));

        SegmentedButton colorStrategies = new SegmentedButton();
        colorStrategies.getButtons().addAll(btnCSShades, btnCSRandom);

        SegmentedButton legendVisibility = new SegmentedButton();
        legendVisibility.getButtons().addAll(btnShowLegend, btnHideLegend);

        toolbar.getChildren().addAll(colorStrategies, slider, legendVisibility, zoomSlider);

        pane.setTop(toolbar);

        pane.setCenter(sunburstView);
        BorderPane.setAlignment(sunburstView, Pos.CENTER);
        SunburstLegend myLegend = new SunburstLegend(sunburstView);
        pane.setRight(myLegend);
        BorderPane.setMargin(myLegend, new Insets(20));
        BorderPane.setAlignment(myLegend, Pos.CENTER_LEFT);

        stage.setScene(new Scene(pane, 1080, 800));
        stage.show();

        Event.fireEvent(sunburstView, new SunburstView.VisualChangedEvent());
    }

    private WeightedTreeItem<String> loadData() {

        // Define a strategy by which the data should be received.
        ISourceStrategy sourceStrategy = new SourceStrategyMockup();
        //ISourceStrategy sourceStrategy = new SourceStrategySQL();

        String databasename = null;
        String user = null;
        String password = null;

        Parameters parameters = getParameters();
        if (parameters.getUnnamed().size() >= 3) {
            databasename = parameters.getUnnamed().get(0);
            user = parameters.getUnnamed().get(1);
            password = parameters.getUnnamed().get(2);
        }

        return sourceStrategy.getData(databasename, user, password);
    }

    /**
     * Build test data model
     * @return
     */
    private WeightedTreeItem<String> getDataL1(){
        WeightedTreeItem<String> root = new WeightedTreeItem(1, "eatables");

        WeightedTreeItem<String> meat = new WeightedTreeItem(3, "meat");
        WeightedTreeItem<String> potato = new WeightedTreeItem(5, "potato");
        WeightedTreeItem<String> fruits = new WeightedTreeItem(10, "fruits");


        root.getChildren().addAll(fruits, meat, potato);

        return root;
    }
}
