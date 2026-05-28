package org.ros.highlighter;

import com.intellij.ide.IconProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class RosIconProvider extends IconProvider {

    @Override
    public @Nullable Icon getIcon(@NotNull PsiElement element, int flags) {
        if (!(element instanceof PsiFile file)) return null;
        String name = file.getName();
        if (name.endsWith(".msg"))    return RosIcons.MSG;
        if (name.endsWith(".srv"))    return RosIcons.SRV;
        if (name.endsWith(".action")) return RosIcons.ACTION;
        return null;
    }
}
