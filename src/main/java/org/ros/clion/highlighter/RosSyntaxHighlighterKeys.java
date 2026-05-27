package org.ros.clion.highlighter;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

/**
 * Defines TextAttributesKey constants for ROS Interface syntax highlighting.
 * These keys map to the TextMate scope names and control the coloring.
 */
public class RosSyntaxHighlighterKeys {

    // Line comments: #
    public static final TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey(
            "ROS_INTERFACE_COMMENT",
            DefaultLanguageHighlighterColors.LINE_COMMENT
    );

    // Built-in types: bool, int32, float64, string, etc.
    public static final TextAttributesKey BUILTIN_TYPE = TextAttributesKey.createTextAttributesKey(
            "ROS_INTERFACE_BUILTIN_TYPE",
            DefaultLanguageHighlighterColors.KEYWORD
    );

    // Support types: package/Type references like std_msgs/Header
    public static final TextAttributesKey SUPPORT_TYPE = TextAttributesKey.createTextAttributesKey(
            "ROS_INTERFACE_SUPPORT_TYPE",
            DefaultLanguageHighlighterColors.PREDEFINED_SYMBOL
    );

    // Field names
    public static final TextAttributesKey FIELD = TextAttributesKey.createTextAttributesKey(
            "ROS_INTERFACE_FIELD",
            DefaultLanguageHighlighterColors.INSTANCE_FIELD
    );

    // Constants (UPPERCASE field identifier after type)
    public static final TextAttributesKey CONSTANT = TextAttributesKey.createTextAttributesKey(
            "ROS_INTERFACE_CONSTANT",
            DefaultLanguageHighlighterColors.CONSTANT
    );

    // Integer numbers
    public static final TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey(
            "ROS_INTERFACE_NUMBER",
            DefaultLanguageHighlighterColors.NUMBER
    );

    // Float numbers
    public static final TextAttributesKey FLOAT = TextAttributesKey.createTextAttributesKey(
            "ROS_INTERFACE_FLOAT",
            DefaultLanguageHighlighterColors.NUMBER
    );

    // Boolean literals (true/false)
    public static final TextAttributesKey BOOLEAN = TextAttributesKey.createTextAttributesKey(
            "ROS_INTERFACE_BOOLEAN",
            DefaultLanguageHighlighterColors.KEYWORD
    );

    // Quoted strings
    public static final TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey(
            "ROS_INTERFACE_STRING",
            DefaultLanguageHighlighterColors.STRING
    );

    // Unquoted strings (default values)
    public static final TextAttributesKey STRING_UNQUOTED = TextAttributesKey.createTextAttributesKey(
            "ROS_INTERFACE_STRING_UNQUOTED",
            DefaultLanguageHighlighterColors.STRING
    );

    // String escape sequences
    public static final TextAttributesKey STRING_ESCAPE = TextAttributesKey.createTextAttributesKey(
            "ROS_INTERFACE_STRING_ESCAPE",
            DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE
    );

    // Array brackets and array meta
    public static final TextAttributesKey ARRAY = TextAttributesKey.createTextAttributesKey(
            "ROS_INTERFACE_ARRAY",
            DefaultLanguageHighlighterColors.BRACKETS
    );

    // Separator (---, ===, MSG:)
    public static final TextAttributesKey SEPARATOR = TextAttributesKey.createTextAttributesKey(
            "ROS_INTERFACE_SEPARATOR",
            DefaultLanguageHighlighterColors.METADATA
    );

    // @optional attribute modifier
    public static final TextAttributesKey ATTRIBUTE = TextAttributesKey.createTextAttributesKey(
            "ROS_INTERFACE_ATTRIBUTE",
            DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE
    );

    private RosSyntaxHighlighterKeys() {
    }
}