# Quick Log Roadmap

## Overview
Quick Log 플러그인의 향후 개발 계획입니다.

---

## Implemented (구현 완료)

- [x] **자동 console.log 생성** - 선택된 변수에 대한 포맷된 로그문 자동 생성 (`Alt+L`)
- [x] **파일명 및 라인번호 포함** - 로그에 현재 파일명:라인번호 정보 포함
- [x] **멀티커서 지원** - 선택 없을 때 2개 커서로 동시 입력 가능
- [x] **들여쓰기 유지** - 현재 라인의 들여쓰기 수준 자동 유지
- [x] **로그 일괄 삭제** - Quick Log로 생성된 console.log 한번에 삭제 (`Alt+Shift+L`)
- [x] **다국어 지원** - 영어/한국어 메시지 지원

---

## High Priority (높은 우선순위)

- [ ] **로그 레벨 선택** - console.warn, console.error, console.info, console.debug 지원
  - 단축키 예시: `Alt+W` (warn), `Alt+E` (error), `Alt+I` (info)

- [ ] **커스텀 로그 템플릿** - 사용자가 로그 포맷을 직접 정의
  - 예: `🔍 ${file}:${line} → ${var}`
  - 설정 UI에서 템플릿 편집 가능

- [ ] **로그 주석 토글** - 기존 console.log를 주석 처리/해제
  - 단축키: `Alt+/` 또는 `Alt+Shift+/`

- [ ] **객체 깊은 복사** - `JSON.stringify(obj, null, 2)` 형태로 객체 로깅 옵션
  - 설정에서 활성화/비활성화 선택

---

## Medium Priority (중간 우선순위)

- [ ] **다중 변수 로깅** - 여러 변수 선택 시 한 줄에 모두 포함
  - 예: `console.log('file:5 | a, b, c : ', a, b, c);`

- [ ] **스타일 로그** - 브라우저 콘솔용 `%c` 스타일 지원 (색상, 배경)
  - 예: `console.log('%cfile:5 | var', 'color: blue; font-weight: bold', var);`

- [ ] **타임스탬프 옵션** - 로그에 `new Date().toISOString()` 추가
  - 설정에서 활성화/비활성화 선택

- [ ] **console.table 지원** - 배열/객체를 테이블 형태로 출력
  - 단축키: `Alt+T`

- [ ] **조건부 로그** - `if (DEBUG)` 조건으로 감싸기 옵션
  - 설정에서 조건문 커스터마이징 가능

---

## Low Priority (낮은 우선순위 / 장기 계획)

- [ ] **다국어 로그 함수** - 언어별 로그 함수 지원
  - Python: `print()`
  - Java: `System.out.println()`
  - Go: `fmt.Println()`
  - Rust: `println!()`

- [ ] **로그 그룹핑** - `console.group()` / `console.groupEnd()` 지원
  - 선택 영역을 그룹으로 감싸기

- [ ] **커스텀 프리픽스** - 프로젝트별 고유 프리픽스 설정
  - 예: `[MyApp]`, `[DEBUG]`

- [ ] **스니펫 연동** - 자주 쓰는 로그 패턴 저장/재사용
  - 최근 사용한 패턴 히스토리

- [ ] **프로젝트 전체 로그 삭제** - 현재 파일뿐 아니라 프로젝트 전체에서 Quick Log 삭제

---

## Contributing

기능 제안이나 버그 리포트는 [GitHub Issues](https://github.com/0r0loo/quick-log/issues)에 등록해주세요.