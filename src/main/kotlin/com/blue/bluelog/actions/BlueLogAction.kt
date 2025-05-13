package com.blue.bluelog.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange

/**
 * BlueLogAction 클래스는 선택된 텍스트를 기반으로 console.log 문을 삽입하는 액션을 구현합니다.
 * 이 액션은 에디터에서 선택된 텍스트를 console.log에 감싸서 현재 라인과 동일한 들여쓰기 레벨에 삽입합니다.
 * 선택된 텍스트가 없는 경우 "variableName"이라는 텍스트와 함께 커서 위치에 로그문을 삽입합니다.
 */
class BlueLogAction: AnAction() {
    
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
        val logStatement = if (selectedText == "undefined") {
            insertMultiCursorLog(editor, project, fileName, lineNumber, indentation)
            return
        } else {
            createLogStatement(fileName, lineNumber, selectedText, indentation)
        }
        
        // 생성된 로그 문 삽입
        insertLogStatement(editor, project, logStatement, selectedText == "undefined")
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
     * 선택된 텍스트가 없을 때 템플릿 변수명과 함께 커서 위치에 console.log 문을 생성합니다.
     */
    private fun createCursorLogStatement(fileName: String, lineNumber: Int, indentation: String): String {
        return "\n${indentation}console.log('$fileName:$lineNumber | variableName : ', variableName);"
    }


    fun insertMultiCursorLog(editor: Editor, project: Project, fileName: String, lineNumber: Int, indentation: String) {
        val caretModel = editor.caretModel
        val document = editor.document

        WriteCommandAction.runWriteCommandAction(project) {
            val primaryCaret = caretModel.primaryCaret
            val currentOffset = primaryCaret.offset

            // 로그 텍스트 삽입
            val logStatement = "${indentation}console.log('$fileName:$lineNumber | ', );"
            document.insertString(currentOffset, "\n$logStatement")

            // 커서 위치 계산
            val baseOffset = currentOffset + 1 // 줄바꿈
            val offset1 = baseOffset + logStatement.indexOf("| ") + 2
            val offset2 = baseOffset + logStatement.indexOf(", )") + 2

            // 우리가 원하는 위치에 커서 추가
            val visualPos1 = editor.logicalToVisualPosition(editor.offsetToLogicalPosition(offset1))
            val visualPos2 = editor.logicalToVisualPosition(editor.offsetToLogicalPosition(offset2))

            caretModel.addCaret(visualPos1)?.moveToOffset(offset1)
            caretModel.addCaret(visualPos2)?.moveToOffset(offset2)

            // 기본 커서는 그냥 뒤로 보내거나 무시
            primaryCaret.moveToOffset(offset2)
        }
    }
    
    /**
     * 생성된 로그 문을 현재 라인 끝에 삽입합니다.
     */
    private fun insertLogStatement(editor: Editor, project: Project, logStatement: String, isCursorLog: Boolean) {
        val document = editor.document
        val caretModel = editor.caretModel
        val currentLineNumber = document.getLineNumber(caretModel.offset)
        
        WriteCommandAction.runWriteCommandAction(project) {
            // 현재 라인 끝에 로그 문 삽입
            val offset = document.getLineEndOffset(currentLineNumber)
            document.insertString(offset, logStatement)
            
            if (isCursorLog) {
                // "variableName" 텍스트의 첫 번째 위치 찾기 (로그 레이블 부분)
                val firstVarPos = offset + logStatement.indexOf("variableName")

                // 커서를 그 위치로 이동하고 텍스트 선택
                caretModel.moveToOffset(firstVarPos)
                editor.selectionModel.setSelection(firstVarPos, firstVarPos + "variableName".length)
            }
        }
    }
}