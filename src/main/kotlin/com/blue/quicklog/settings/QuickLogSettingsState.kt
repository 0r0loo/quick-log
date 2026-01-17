package com.blue.quicklog.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * 플러그인 설정 상태를 저장하는 클래스입니다.
 * 사용자가 설정한 단축키 값을 저장하고 불러옵니다.
 */
@State(
    name = "com.blue.quicklog.settings.QuickLogSettingsState",
    storages = [Storage("QuickLogSettings.xml")]
)
class QuickLogSettingsState : PersistentStateComponent<QuickLogSettingsState> {
    var shortcutKey: String = "alt L"
    var logTemplate: String = DEFAULT_LOG_TEMPLATE

    companion object {
        const val DEFAULT_LOG_TEMPLATE = "console.log('\${file}:\${line} | \${var} : ', \${var});"

        fun getInstance(): QuickLogSettingsState = ApplicationManager.getApplication().getService(QuickLogSettingsState::class.java)
    }

    override fun getState(): QuickLogSettingsState = this

    override fun loadState(state: QuickLogSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
