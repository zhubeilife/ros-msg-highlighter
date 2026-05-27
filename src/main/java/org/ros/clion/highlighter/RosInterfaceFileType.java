package org.ros.clion.highlighter;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * File type for ROS Interface files (.msg, .srv, .action).
 * Associates the ROS Interface language with file extensions.
 */
public class RosInterfaceFileType extends LanguageFileType {

    public static final RosInterfaceFileType INSTANCE = new RosInterfaceFileType();

    private RosInterfaceFileType() {
        super(RosInterfaceLanguage.INSTANCE);
    }

    @Override
    public @NonNls @NotNull String getName() {
        return "ROSInterface";
    }

    @Override
    public @NlsContexts.Label @NotNull String getDescription() {
        return "ROS Interface files (.msg, .srv, .action)";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "msg";
    }

    @Override
    public @Nullable Icon getIcon() {
        return null;
    }
}