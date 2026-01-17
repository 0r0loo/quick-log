package com.blue.quicklog.actions

import com.blue.quicklog.QuickLogBundle
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ui.Messages

/**
 * QuickLogDeleteAction 클래스는 Quick Log로 생성된 console.log 문을 일괄 삭제하는 액션을 구현합니다.
 * Quick Log 패턴: console.log('fileName:lineNumber | ...')
 */
class QuickLogDeleteAction : AnAction() {

    // Quick Log로 생성된 로그를 식별하는 정규식 패턴
    // 패턴: console.log('파일명:숫자 |
    private val quickLogPattern = Regex("""console\.log\('[^']*:\d+\s*\|""")

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return
        val document = editor.document

        val text = document.text
        val lines = text.lines()

        // Quick Log 패턴이 있는 라인 인덱스 찾기 (역순으로 삭제하기 위해)
        val linesToDelete = mutableListOf<Int>()
        lines.forEachIndexed { index, line ->
            if (quickLogPattern.containsMatchIn(line)) {
                linesToDelete.add(index)
            }
        }

        if (linesToDelete.isEmpty()) {
            Messages.showInfoMessage(
                project,
                QuickLogBundle.message("delete.notFound"),
                QuickLogBundle.message("delete.title")
            )
            return
        }

        // 사용자 확인
        val confirm = Messages.showYesNoDialog(
            project,
            QuickLogBundle.message("delete.confirm", linesToDelete.size),
            QuickLogBundle.message("delete.title"),
            Messages.getQuestionIcon()
        )

        if (confirm != Messages.YES) {
            return
        }

        // 역순으로 삭제 (뒤에서부터 삭제해야 인덱스가 밀리지 않음)
        WriteCommandAction.runWriteCommandAction(project) {
            linesToDelete.sortedDescending().forEach { lineIndex ->
                deleteLineAt(document, lineIndex)
            }
        }

        Messages.showInfoMessage(
            project,
            QuickLogBundle.message("delete.success", linesToDelete.size),
            QuickLogBundle.message("delete.title")
        )
    }

    /**
     * 특정 라인을 삭제합니다.
     */
    private fun deleteLineAt(document: com.intellij.openapi.editor.Document, lineIndex: Int) {
        if (lineIndex < 0 || lineIndex >= document.lineCount) return

        val startOffset = document.getLineStartOffset(lineIndex)
        val endOffset = if (lineIndex < document.lineCount - 1) {
            // 마지막 라인이 아니면 줄바꿈 문자까지 삭제
            document.getLineStartOffset(lineIndex + 1)
        } else {
            // 마지막 라인이면 라인 끝까지만 삭제
            document.getLineEndOffset(lineIndex)
        }

        // 마지막 라인이고 이전 라인이 있으면 이전 줄바꿈도 삭제
        val deleteStart = if (lineIndex == document.lineCount - 1 && lineIndex > 0) {
            document.getLineEndOffset(lineIndex - 1)
        } else {
            startOffset
        }

        document.deleteString(deleteStart, endOffset)
    }
}