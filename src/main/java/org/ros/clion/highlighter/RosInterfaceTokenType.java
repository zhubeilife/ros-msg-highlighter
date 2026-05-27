package org.ros.clion.highlighter;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Token types for the ROS Interface lexer.
 */
public class RosInterfaceTokenType extends IElementType {

    public static final RosInterfaceTokenType COMMENT = new RosInterfaceTokenType("COMMENT");
    public static final RosInterfaceTokenType BUILTIN_TYPE = new RosInterfaceTokenType("BUILTIN_TYPE");
    public static final RosInterfaceTokenType SUPPORT_TYPE = new RosInterfaceTokenType("SUPPORT_TYPE");
    public static final RosInterfaceTokenType FIELD = new RosInterfaceTokenType("FIELD");
    public static final RosInterfaceTokenType NUMBER = new RosInterfaceTokenType("NUMBER");
    public static final RosInterfaceTokenType STRING = new RosInterfaceTokenType("STRING");
    public static final RosInterfaceTokenType SEPARATOR = new RosInterfaceTokenType("SEPARATOR");
    public static final RosInterfaceTokenType ATTRIBUTE = new RosInterfaceTokenType("ATTRIBUTE");
    public static final RosInterfaceTokenType ARRAY = new RosInterfaceTokenType("ARRAY");
    public static final RosInterfaceTokenType WHITESPACE = new RosInterfaceTokenType("WHITESPACE");
    public static final RosInterfaceTokenType OTHER = new RosInterfaceTokenType("OTHER");

    private RosInterfaceTokenType(@NotNull @NonNls String debugName) {
        super(debugName, RosInterfaceLanguage.INSTANCE);
    }
}