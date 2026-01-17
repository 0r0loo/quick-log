package com.blue.quicklog.actions

import com.intellij.codeInsight.template.Template
import com.intellij.codeInsight.template.TemplateManager
import com.intellij.codeInsight.template.impl.ConstantNode
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange

/**
 * QuickLogAction 클래스는 선택된 텍스트를 기반으로 console.log 문을 삽입하는 액션을 구현합니다.
 * 이 액션은 에디터에서 선택된 텍스트를 console.log에 감싸서 현재 라인과 동일한 들여쓰기 레벨에 삽입합니다.
 * 선택된 텍스트가 없는 경우 Live Template 방식으로 변수명을 입력받아 자동으로 두 곳에 삽입합니다.
 */
class QuickLogAction: AnAction() {

    /**
     * 액션이 수행될 때 호출되는 메인 메서드입니다.
     */
    override fun actionPerformed(e: AnActionEvent) {
        // 필수 컨텍스트 정보 가져오기
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return

        // 로그 문 생성에 필요한 정보 수집
        val selectedText = getSelectedText(editor)
        val lineNumber = getCurrentLineNumber(editor)
        val fileName = getFileName(e)
        val indentation = getIndentation(editor)

        // 로그 문 생성 (선택 여부에 따라 다름)
        // 새 줄에 삽입되므로 lineNumber + 1
        val insertedLineNumber = lineNumber + 1
        if (selectedText == "undefined") {
            insertTemplateLog(editor, project, fileName, insertedLineNumber, indentation)
        } else {
            val logStatement = createLogStatement(fileName, insertedLineNumber, selectedText, indentation)
            insertLogStatement(editor, project, logStatement)
        }
    }

    /**
     * 에디터에서 선택된 텍스트를 가져옵니다. 선택된 텍스트가 없으면 "undefined"를 반환합니다.
     */
    private fun getSelectedText(editor: Editor): String {
        return editor.selectionModel.selectedText ?: "undefined"
    }

    /**
     * 현재 커서가 위치한 라인 번호를 가져옵니다. (1-based)
     */
    private fun getCurrentLineNumber(editor: Editor): Int {
        val document = editor.document
        val caretOffset = editor.caretModel.offset
        return document.getLineNumber(caretOffset) + 1
    }

    /**
     * 현재 열려있는 파일의 이름을 가져옵니다.
     */
    private fun getFileName(e: AnActionEvent): String {
        return e.getData(CommonDataKeys.VIRTUAL_FILE)?.name ?: "unknown"
    }

    /**
     * 현재 라인의 들여쓰기 텍스트를 가져옵니다.
     */
    private fun getIndentation(editor: Editor): String {
        val document = editor.document
        val caretOffset = editor.caretModel.offset
        val lineNumber = document.getLineNumber(caretOffset)
        val lineStartOffset = document.getLineStartOffset(lineNumber)
        val lineText = document.getText(TextRange(lineStartOffset, caretOffset))

        return lineText.takeWhile { it == ' ' || it == '\t' }
    }

    /**
     * 파일명, 라인 번호, 선택된 텍스트, 들여쓰기 정보를 기반으로 console.log 문을 생성합니다.
     */
    private fun createLogStatement(fileName: String, lineNumber: Int, selectedText: String, indentation: String): String {
        return "\n${indentation}console.log('$fileName:$lineNumber | $selectedText : ', $selectedText);"
    }

    /**
     * Live Template 방식으로 console.log를 삽입합니다.
     * 한 곳에서 변수명을 입력하면 다른 곳에도 자동으로 복사됩니다.
     * Tab으로 다음 위치로 이동, Enter로 완료됩니다.
     */
    private fun insertTemplateLog(editor: Editor, project: Project, fileName: String, lineNumber: Int, indentation: String) {
        val document = editor.document
        val caretModel = editor.caretModel
        val currentLineNumber = document.getLineNumber(caretModel.offset)
        val lineEndOffset = document.getLineEndOffset(currentLineNumber)

        // 먼저 커서를 라인 끝으로 이동
        caretModel.moveToOffset(lineEndOffset)

        // Live Template 생성
        val templateManager = TemplateManager.getInstance(project)
        val template = templateManager.createTemplate("", "")

        // 템플릿 구성: \n{indentation}console.log('{fileName}:{lineNumber} | {VAR} : ', {VAR});
        template.addTextSegment("\n${indentation}console.log('$fileName:$lineNumber | ")
        template.addVariable("VAR", ConstantNode(""), true)  // 첫 번째 변수 위치 (편집 가능)
        template.addTextSegment(" : ', ")
        template.addVariableSegment("VAR")  // 두 번째 변수 위치 (자동 복사)
        template.addTextSegment(");")
        template.addEndVariable()  // Tab 후 커서 최종 위치

        // 템플릿 시작
        templateManager.startTemplate(editor, template)
    }

    /**
     * 생성된 로그 문을 현재 라인 끝에 삽입합니다.
     */
    private fun insertLogStatement(editor: Editor, project: Project, logStatement: String) {
        val document = editor.document
        val caretModel = editor.caretModel
        val currentLineNumber = document.getLineNumber(caretModel.offset)

        WriteCommandAction.runWriteCommandAction(project) {
            // 현재 라인 끝에 로그 문 삽입
            val offset = document.getLineEndOffset(currentLineNumber)
            document.insertString(offset, logStatement)

            // 커서를 삽입된 로그 문 끝으로 이동
            caretModel.moveToOffset(offset + logStatement.length)
        }
    }
}