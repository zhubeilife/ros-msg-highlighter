package org.ros.clion.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

/**
 * Color settings page for ROS Interface files.
 * Allows users to customize syntax highlighting colors in CLion settings.
 */
public class RosColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Comment", RosSyntaxHighlighterKeys.COMMENT),
            new AttributesDescriptor("Built-in type", RosSyntaxHighlighterKeys.BUILTIN_TYPE),
            new AttributesDescriptor("Type (msg/package)", RosSyntaxHighlighterKeys.SUPPORT_TYPE),
            new AttributesDescriptor("Field name", RosSyntaxHighlighterKeys.FIELD),
            new AttributesDescriptor("Constant", RosSyntaxHighlighterKeys.CONSTANT),
            new AttributesDescriptor("Number (integer)", RosSyntaxHighlighterKeys.NUMBER),
            new AttributesDescriptor("Number (float)", RosSyntaxHighlighterKeys.FLOAT),
            new AttributesDescriptor("Boolean literal", RosSyntaxHighlighterKeys.BOOLEAN),
            new AttributesDescriptor("String (quoted)", RosSyntaxHighlighterKeys.STRING),
            new AttributesDescriptor("String (unquoted)", RosSyntaxHighlighterKeys.STRING_UNQUOTED),
            new AttributesDescriptor("String escape", RosSyntaxHighlighterKeys.STRING_ESCAPE),
            new AttributesDescriptor("Array brackets", RosSyntaxHighlighterKeys.ARRAY),
            new AttributesDescriptor("Separator (---)", RosSyntaxHighlighterKeys.SEPARATOR),
            new AttributesDescriptor("Attribute modifier", RosSyntaxHighlighterKeys.ATTRIBUTE),
    };

    @Override
    public @NotNull String getDisplayName() {
        return "ROS Interface";
    }

    @Override
    public @Nullable Icon getIcon() {
        return null;
    }

    @Override
    public @NotNull AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public @NotNull ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @Override
    public @NotNull SyntaxHighlighter getHighlighter() {
        return new RosSyntaxHighlighter();
    }

    @Override
    public @NonNls @NotNull String getDemoText() {
        return "# Comment: Odometry message\n" +
               "std_msgs/Header header\n" +
               "string child_frame_id\n" +
               "geometry_msgs/PoseWithCovariance pose\n" +
               "geometry_msgs/TwistWithCovariance twist\n" +
               "---\n" +
               "# Service request/response separator\n" +
               "int32 STATUS_OK = 0\n" +
               "int32 STATUS_ERROR = 1\n" +
               "bool success\n" +
               "string message\n" +
               "---\n" +
               "# Action feedback separator\n" +
               "float64 progress\n" +
               "string[] messages [\"hello\", \"world\"]\n" +
               "@optional string optional_field\n";
    }

    @Override
    public @Nullable Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }
}