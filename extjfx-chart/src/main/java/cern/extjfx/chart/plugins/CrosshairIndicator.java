/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

/**Modified by GiuSan82*/

package cern.extjfx.chart.plugins;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.chart.Axis;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

/**
 * Horizontal and vertical {@link Line} drawn on the plot area, crossing at the mouse cursor location, together with a
 * {@link Label} displaying the cursor coordinates in data units.
 * <p>
 * CSS style class names: {@value #STYLE_CLASS_PATH} and {@value #STYLE_CLASS_LABEL}
 * 
 * @param <X> type of X values
 * @param <Y> type of Y values
 */
public class CrosshairIndicator<X, Y> extends AbstractDataFormattingPlugin<X, Y> {
    /**
     * Name of the CSS class of the horizontal and vertical lines path.
     */
    public static final String STYLE_CLASS_PATH = "chart-crosshair-path";

    /**
     * Name of the CSS class of the label displaying mouse coordinates.
     */
    public static final String STYLE_CLASS_LABEL = "chart-crosshair-label";

    private int mLabelXoffset = 15;
    private int mLabelYoffset = 5;
    private int mCursorXoffset;
    private int mCursorYoffset;
    private String mLabelX = "";
    private String mLabelY = "";
    private String mSeparator = "\n";

    private final Path crosshairPath = new Path();
    private final Label coordinatesLabel = new Label();

    /**
     * Creates a new instance of CrosshairIndicator class.
     */
    public CrosshairIndicator() {
        crosshairPath.getStyleClass().add(STYLE_CLASS_PATH);
        crosshairPath.setManaged(false);
        coordinatesLabel.getStyleClass().add(STYLE_CLASS_LABEL);
        coordinatesLabel.setManaged(false);

        registerMouseEventHandler(MouseEvent.MOUSE_MOVED, mouseMoveHandler);
    }

    //setters added by GiuSan82
    public void setCursorXoffset(int cursor_x_offset){this.mCursorXoffset = cursor_x_offset;}
    public void setCursorYoffset(int cursor_y_offset){this.mCursorYoffset = cursor_y_offset;}
    public void setLabelXoffset(int label_x_offset){this.mLabelXoffset = label_x_offset;}
    public void setLabelYoffset(int label_y_offset){this.mLabelYoffset = label_y_offset;}
    public void setLabelX(String label_x){this.mLabelX = label_x;}
    public void setLabelY(String label_y){this.mLabelY = label_y;}
    public void setSeparator(String separator){this.mSeparator = separator;}

    private final EventHandler<MouseEvent> mouseMoveHandler = (MouseEvent event) -> {
        Bounds plotAreaBounds = getChartPane().getPlotAreaBounds();
        if (!plotAreaBounds.contains(event.getX(), event.getY())) {
            getChartChildren().clear();
            return;
        }

        updatePath(event, plotAreaBounds);
        updateLabel(event, plotAreaBounds);

        if (!getChartChildren().contains(crosshairPath)) {
            getChartChildren().addAll(crosshairPath, coordinatesLabel);
        }
    };

    //edited by GiuSan82
    private void updatePath(MouseEvent event, Bounds plotAreaBounds) {
        ObservableList<PathElement> path = crosshairPath.getElements();
        path.clear();
        path.add(new MoveTo(plotAreaBounds.getMinX() + 1, event.getY() + mCursorYoffset));
        path.add(new LineTo(plotAreaBounds.getMaxX(), event.getY() + mCursorYoffset));
        path.add(new MoveTo(event.getX() + mCursorXoffset, plotAreaBounds.getMinY() + 1));
        path.add(new LineTo(event.getX() + mCursorXoffset, plotAreaBounds.getMaxY()));
    }

    private void updateLabel(MouseEvent event, Bounds plotAreaBounds) {
        coordinatesLabel.setText(formatLabelText(getLocationInPlotArea(event)));

        double width = coordinatesLabel.prefWidth(-1);
        double height = coordinatesLabel.prefHeight(width);

        double xLocation = event.getX() + mLabelXoffset;
        double yLocation = event.getY() + mLabelYoffset;

        if (xLocation + width > plotAreaBounds.getMaxX()) {
            xLocation = event.getX() - mLabelXoffset - width;
        }
        if (yLocation + height > plotAreaBounds.getMaxY()) {
            yLocation = event.getY() - mLabelYoffset - height;
        }
        coordinatesLabel.resizeRelocate(xLocation, yLocation, width, height);
    }

    //edited by GiuSan82
    private String formatLabelText(Point2D displayPointInPlotArea) {
        // Support for multiple axes missing
        Axis<Y> yAxis = getChartPane().getChart().getYAxis();
        return formatData(mLabelY, mLabelX, mSeparator, yAxis, toDataPoint(yAxis, displayPointInPlotArea));
    }
}
