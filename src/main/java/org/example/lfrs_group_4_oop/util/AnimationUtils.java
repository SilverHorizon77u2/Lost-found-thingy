package org.example.lfrs_group_4_oop.util;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 * Utility library providing reusable animation transitions for JavaFX controls.
 * This class encapsulates complex animation behaviors, including fade transitions
 * for page entry effects, tactile scaling on mouse hovers to signify interactivity,
 * and horizontal shaking animations to signal validation failures or login errors.
 * Declares a private constructor to prevent instantiation in compliance with SonarQube static analysis.
 */
public class AnimationUtils {

    /**
     * Private constructor to prevent direct instantiation of this utility class.
     * Throws an {@link UnsupportedOperationException} if instantiation is attempted via reflection.
     */
    private AnimationUtils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated.");
    }

    /**
     * Applies a linear fade-in transition to a target JavaFX node.
     * Progresses the node's opacity from fully transparent (0.0) to fully opaque (1.0)
     * over a duration of 500 milliseconds.
     *
     * @param node The JavaFX Node (e.g. VBox, Label, Button) to animate.
     */
    public static void applyFadeTransition(Node node) {
        if (node == null) return;
        FadeTransition ft = new FadeTransition(Duration.millis(500), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    /**
     * Attaches tactile mouse hover listeners to a target JavaFX node.
     * Scales the node upwards by 3% (1.03x) upon mouse entry and smoothly reverts it
     * back to its base scale (1.0x) upon mouse exit, creating an intuitive micro-interaction cue.
     *
     * @param node The JavaFX Node (typically a Button or Card) to attach interactive hover listeners to.
     */
    public static void makeTactile(Node node) {
        if (node == null) return;
        ScaleTransition stEnter = new ScaleTransition(Duration.millis(100), node);
        stEnter.setToX(1.03);
        stEnter.setToY(1.03);

        ScaleTransition stExit = new ScaleTransition(Duration.millis(100), node);
        stExit.setToX(1.0);
        stExit.setToY(1.0);

        node.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> stEnter.playFromStart());
        node.addEventHandler(MouseEvent.MOUSE_EXITED, e -> stExit.playFromStart());
    }

    /**
     * Executes a high-frequency horizontal shake animation on a target JavaFX node.
     * Translates the node horizontally back and forth by 10 pixels. Runs a total of
     * 6 alternating cycles (3 full oscillations) with auto-reverse over 50 milliseconds
     * per cycle, immediately resetting the node's translation back to 0 upon completion
     * to prevent layout drift.
     *
     * @param node The JavaFX Node (typically error labels or dialog boxes) to perform the shake cue on.
     */
    public static void shake(Node node) {
        if (node == null) return;
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.setOnFinished(e -> node.setTranslateX(0));
        tt.playFromStart();
    }
}
