# quarkus-kotlin-realworld-user

RealWorld API Spec을 준수하는 실제 예제(CRUD 및 인증)가 포함된 Kotlin 기반 Quarkus 애플리케이션 

User 엔터티를 대상으로 CRUD 작업, 간단한 인증 예시, 라우팅 등이 포함되어 있음

## 개발환경 (2023-06-19 기준, 하위호환 확인 X)
* Java 17 installed and set as default
* gradle 8.1.1 or higher installed

## 주요 패키지 구조
```text
.
├── build.gradle.kts
└── src
    ├── main
    │   ├── docker
    │   ├── kotlin
    │   │   └── com
    │   │       └── coway
    │   │           ├── infra
    │   │           │   ├── config
    │   │           │   ├── security
    │   │           │   └── web
    │   │           ├── user
    │   │           │   ├── data
    │   │           │   │   ├── dto
    │   │           │   │   ├── entity
    │   │           │   │   └── repository
    │   │           │   ├── exception
    │   │           │   ├── resource
    │   │           │   └── service
    │   │           └── util
    │   └── resources
    ├── native-test
    └── test
        ├── kotlin
        └── resources
```

## 실행 및 빌드 Script

다음 명령어를 사용해 개발 모드에서 애플리케이션을 실행할 수 있음.

개발 모드에서는 라이브 코딩이 가능함

```shell script
./gradlew quarkusDev
```

아래 명령어를 통해 애플리케이션을 패키징할 수 있음

```shell script
./gradlew build
```
이 명령은 build/quarkus-app/ 디렉토리에 quarkus-run.jar 파일을 생성함 

의존성은 build/quarkus-app/lib/ 디렉토리에 복사됨

애플리케이션은 java -jar build/quarkus-app/quarkus-run.jar 명령을 사용하여 실행할 수 있음

_über-jar_ 로 애플리케이션을 빌드하려면 다음 명령을 실행함

```shell script
./gradlew build -Dquarkus.package.type=uber-jar
```

_über-jar_ 로 패키징된 애플리케이션은 java -jar build/*-runner.jar 명령을 사용하여 실행할 수 있음

> _über-jar_ 는 모든 종속성이 포함되어 있는 JAR 파일임

## 네이티브 실행파일 생성

아래 명령어를 통해 네이티브 실행파일을 생성할 수 있음 
```shell script
./gradlew build -Dquarkus.package.type=native
```

GraalVM(그랄 VM)이 설치되어 있지 않은 경우, 다음 명령을 사용하여 컨터이너 내에서 네이티브 실행 파일 빌드를 실행할 수 있음
```shell script
./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
```

`./build/quarkus-kotlin-realworld-user-0.0.1-SNAPSHOT-runner` 명령어를 통해 네이티브 실행 파일을 실행할 수 있음

## 주요 Dependencies

### Related Guides

- Hibernate ORM with Panache and Kotlin ([guide](https://quarkus.io/guides/hibernate-orm-panache-kotlin)): Hibernate ORM과 Panache를 사용하여 영속 모델을 정의함
- RESTEasy Reactive ([guide](https://quarkus.io/guides/resteasy-reactive)): 빌드 타임 처리와 Vert.x를 활용한 Jakarta REST 구현체
- Kotlin ([guide](https://quarkus.io/guides/kotlin)): 코틀린으로 서비스 작성
- YAML Configuration ([guide](https://quarkus.io/guides/config-yaml)): Quarkus 애플리케이션 구성을 YAML 파일로 작성
- JDBC Driver - MySQL ([guide](https://quarkus.io/guides/datasource)): JDBC를 통해 MySQL을 연결함
