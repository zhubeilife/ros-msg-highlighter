package org.ros.clion.highlighter;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * Simple syntax highlighter for ROS Interface files.
 * This highlighter maps token types to text attributes.
 *
 * Note: In practice, when using TextMate bundles, the TextMateSyntaxHighlighterFactory
 * from the TextMate plugin handles highlighting. This class provides a fallback for
 * cases where the TextMate bundle isn't loaded.
 */
public class RosSyntaxHighlighter extends SyntaxHighlighterBase {

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new RosInterfaceLexer();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (tokenType == RosInterfaceTokenType.COMMENT) {
            return pack(RosSyntaxHighlighterKeys.COMMENT);
        }
        if (tokenType == RosInterfaceTokenType.BUILTIN_TYPE) {
            return pack(RosSyntaxHighlighterKeys.BUILTIN_TYPE);
        }
        if (tokenType == RosInterfaceTokenType.SUPPORT_TYPE) {
            return pack(RosSyntaxHighlighterKeys.SUPPORT_TYPE);
        }
        if (tokenType == RosInterfaceTokenType.FIELD) {
            return pack(RosSyntaxHighlighterKeys.FIELD);
        }
        if (tokenType == RosInterfaceTokenType.NUMBER) {
            return pack(RosSyntaxHighlighterKeys.NUMBER);
        }
        if (tokenType == RosInterfaceTokenType.STRING) {
            return pack(RosSyntaxHighlighterKeys.STRING);
        }
        if (tokenType == RosInterfaceTokenType.SEPARATOR) {
            return pack(RosSyntaxHighlighterKeys.SEPARATOR);
        }
        if (tokenType == RosInterfaceTokenType.ATTRIBUTE) {
            return pack(RosSyntaxHighlighterKeys.ATTRIBUTE);
        }
        if (tokenType == RosInterfaceTokenType.ARRAY) {
            return pack(RosSyntaxHighlighterKeys.ARRAY);
        }
        return pack(HighlighterColors.TEXT);
    }
}