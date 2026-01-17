package com.blue.quicklog.settings

import com.blue.quicklog.QuickLogBundle
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.rows
import com.intellij.ui.dsl.builder.columns
import com.intellij.ui.dsl.builder.AlignX
import javax.swing.JComponent
import javax.swing.JLabel

/**
 * 사용자 인터페이스 구성 요소를 제공하는 컴포넌트입니다.
 */
class QuickLogSettingsComponent {
    private var shortcutKeyValue: String = "alt L"
    private var logTemplateValue: String = QuickLogSettingsState.DEFAULT_LOG_TEMPLATE
    private var previewLabel: JLabel = JLabel()

    val panel: DialogPanel = panel {
        group(QuickLogBundle.message("settings.shortcut.group")) {
            row(QuickLogBundle.message("settings.shortcut.label")) {
                textField()
                    .bindText({ shortcutKeyValue }, { shortcutKeyValue = it })
                    .comment(QuickLogBundle.message("settings.shortcut.comment"))
            }

            row {
                comment(QuickLogBundle.message("settings.shortcut.restart"))
            }
        }

        group(QuickLogBundle.message("settings.template.group")) {
            row(QuickLogBundle.message("settings.template.label")) {
                textArea()
                    .bindText({ logTemplateValue }, {
                        logTemplateValue = it
                        updatePreview()
                    })
                    .rows(3)
                    .columns(50)
                    .align(AlignX.FILL)
                    .comment(QuickLogBundle.message("settings.template.comment"))
            }.resizableRow()

            row {
                comment(QuickLogBundle.message("settings.template.variables"))
            }

            row(QuickLogBundle.message("settings.template.preview.label")) {
                cell(previewLabel)
            }

            row {
                button(QuickLogBundle.message("settings.template.reset")) {
                    logTemplateValue = QuickLogSettingsState.DEFAULT_LOG_TEMPLATE
                    updatePreview()
                    panel.reset()
                }
            }
        }
    }

    init {
        updatePreview()
    }

    private fun updatePreview() {
        val preview = logTemplateValue
            .replace("\${file}", "Example.js")
            .replace("\${line}", "42")
            .replace("\${var}", "myVariable")
        previewLabel.text = "<html><code>$preview</code></html>"
    }

    fun getPreferredShortcutKey(): String = shortcutKeyValue

    fun setPreferredShortcutKey(newValue: String) {
        shortcutKeyValue = newValue
    }

    fun getLogTemplate(): String = logTemplateValue

    fun setLogTemplate(newValue: String) {
        logTemplateValue = newValue
        updatePreview()
    }

    fun getComponent(): JComponent = panel
}
