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
        return settingsComponent!!.getComponent()
    }

    override fun isModified(): Boolean {
        val settings = QuickLogSettingsState.getInstance()
        return settingsComponent?.getPreferredShortcutKey() != settings.shortcutKey
    }

    override fun apply() {
        val settings = QuickLogSettingsState.getInstance()
        settings.shortcutKey = settingsComponent?.getPreferredShortcutKey() ?: "alt L"
    }

    override fun reset() {
        val settings = QuickLogSettingsState.getInstance()
        settingsComponent?.setPreferredShortcutKey(settings.shortcutKey)
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}
