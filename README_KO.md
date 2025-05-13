# Quick Log

![Build](https://img.shields.io/github/actions/workflow/status/0r0loo/quick-log/build.yml?branch=main)
![Version](https://img.shields.io/badge/version-0.0.1-blue)
![License](https://img.shields.io/badge/license-MIT-green)

*Read this in [English](README.md)*

<!-- Plugin description -->
Quick Log는 선택한 변수나 텍스트를 스타일링된 console.log로 빠르게 삽입해주는 JetBrains IDE 용 플러그인입니다.
<!-- Plugin description end -->

## 기능

Quick Log 플러그인은 자바스크립트/타입스크립트 코드 작성 시 디버깅을 위한 console.log 문을 손쉽게 추가할 수 있는 기능을 제공합니다:

- 변수 선택 후 단축키를 누르면 스타일링된 console.log 문 자동 삽입
- 현재 파일 이름과 라인 번호를 포함한 형식으로 출력
- 선택 없이 단축키를 누르면 멀티 커서를 활용한 템플릿 로그 삽입
- 들여쓰기 자동 유지

## 사용법

1. 코드에서 로깅하고 싶은 변수나 텍스트를 선택합니다.
2. 단축키 `Alt+L`(기본값)을 누릅니다.
3. 선택한 변수에 대한 console.log 문이 현재 라인 아래에 자동으로 삽입됩니다.

## 예시

변수 `userName`을 선택하고 단축키를 누르면:

```javascript
const userName = "John";
console.log('fileName.js:2 | userName : ', userName);
```

선택 없이 단축키를 누르면:
```javascript
console.log('fileName.js:5 | ', );
```
위 상태에서 멀티 커서가 활성화되어 원하는 텍스트를 입력할 수 있습니다.

## 설치

1. JetBrains IDE의 플러그인 마켓플레이스에서 "Quick Log"를 검색합니다.
2. 설치 버튼을 클릭합니다.
3. IDE를 재시작합니다.

## 설정

플러그인 단축키는 IDE의 설정에서 변경할 수 있습니다:

1. `Settings/Preferences` > `Tools` > `Quick Log Settings` 메뉴로 이동합니다.
2. 원하는 단축키 조합을 입력합니다 (예: `ctrl shift L`).
3. 설정을 저장하고 IDE를 재시작합니다.

## 시스템 요구사항

- IntelliJ IDEA, WebStorm, PhpStorm, PyCharm 등 JetBrains IDE
- 242.* 이상 버전 (2024.2 이상)

## 기여하기

버그 리포트나 기능 제안은 GitHub 이슈를 통해 제출해주세요. 풀 리퀘스트도 환영합니다.

## 라이센스

이 프로젝트는 MIT 라이센스 하에 배포됩니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

## 개발자 정보

Made with ❤️ by [0r0loo](https://github.com/0r0loo)

## 변경 이력

최신 변경 사항은 [CHANGELOG.md](CHANGELOG.md) 파일을 참조하세요.
