package org.ros.clion.highlighter;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

/**
 * PSI file for ROS Interface files.
 */
public class RosInterfaceFile extends PsiFileBase {

    public RosInterfaceFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, RosInterfaceLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return RosInterfaceFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "ROS Interface file";
    }
}