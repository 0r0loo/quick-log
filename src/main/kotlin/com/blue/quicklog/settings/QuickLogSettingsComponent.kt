package com.blue.quicklog.settings

import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.bindText
import javax.swing.JComponent

/**
 * 사용자 인터페이스 구성 요소를 제공하는 컴포넌트입니다.
 */
class QuickLogSettingsComponent {
    private var shortcutKeyValue: String = "alt L"

    val panel: DialogPanel = panel {
        row("단축키:") {
            textField()
                .bindText({ shortcutKeyValue }, { shortcutKeyValue = it })
                .comment("단축키를 설정하세요 (예: alt L, ctrl shift C)")
        }

        row {
            comment("변경 후 설정을 저장하고 IDE를 재시작하면 적용됩니다.")
        }
    }

    fun getPreferredShortcutKey(): String = shortcutKeyValue

    fun setPreferredShortcutKey(newValue: String) {
        shortcutKeyValue = newValue
    }

    fun getComponent(): JComponent = panel
}
