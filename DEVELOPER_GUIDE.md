# Quick Log 개발자 가이드

## 플러그인 구조

Quick Log 플러그인은 다음과 같은 구조로 구성되어 있습니다:

```
src/main/
├── kotlin/
│   └── com/
│       └── blue/
│           └── quicklog/
│               ├── actions/
│               │   └── QuickLogAction.kt        # 메인 액션 구현
│               └── settings/
│                   ├── QuickLogSettingsComponent.kt    # 설정 UI 컴포넌트
│                   ├── QuickLogSettingsConfigurable.kt # 설정 구성 클래스
│                   └── QuickLogSettingsState.kt        # 설정 상태 저장
└── resources/
    ├── META-INF/
    │   └── plugin.xml                           # 플러그인 설정 파일
    └── messages/                                # 국제화 메시지 (필요시)
```

## 핵심 클래스

### 1. QuickLogAction

`QuickLogAction.kt`는 플러그인의 핵심 기능을 구현하는 클래스입니다. 이 클래스는 다음과 같은 주요 기능을 포함합니다:

- `actionPerformed`: 사용자가 단축키를 누를 때 실행되는 메인 함수
- `getSelectedText`: 에디터에서 선택된 텍스트 가져오기
- `getCurrentLineNumber`: 현재 커서의 라인 번호 가져오기 
- `getFileName`: 현재 파일 이름 가져오기
- `getIndentation`: 현재 라인의 들여쓰기 정보 가져오기
- `createLogStatement`: console.log 문 생성
- `insertMultiCursorLog`: 멀티 커서 지원 로그 삽입
- `insertLogStatement`: 생성된 로그 문 삽입

### 2. 설정 관련 클래스

#### QuickLogSettingsState

`QuickLogSettingsState.kt`는 단축키 설정 등의 사용자 설정을 저장하고 불러오는 클래스입니다. IntelliJ Platform의 `PersistentStateComponent`를 구현하여 설정이 IDE 재시작 후에도 유지됩니다.

#### QuickLogSettingsComponent 

`QuickLogSettingsComponent.kt`는 설정 UI를 구성하는 클래스입니다. IntelliJ UI DSL을 사용하여 단축키 설정 필드를 제공합니다.

#### QuickLogSettingsConfigurable

`QuickLogSettingsConfigurable.kt`는 설정 페이지를 IDE의 설정 메뉴에 통합하는 클래스입니다. 설정 변경 여부 감지 및 적용을 담당합니다.

## 플러그인 등록

`plugin.xml` 파일은 플러그인의 메타데이터와 확장점을 정의합니다:

```xml
<idea-plugin>
    <id>com.0r0loo.quicklog</id>
    <name>Quick log</name>
    <vendor>0r0loo</vendor>

    <description>
        Quick Log 는 선택한 변수나 텍스트를 스타일링된 console.log 로 빠르게 삽입해주는 JetBrains IDE 용 플러그인입니다.
    </description>

    <depends>com.intellij.modules.platform</depends>

    <extensions defaultExtensionNs="com.intellij">
        <!-- 설정 클래스 등록 -->
        <applicationService serviceImplementation="com.blue.quicklog.settings.QuickLogSettingsState"/>
        <applicationConfigurable 
            parentId="tools" 
            instance="com.blue.quicklog.settings.QuickLogSettingsConfigurable" 
            id="com.blue.quicklog.settings.QuickLogSettingsConfigurable" 
            displayName="Quick Log Settings"/>
    </extensions>

    <actions>
        <action id="com.blue.quicklog.actions.QuickLogAction"
                class="com.blue.quicklog.actions.QuickLogAction"
                text="Quick Log"
                description="Insert styled console.log for selected variable">
            <keyboard-shortcut keymap="$default" first-keystroke="alt L"/>
        </action>
    </actions>
</idea-plugin>
```

## 주요 로직 설명

### 1. 로그 문 생성 로직

```kotlin
private fun createLogStatement(fileName: String, lineNumber: Int, selectedText: String, indentation: String): String {
    return "\n${indentation}console.log('$fileName:$lineNumber | $selectedText : ', $selectedText);"
}
```

이 함수는 파일명, 라인 번호, 선택된 텍스트, 들여쓰기 정보를 이용하여 스타일링된 console.log 문을 생성합니다.

### 2. 멀티 커서 로그 삽입 로직

```kotlin
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
```

이 함수는 선택된 텍스트가 없을 때 호출되며, 삽입된 로그 문에 두 개의 커서를 추가하여 라벨과 값을 동시에 편집할 수 있게 합니다.

## 빌드 및 실행

### 플러그인 빌드하기

```bash
./gradlew build
```

### 플러그인 테스트 실행

```bash
./gradlew runIde
```

### 플러그인 패키지 생성

```bash
./gradlew buildPlugin
```

### 플러그인 검증

```bash
./gradlew verifyPlugin
```

## 기여 가이드라인

### 코딩 스타일

- Kotlin 공식 코딩 스타일을 따릅니다.
- 함수 및 클래스에 KDoc 주석을 추가합니다.
- 모든 문자열은 국제화를 위해 resource bundle에서 가져오는 것이 좋습니다.

### 변경 사항 제출

1. 이슈를 생성하거나 기존 이슈에 대한 작업 의사를 표시합니다.
2. 새 브랜치를 생성합니다 (`git checkout -b feature/my-feature`).
3. 변경 사항을 커밋합니다.
4. 브랜치를 푸시하고 Pull Request를 생성합니다.

### 테스트

새로운 기능이나 버그 수정을 추가할 때는 다음을 테스트해야 합니다:

1. 다양한 IDE에서 플러그인이 올바르게 작동하는지 확인
2. 다양한 언어 파일에서 적절히 동작하는지 확인
3. 다양한 커서 위치와 선택 상태에서 올바르게 작동하는지 확인

## 라이센스

이 프로젝트는 MIT 라이센스 하에 배포됩니다. 자세한 내용은 LICENSE 파일을 참조하세요.
