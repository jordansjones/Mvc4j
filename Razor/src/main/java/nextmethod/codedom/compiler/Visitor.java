/*
 * Copyright 2014 Jordan S. Jones <jordansjones@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nextmethod.codedom.compiler;

import nextmethod.codedom.CodeArgumentReferenceExpression;
import nextmethod.codedom.CodeArrayCreateExpression;
import nextmethod.codedom.CodeArrayIndexerExpression;
import nextmethod.codedom.CodeAssignStatement;
import nextmethod.codedom.CodeAttachEventStatement;
import nextmethod.codedom.CodeBaseReferenceExpression;
import nextmethod.codedom.CodeBinaryOperatorExpression;
import nextmethod.codedom.CodeCastExpression;
import nextmethod.codedom.CodeCommentStatement;
import nextmethod.codedom.CodeConditionStatement;
import nextmethod.codedom.CodeConstructor;
import nextmethod.codedom.CodeDefaultValueExpression;
import nextmethod.codedom.CodeDelegateCreateExpression;
import nextmethod.codedom.CodeDelegateInvokeExpression;
import nextmethod.codedom.CodeDirectionExpression;
import nextmethod.codedom.CodeEntryPointMethod;
import nextmethod.codedom.CodeEventReferenceExpression;
import nextmethod.codedom.CodeExpressionStatement;
import nextmethod.codedom.CodeFieldReferenceExpression;
import nextmethod.codedom.CodeGotoStatement;
import nextmethod.codedom.CodeIndexerExpression;
import nextmethod.codedom.CodeIterationStatement;
import nextmethod.codedom.CodeLabeledStatement;
import nextmethod.codedom.CodeMemberEvent;
import nextmethod.codedom.CodeMemberField;
import nextmethod.codedom.CodeMemberMethod;
import nextmethod.codedom.CodeMemberProperty;
import nextmethod.codedom.CodeMethodInvokeExpression;
import nextmethod.codedom.CodeMethodReferenceExpression;
import nextmethod.codedom.CodeMethodReturnStatement;
import nextmethod.codedom.CodeObjectCreateExpression;
import nextmethod.codedom.CodeParameterDeclarationExpression;
import nextmethod.codedom.CodePrimitiveExpression;
import nextmethod.codedom.CodePropertyReferenceExpression;
import nextmethod.codedom.CodePropertySetValueReferenceExpression;
import nextmethod.codedom.CodeRemoveEventStatement;
import nextmethod.codedom.CodeSnippetExpression;
import nextmethod.codedom.CodeSnippetTypeMember;
import nextmethod.codedom.CodeThisReferenceExpression;
import nextmethod.codedom.CodeThrowExceptionStatement;
import nextmethod.codedom.CodeTryCatchFinallyStatement;
import nextmethod.codedom.CodeTypeConstructor;
import nextmethod.codedom.CodeTypeOfExpression;
import nextmethod.codedom.CodeTypeReferenceExpression;
import nextmethod.codedom.CodeVariableDeclarationStatement;
import nextmethod.codedom.CodeVariableReferenceExpression;
import nextmethod.codedom.ICodeDomVisitor;

/**
 *
 */
class Visitor implements ICodeDomVisitor {

    private final CodeGenerator cg;

    Visitor(final CodeGenerator cg) {
        this.cg = cg;
    }

    @Override
    public void visit(final CodeArgumentReferenceExpression o) {
        cg.generateArgumentReferenceExpression(o);
    }

    @Override
    public void visit(final CodeArrayCreateExpression o) {
        cg.generateArrayCreateExpression(o);
    }

    @Override
    public void visit(final CodeArrayIndexerExpression o) {
        cg.generateArrayIndexerExpression(o);
    }

    @Override
    public void visit(final CodeBaseReferenceExpression o) {
        cg.generateBaseReferenceExpression(o);
    }

    @Override
    public void visit(final CodeBinaryOperatorExpression o) {
        cg.generateBinaryOperatorExpression(o);
    }

    @Override
    public void visit(final CodeCastExpression o) {
        cg.generateCastExpression(o);
    }

    @Override
    public void visit(final CodeDefaultValueExpression o) {
        cg.generateDefaultValueExpression(o);
    }

    @Override
    public void visit(final CodeDelegateCreateExpression o) {
        cg.generateDelegateCreateExpression(o);
    }

    @Override
    public void visit(final CodeDelegateInvokeExpression o) {
        cg.generateDelegateInvokeExpression(o);
    }

    @Override
    public void visit(final CodeDirectionExpression o) {
        cg.generateDirectionExpression(o);
    }

