package org.ros.clion.highlighter;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * PSI element for tokens in ROS Interface files.
 */
public class RosInterfacePsiElement extends ASTWrapperPsiElement {

    public RosInterfacePsiElement(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "ROSInterface:" + getNode().getElementType().toString();
    }
}