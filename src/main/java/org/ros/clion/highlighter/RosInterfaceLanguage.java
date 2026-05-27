package org.ros.clion.highlighter;

import com.intellij.lang.Language;

/**
 * Represents the ROS Interface language (.msg, .srv, .action files).
 * This serves as the language definition for the IntelliJ Platform.
 */
public class RosInterfaceLanguage extends Language {

    public static final RosInterfaceLanguage INSTANCE = new RosInterfaceLanguage();

    private RosInterfaceLanguage() {
        super("ROSInterface");
    }
}