    @Override
    public void visit(final CodeEventReferenceExpression o) {
        cg.generateEventReferenceExpression(o);
    }

    @Override
    public void visit(final CodeFieldReferenceExpression o) {
        cg.generateFieldReferenceExpression(o);
    }

    @Override
    public void visit(final CodeIndexerExpression o) {
        cg.generateIndexerExpression(o);
    }

    @Override
    public void visit(final CodeMethodInvokeExpression o) {
        cg.generateMethodInvokeExpression(o);
    }

    @Override
    public void visit(final CodeMethodReferenceExpression o) {
        cg.generateMethodReferenceExpression(o);
    }

    @Override
    public void visit(final CodeObjectCreateExpression o) {
        cg.generateObjectCreateExpression(o);
    }

    @Override
    public void visit(final CodeParameterDeclarationExpression o) {
        cg.generateParameterDeclarationExpression(o);
    }

    @Override
    public void visit(final CodePrimitiveExpression o) {
        cg.generatePrimitiveExpression(o);
    }

    @Override
    public void visit(final CodePropertyReferenceExpression o) {
        cg.generatePropertyReferenceExpression(o);
    }

    @Override
    public void visit(final CodePropertySetValueReferenceExpression o) {
        cg.generatePropertySetValueReferenceExpression(o);
    }

    @Override
    public void visit(final CodeSnippetExpression o) {
        cg.generateSnippetExpression(o);
    }

    @Override
    public void visit(final CodeThisReferenceExpression o) {
        cg.generateThisReferenceExpression(o);
    }

    @Override
    public void visit(final CodeTypeOfExpression o) {
        cg.generateTypeOfExpression(o);
    }

    @Override
    public void visit(final CodeTypeReferenceExpression o) {
        cg.generateTypeReferenceExpression(o);
    }

    @Override
    public void visit(final CodeVariableReferenceExpression o) {
        cg.generateVariableReferenceExpression(o);
    }

    @Override
    public void visit(final CodeAssignStatement o) {
        cg.generateAssignStatement(o);
    }

    @Override
    public void visit(final CodeAttachEventStatement o) {
        cg.generateAttachEventStatement(o);
    }

    @Override
    public void visit(final CodeCommentStatement o) {
        cg.generateCommentStatement(o);
    }

    @Override
    public void visit(final CodeConditionStatement o) {
        cg.generateConditionStatement(o);
    }

    @Override
    public void visit(final CodeExpressionStatement o) {
        cg.generateExpressionStatement(o);
    }

    @Override
    public void visit(final CodeGotoStatement o) {
        cg.generateGotoStatement(o);
    }

    @Override
    public void visit(final CodeIterationStatement o) {
        cg.generateIterationStatement(o);
    }

    @Override
    public void visit(final CodeLabeledStatement o) {
        cg.generateLabeledStatement(o);
    }

    @Override
    public void visit(final CodeMethodReturnStatement o) {
        cg.generateMethodReturnStatement(o);
    }

    @Override
    public void visit(final CodeRemoveEventStatement o) {
        cg.generateRemoveEventStatement(o);
    }

    @Override
    public void visit(final CodeThrowExceptionStatement o) {
        cg.generateThrowExceptionStatement(o);
    }

    @Override
    public void visit(final CodeTryCatchFinallyStatement o) {
        cg.generateTryCatchFinallyStatement(o);
    }

    @Override
    public void visit(final CodeVariableDeclarationStatement o) {
        cg.generateVariableDeclarationStatement(o);
    }

    @Override
    public void visit(final CodeConstructor o) {
        cg.generateConstructor(o, cg.getCurrentClass());
    }

    @Override
    public void visit(final CodeEntryPointMethod o) {
        cg.generateEntryPointMethod(o, cg.getCurrentClass());
    }

    @Override
    public void visit(final CodeMemberEvent o) {
        cg.generateEvent(o, cg.getCurrentClass());
    }

    @Override
    public void visit(final CodeMemberField o) {
        cg.generateField(o);
    }

    @Override
    public void visit(final CodeMemberMethod o) {
        cg.generateMethod(o, cg.getCurrentClass());
    }

    @Override
    public void visit(final CodeMemberProperty o) {
        cg.generateProperty(o, cg.getCurrentClass());
    }

    @Override
    public void visit(final CodeSnippetTypeMember o) {
        int indent = cg.getIndent();
        cg.setIndent(0);
        cg.generateSnippetMember(o);

        if (cg.getOptions().isVerbatimOrder()) {
            cg.getOutput().writeLine();
        }
        cg.setIndent(indent);
    }

    @Override
    public void visit(final CodeTypeConstructor o) {
        cg.generateTypeConstructor(o);
    }

}
