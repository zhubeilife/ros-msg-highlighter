package org.ros.clion.highlighter;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * Minimal parser for ROS Interface files.
 * Since the main purpose is syntax highlighting, this parser
 * creates a flat structure - it simply advances through tokens.
 */
public class RosInterfaceParser implements PsiParser {

    @Override
    public @NotNull ASTNode parse(@NotNull IElementType root, @NotNull PsiBuilder builder) {
        PsiBuilder.Marker rootMarker = builder.mark();

        // Parse the file: just advance through all tokens
        while (!builder.eof()) {
            PsiBuilder.Marker statementMarker = builder.mark();
            IElementType tokenType = builder.getTokenType();

            if (tokenType == RosInterfaceTokenType.SEPARATOR) {
                parseSeparator(builder);
            } else if (tokenType == RosInterfaceTokenType.COMMENT) {
                builder.advanceLexer();
            } else {
                // Consume the rest of the line as a field definition
                while (!builder.eof() && builder.getTokenType() != RosInterfaceTokenType.SEPARATOR
                        && builder.getTokenType() != RosInterfaceTokenType.COMMENT
                        && !"\n".equals(builder.getTokenText())) {
                    builder.advanceLexer();
                }
            }
            statementMarker.drop();
        }

        rootMarker.done(root);
        return builder.getTreeBuilt();
    }

    private void parseSeparator(PsiBuilder builder) {
        while (!builder.eof() && builder.getTokenType() != RosInterfaceTokenType.COMMENT
                && builder.getTokenType() != RosInterfaceTokenType.WHITESPACE
                && builder.getTokenType() != RosInterfaceTokenType.SEPARATOR) {
            // Skip content until newline/comment
        }
        builder.advanceLexer();
    }
}