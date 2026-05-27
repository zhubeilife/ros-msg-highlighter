package org.ros.clion.highlighter;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Simple character-based lexer for ROS Interface files (.msg, .srv, .action).
 * Handles: comments (#), separators (---, ===), built-in types,
 * package/type references, field names, numbers, strings, and arrays.
 *
 * This is a fallback lexer. The TextMate bundle provides the actual highlighting;
 * this ensures basic highlighting even without the TextMate plugin.
 */
public class RosInterfaceLexer extends LexerBase {

    private CharSequence buffer;
    private int startOffset;
    private int endOffset;
    private int currentOffset;
    private IElementType currentTokenType;

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.currentOffset = startOffset;
        this.currentTokenType = null;
    }

    @Override
    public int getState() {
        return 0;
    }

    @Override
    public @Nullable IElementType getTokenType() {
        if (currentOffset >= endOffset) {
            return null;
        }
        locateToken();
        return currentTokenType;
    }

    @Override
    public int getTokenStart() {
        return startOffset;
    }

    @Override
    public int getTokenEnd() {
        return endOffset;
    }

    @Override
    public void advance() {
        currentOffset = endOffset;
        currentTokenType = null;
    }

    @Override
    public @NotNull CharSequence getBufferSequence() {
        return buffer;
    }

    @Override
    public int getBufferEnd() {
        return buffer.length();
    }

    private void locateToken() {
        if (currentTokenType != null) return;

        int i = currentOffset;
        if (i >= buffer.length()) return;

        char c = buffer.charAt(i);

        // Whitespace
        if (Character.isWhitespace(c)) {
            startOffset = i;
            while (i < buffer.length() && Character.isWhitespace(buffer.charAt(i))) {
                i++;
            }
            endOffset = i;
            currentTokenType = RosInterfaceTokenType.WHITESPACE;
            return;
        }

        // Comment: #
        if (c == '#') {
            startOffset = i;
            while (i < buffer.length() && buffer.charAt(i) != '\n') {
                i++;
            }
            endOffset = i;
            currentTokenType = RosInterfaceTokenType.COMMENT;
            return;
        }

        // Separator: --- or ===
        if (c == '-' && i + 2 < buffer.length() && buffer.charAt(i + 1) == '-' && buffer.charAt(i + 2) == '-') {
            startOffset = i;
            i += 3;
            endOffset = i;
            currentTokenType = RosInterfaceTokenType.SEPARATOR;
            return;
        }
        if (c == '=' && i + 2 < buffer.length() && buffer.charAt(i + 1) == '=' && buffer.charAt(i + 2) == '=') {
            startOffset = i;
            while (i < buffer.length() && buffer.charAt(i) == '=') {
                i++;
            }
            endOffset = i;
            currentTokenType = RosInterfaceTokenType.SEPARATOR;
            return;
        }

        // MSG: prefix
        if (i + 3 < buffer.length()
            && (c == 'M' || c == 'm')
            && (buffer.charAt(i + 1) == 'S' || buffer.charAt(i + 1) == 's')
            && (buffer.charAt(i + 2) == 'G' || buffer.charAt(i + 2) == 'g')
            && buffer.charAt(i + 3) == ':') {
            startOffset = i;
            while (i < buffer.length() && buffer.charAt(i) != '\n') {
                i++;
            }
            endOffset = i;
            currentTokenType = RosInterfaceTokenType.SEPARATOR;
            return;
        }

        // Array brackets
        if (c == '[' || c == ']') {
            startOffset = i;
            endOffset = i + 1;
            currentTokenType = RosInterfaceTokenType.ARRAY;
            return;
        }

        // Strings: double or single quoted, or unquoted string after '='
        if (c == '"' || c == '\'') {
            startOffset = i;
            char quote = c;
            i++;
            while (i < buffer.length() && buffer.charAt(i) != quote && buffer.charAt(i) != '\n') {
                i++;
            }
            if (i < buffer.length() && buffer.charAt(i) == quote) {
                i++;
            }
            endOffset = i;
            currentTokenType = RosInterfaceTokenType.STRING;
            return;
        }

        // Numbers
        if (c == '-' || c == '+' || Character.isDigit(c)) {
            startOffset = i;
            boolean isFloat = false;
            if (c == '-' || c == '+') {
                i++;
                if (i < buffer.length() && !Character.isDigit(buffer.charAt(i)) && buffer.charAt(i) != '.') {
                    // Not a number after all, backtrack
                    endOffset = i;
                    currentTokenType = RosInterfaceTokenType.OTHER;
                    return;
                }
            }
            // Check for float
            while (i < buffer.length()) {
                char ch = buffer.charAt(i);
                if (Character.isDigit(ch)) {
                    i++;
                } else if (ch == '.' || ch == 'e' || ch == 'E') {
                    isFloat = true;
                    i++;
                } else {
                    break;
                }
            }
            endOffset = i;
            currentTokenType = RosInterfaceTokenType.NUMBER;
            return;
        }

        // Words: identifiers, types, field names, built-in types, @optional
        if (Character.isLetter(c) || c == '_') {
            startOffset = i;
            // @optional attribute
            if (c == '@') {
                i++;
                while (i < buffer.length() && (Character.isLetterOrDigit(buffer.charAt(i)) || buffer.charAt(i) == '_')) {
                    i++;
                }
                endOffset = i;
                currentTokenType = RosInterfaceTokenType.ATTRIBUTE;
                return;
            }
            i++;
            while (i < buffer.length() && (Character.isLetterOrDigit(buffer.charAt(i)) || buffer.charAt(i) == '_' || buffer.charAt(i) == '/')) {
                i++;
            }
            String word = buffer.subSequence(startOffset, i).toString();
            endOffset = i;

            // Check for built-in types
            if (isBuiltinType(word)) {
                currentTokenType = RosInterfaceTokenType.BUILTIN_TYPE;
            } else {
                currentTokenType = RosInterfaceTokenType.FIELD;
            }
            return;
        }

        // Anything else
        startOffset = i;
        endOffset = i + 1;
        currentTokenType = RosInterfaceTokenType.OTHER;
    }

    private boolean isBuiltinType(String word) {
        return word.equals("bool") || word.equals("byte") || word.equals("char")
                || word.equals("int8") || word.equals("int16") || word.equals("int32") || word.equals("int64")
                || word.equals("uint8") || word.equals("uint16") || word.equals("uint32") || word.equals("uint64")
                || word.equals("float32") || word.equals("float64")
                || word.equals("string") || word.equals("wstring")
                || word.equals("time") || word.equals("duration");
    }
}