package components.interviewPanel.Controllers;

import components.interviewPanel.ModelCommands.addAnnotationCommand;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import models.Annotation;
import models.InterviewText;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.event.MouseOverTextEvent;

import java.time.Duration;

public class RichTextAreaController {
    private InlineCssTextArea area;
    private InterviewText interviewText;
    private VirtualizedScrollPane<InlineCssTextArea> vsPane;
    private int userCaretPosition;

    public RichTextAreaController(InterviewText interviewText) {
        this.interviewText = interviewText;

        area = new InlineCssTextArea();
        area.setWrapText(true);
        area.setEditable(false);
        area.setParagraphGraphicFactory(LineNumberFactory.get(area));
        area.appendText(interviewText.getText());
        //area.requestFocus();

        setUpPopUp();
        setUpMouseEvent();
    }

    public VirtualizedScrollPane<InlineCssTextArea> getNode() {
        VirtualizedScrollPane<InlineCssTextArea> vsPane = new VirtualizedScrollPane(area);
        this.vsPane = vsPane;
        return vsPane;
    }

    private void setUpPopUp() {
        Popup popup = new Popup();
        Label popupMsg = new Label();
        popupMsg.setStyle(
                "-fx-background-color: black;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 5;");
        popup.getContent().add(popupMsg);

        area.setMouseOverTextDelay(Duration.ofMillis(500));
        area.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, e -> {
            int chIdx = e.getCharacterIndex();
            Point2D pos = e.getScreenPosition();
            popupMsg.setText("Character '" + area.getText(chIdx, chIdx+1) + "' at " + pos);
            popup.show(area, pos.getX(), pos.getY() + 5);
        });
        area.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, e -> {
            popup.hide();
        });
    }

    private void setUpMouseEvent() {
        area.setOnMousePressed(event -> {
            userCaretPosition = area.getCaretPosition();
            System.out.println("area pressed " + userCaretPosition);
        });
    }

    public void setOnMouseReleased(EventHandler eventHandler) {
        area.setOnMouseReleased(eventHandler);
    }




    private Annotation createAnnotation(IndexRange selection) {
        int start = selection.getStart();
        int end = selection.getEnd();
        Annotation a = new Annotation(interviewText, start, end, Color.YELLOW);
        return a;
    }

    private void highlightAnnotation(Annotation a) {
        String css = "-rtfx-background-color: " + a.getCSSColor();
        area.setStyle(a.getStartIndex(), a.getEndIndex(), css);
    }
    private void hideHighlightAnnotation(Annotation a) {
        area.clearStyle(a.getStartIndex(), a.getEndIndex());
    }

    public void annotate() {
        IndexRange selection = area.getSelection();
        if (selection.getStart() != selection.getEnd()) {
            Annotation a = createAnnotation(selection);
            highlightAnnotation(a);
            area.deselect();

            addAnnotationCommand cmd = new addAnnotationCommand(a, interviewText);
            cmd.execute();
            System.out.println(a);

        }
    }

    public void deleteAnnotation() {
        int i = area.getCaretPosition();
        Annotation a = interviewText.getFirstAnnotationByIndex(i);
        if (a != null) {
            System.out.println("deleteAnnotation " + i);
            System.out.println(a);
            hideHighlightAnnotation(a);
        }
    }

    public String getSelectedText() {
        return area.getSelectedText();
    }

    public void deselect() {
        area.deselect();
    }
}
