package com.blue.quicklog.actions

import com.blue.quicklog.settings.QuickLogSettingsState
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
     * 에디터에서 선택된 텍스트를 가져옵니다.
     * 선택된 텍스트가 없으면 커서 위치의 단어를 가져옵니다.
     * 둘 다 없으면 "undefined"를 반환합니다.
     */
    private fun getSelectedText(editor: Editor): String {
        // 1. 선택된 텍스트가 있으면 반환
        editor.selectionModel.selectedText?.let { return it }

        // 2. 커서 위치의 단어 감지
        getWordAtCaret(editor)?.let { return it }

        // 3. 둘 다 없으면 undefined
        return "undefined"
    }

    /**
     * 커서 위치의 단어를 가져옵니다. (변수명 등)
     */
    private fun getWordAtCaret(editor: Editor): String? {
        val document = editor.document
        val caretOffset = editor.caretModel.offset
        val text = document.text

        if (caretOffset >= text.length) return null

        // 현재 위치 또는 바로 앞 문자가 단어 문자인지 확인
        val charAtCaret = if (caretOffset < text.length) text[caretOffset] else ' '
        val charBefore = if (caretOffset > 0) text[caretOffset - 1] else ' '

        if (!isWordChar(charAtCaret) && !isWordChar(charBefore)) return null

        // 단어 시작 위치 찾기
        var start = caretOffset
        while (start > 0 && isWordChar(text[start - 1])) {
            start--
        }

        // 단어 끝 위치 찾기
        var end = caretOffset
        while (end < text.length && isWordChar(text[end])) {
            end++
        }

        if (start == end) return null

        return text.substring(start, end)
    }

    /**
     * 단어를 구성하는 문자인지 확인 (영문, 숫자, _, $)
     */
    private fun isWordChar(char: Char): Boolean {
        return char.isLetterOrDigit() || char == '_' || char == '$'
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
     * 설정에서 템플릿을 가져와서 변수를 치환하여 로그 문을 생성합니다.
     */
    private fun createLogStatement(fileName: String, lineNumber: Int, selectedText: String, indentation: String): String {
        val template = QuickLogSettingsState.getInstance().logTemplate
        val logContent = template
            .replace("\${file}", fileName)
            .replace("\${line}", lineNumber.toString())
            .replace("\${var}", selectedText)
        return "\n${indentation}$logContent"
    }

    /**
     * Live Template 방식으로 console.log를 삽입합니다.
     * 템플릿에서 ${var} 위치를 찾아서 첫 번째는 편집 가능하게, 나머지는 자동 복사되게 합니다.
     * Tab으로 다음 위치로 이동, Enter로 완료됩니다.
     */
    private fun insertTemplateLog(editor: Editor, project: Project, fileName: String, lineNumber: Int, indentation: String) {
        val document = editor.document
        val caretModel = editor.caretModel
        val currentLineNumber = document.getLineNumber(caretModel.offset)
        val lineEndOffset = document.getLineEndOffset(currentLineNumber)

        // 먼저 커서를 라인 끝으로 이동
        caretModel.moveToOffset(lineEndOffset)

        // 설정에서 템플릿 가져오기
        val templateStr = QuickLogSettingsState.getInstance().logTemplate

        // 템플릿에서 ${file}, ${line} 먼저 치환
        val processedTemplate = templateStr
            .replace("\${file}", fileName)
            .replace("\${line}", lineNumber.toString())

        // Live Template 생성
        val templateManager = TemplateManager.getInstance(project)
        val template = templateManager.createTemplate("", "")

        // 템플릿 구성: \n{indentation} + 처리된 템플릿
        template.addTextSegment("\n$indentation")

        // ${var} 위치를 찾아서 처리
        var isFirstVar = true
        var remaining = processedTemplate

        while (remaining.contains("\${var}")) {
            val index = remaining.indexOf("\${var}")
            // ${var} 앞부분 추가
            if (index > 0) {
                template.addTextSegment(remaining.substring(0, index))
            }

            // 변수 추가
            if (isFirstVar) {
                template.addVariable("VAR", ConstantNode(""), true)  // 첫 번째 변수 위치 (편집 가능)
                isFirstVar = false
            } else {
                template.addVariableSegment("VAR")  // 이후 변수 위치 (자동 복사)
            }

            remaining = remaining.substring(index + "\${var}".length)
        }

        // 남은 부분 추가
        if (remaining.isNotEmpty()) {
            template.addTextSegment(remaining)
        }

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
