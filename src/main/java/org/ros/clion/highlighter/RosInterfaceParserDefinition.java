package org.ros.clion.highlighter;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

/**
 * Parser definition for ROS Interface files.
 * Uses the simple lexer and a lightweight parser.
 * For pure syntax highlighting via TextMate, the PSI tree is minimal.
 */
public class RosInterfaceParserDefinition implements ParserDefinition {

    private static final IFileElementType FILE_ELEMENT_TYPE =
            new IFileElementType(RosInterfaceLanguage.INSTANCE);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new RosInterfaceLexer();
    }

    @Override
    public @NotNull PsiParser createParser(Project project) {
        return new RosInterfaceParser();
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return FILE_ELEMENT_TYPE;
    }

    @Override
    public @NotNull TokenSet getWhitespaceTokens() {
        return TokenSet.create(RosInterfaceTokenType.WHITESPACE);
    }

    @Override
    public @NotNull TokenSet getCommentTokens() {
        return TokenSet.create(RosInterfaceTokenType.COMMENT);
    }

    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        return TokenSet.create(RosInterfaceTokenType.STRING);
    }

    @Override
    public @NotNull PsiElement createElement(ASTNode node) {
        return new RosInterfacePsiElement(node);
    }

    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new RosInterfaceFile(viewProvider);
    }

    @Override
    public @NotNull SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }
}