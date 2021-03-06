package utils.dragAndDrop;

import javafx.scene.input.DataFormat;

import java.io.Serializable;

public interface IDraggable extends Serializable {

    DataFormat getDataFormat();
    boolean isDraggable();
}
