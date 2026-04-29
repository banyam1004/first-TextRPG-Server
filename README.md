# 🎮 Java 멀티플레이 텍스트 RPG 게임 서버

Java로 구현한 TCP 소켓 기반 멀티플레이 텍스트 RPG 게임 서버입니다.
여러 클라이언트가 동시에 접속해 협동 던전을 탐험하고 몬스터를 처치할 수 있습니다.

---

## 🛠 기술 스택

- **Language**: Java 21
- **Network**: TCP 소켓 프로그래밍
- **Concurrency**: 멀티스레드 (Thread, Runnable)
- **IDE**: IntelliJ IDEA Community Edition

---

## ✨ 주요 기능

- **멀티플레이 접속**: 여러 클라이언트가 동시에 서버에 접속
- **캐릭터 시스템**: 레벨, HP, 공격력, 경험치, 레벨업
- **인벤토리 시스템**: 아이템 획득 및 사용
- **협동 던전**: 2명이 파티를 구성해 함께 던전 탐험
- **턴제 전투**: 파티원이 순서대로 몬스터 공격
- **틱 시스템**: 20TPS 게임 루프 기반 입력 처리

---

## 📁 프로젝트 구조

```
src/
├── app/          # 메인 실행 (싱글플레이)
├── game/         # 게임 핵심 로직
│   ├── Character.java    # 캐릭터 (레벨, HP, 공격력, 경험치)
│   ├── Monster.java      # 몬스터 (HP, 공격력, 드롭 아이템)
│   ├── Item.java         # 아이템 (회복량, 드롭 확률)
│   └── Inventory.java    # 인벤토리 (아이템 관리)
├── network/      # 네트워크 및 서버 로직
│   ├── GameServer.java   # 서버 (클라이언트 목록 관리, 브로드캐스트)
│   ├── GameClient.java   # 클라이언트
│   ├── ClientHandler.java # 클라이언트 담당 스레드
│   └── GameLoop.java     # 틱 기반 게임 루프
└── system/       # 게임 시스템
    ├── Battle.java       # 전투 시스템
    ├── Dungeon.java      # 던전 시스템 (파티 매칭)
    └── DungeonState.java # 던전 상태 enum
```

---

## ⚙️ 기술적 의사결정

### CopyOnWriteArrayList 사용
클라이언트 목록(`clients`)에 여러 스레드가 동시에 접근하는 상황에서 `ArrayList` 를 쓰면 충돌이 발생할 수 있습니다. 브로드캐스트처럼 읽기 작업이 쓰기보다 훨씬 많은 환경이라 `CopyOnWriteArrayList` 를 선택했습니다.

### 틱 시스템 도입
기존 블로킹 방식(`in.readLine()` 으로 입력 대기)은 입력을 기다리는 동안 스레드가 멈춰 실시간 처리가 불가능했습니다. 20TPS 게임 루프를 도입해 클라이언트 입력을 `ConcurrentLinkedQueue` 에 저장하고 매 틱마다 꺼내 처리하는 방식으로 개선했습니다.

### synchronized + wait/notifyAll → 틱 방식으로 전환
초기에는 파티 매칭 시 `wait()` / `notifyAll()` 로 스레드를 대기시켰으나, 틱 시스템 도입 후 스레드가 잠들면 입력을 받지 못하는 문제가 발생했습니다. 이를 `DungeonState` enum으로 상태를 관리하고 틱마다 상태를 확인하는 방식으로 전환했습니다.

### 서버 권위 모델 (Server Authoritative)
클라이언트끼리 직접 통신하지 않고 모든 게임 상태를 서버에서 관리합니다. 클라이언트는 입력만 전송하고 서버가 검증 후 결과를 전송합니다.

---

## 🚀 실행 방법

**1. 서버 실행**
```
network/GameServer.java 실행
```

**2. 클라이언트 실행 (여러 개 가능)**
```
IntelliJ → Run/Debug Configurations
→ GameClient → Modify options → Allow multiple instances 체크
→ GameClient 여러 번 실행
```

**3. 게임 진행**
```
이름 입력 → 마을에서 던전 입장 선택
→ 2명이 모이면 자동으로 파티 구성
→ 협동으로 몬스터 처치
```

---

## 📌 개발 과정

군 전역 후 Java를 처음 배우면서 프로젝트 기반으로 학습했습니다.

```
1단계: 텍스트 RPG (OOP, 클래스 설계)
2단계: 1:1 채팅 서버 (TCP 소켓, 스트림)
3단계: 멀티 채팅 서버 (멀티스레드, 브로드캐스트)
4단계: RPG + 서버 연동 (패키지 구조, 상태 관리)
5단계: 협동 던전 + 틱 시스템 (게임 루프, 동시성 제어)
```
