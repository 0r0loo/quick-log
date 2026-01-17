package com.blue.quicklog.settings

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

/**
 * 설정 페이지를 제공하는 클래스입니다.
 * 사용자가 IDE의 설정 메뉴에서 이 플러그인의 설정을 변경할 수 있게 합니다.
 */
class QuickLogSettingsConfigurable : Configurable {
    private var settingsComponent: QuickLogSettingsComponent? = null

    override fun getDisplayName(): String = "Quick Log"

    override fun getPreferredFocusedComponent(): JComponent? = settingsComponent?.getComponent()

    override fun createComponent(): JComponent {
        settingsComponent = QuickLogSettingsComponent()
        reset()
        return settingsComponent!!.getComponent()
    }

    override fun isModified(): Boolean {
        val settings = QuickLogSettingsState.getInstance()
        val component = settingsComponent ?: return false
        return component.getPreferredShortcutKey() != settings.shortcutKey ||
               component.getLogTemplate() != settings.logTemplate
    }

    override fun apply() {
        val settings = QuickLogSettingsState.getInstance()
        settingsComponent?.let { component ->
            settings.shortcutKey = component.getPreferredShortcutKey()
            settings.logTemplate = component.getLogTemplate()
        }
    }

    override fun reset() {
        val settings = QuickLogSettingsState.getInstance()
        settingsComponent?.let { component ->
            component.setPreferredShortcutKey(settings.shortcutKey)
            component.setLogTemplate(settings.logTemplate)
        }
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}
