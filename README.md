# メインモジュール 種類別リファレンス（配布用・md／**タブインデント**）

> 対象: 本体アプリ（`main_app`）
>
> 目的: **「種類名（Entity/Controller など）が何を示すか」を横断で理解**するための早見表
>
> 前提: JPMS無効／公開境界は `api/*`／**全言語タブインデント**／AOP対象実装は **public 非final**

---

## 1. コンポ内（`components/` 配下）— 種類と役割

* **Entity**

  * DB テーブルに対応する永続化クラス（JPA）。
  * `@Getter`・`@NoArgsConstructor(PROTECTED)`／**public setter 禁止**（変更は意図メソッド）。
  * `equals/hashCode` は必要時のみ ID ベース。

* **Repository**

  * Spring Data の問い合わせ窓口（CRUD・導出メソッド）。
  * 付加ロジックは `default` で薄くラップ、重処理は Service 側へ。

* **ServiceImpl**

  * **ユースケース本体**。`@Transactional`・`@PreAuthorize` 集約の**最終防衛層**。
  * `toEntity()` は **private**。**IdBridge** と整合チェック → 保存 → `ViewDto` 組み立て。

* **Form / FormList**

  * 画面（Thymeleaf）との**データバインド**用。`public` getter/setter 必須（可変）。
  * `toInputDto()` で **internal の InputDto** を生成（Controller 内のみ使用）。
  * 初期表示は `static fromViewDto(My…ViewDto)` を基本。

* **FormValidator / FormListValidator**

  * Form の**サーバ側バリデーション**（クロスフィールド含む）。
  * 画面へ返す**FieldError** をここで作る。

* **FormParam**

  * 画面層の**定数束**（属性名・フォーム名・画面専用キー等）。**魔法文字列の集中管理**。

* **`api/dto/`（`My<…>ViewDto` / `My<…><UseCase>InputDto`）**

  * **公開境界の DTO**。原則 **record（不変）**（Java 21）。
  * `ViewDto`＝**出力専用**／`<UseCase>InputDto`＝**越境入力専用**（外部呼び出し Service の引数）。

* **`internal/<コンポ名>InputDto`**

  * **同一モジュール画面 → Service** の**内部入力 DTO**。`record`（不変）。Form から生成（Controller 限定）。

* **Mapper（`To<変換先>…Mapper`）**

  * **変換専用クラス**。命名＝クラス: `To<変換先>`、メソッド: `from<変換元>` で統一。
  * 例：`To<コンポ名>ViewDtoMapper.fromEntity(...)`

* **IdBridge / IdConstants**

  * **ViewId ↔ EntityId 変換**と厳格検証（接頭辞・桁・ゼロ埋め）。
  * regex は **IdRegex/IdConstants** に定数化。最終責務は常に **IdBridge**（DTO の `@Pattern` は早期検証の二重防御）。

* **DB（`<コンポ名>DB`）**

  * **テーブル名/カラム名/制約キー名** 等の**DB 契約定数束**（SQL・メタの単一情報源）。

* **ErrorCode / Exception**

  * コンポの**業務エラー種別**（ErrorCode）と **例外**（`MyRuntimeException` 派生）。
  * UI には一般エラーで返し、内部情報は出さない。

* **Controller**

  * **薄い I/O 層**。`@Valid` で Form 検証 → **エラーは同テンプレ返し**／OK で Service 呼び出し。
  * 遷移は **AppPath/TempPath** の定数のみ参照（直書き禁止）。

* **ControllerAdvice**

  * コンポの**画面向け例外集約**。Flash/Model へ `status/errorCode/errorMessage/traceId` を積む。

* **ValidationMessages（…ValidationMessages / …Contributor）**

  * コンポが**使うメッセージキーの宣言口**（`error.*` / `regex.*` の**キーのみ**列挙）。
  * 実体解決は**グローバル Advice** が一括（画面での手動 `put` は禁止）。

---

## 2. コンポ外（モジュール直下・共通）— 主な機能

* **config（設定）**

  * **SecurityConfig**：URL 保護・認可（`/admin/**` = ROLE_ADMIN 等）・Login/Logout・`permitAll` 範囲。
  * **EntityAndRepositoryConfig**：`@EntityScan` / `@EnableJpaRepositories` 等の**スキャン境界**。

* **config/logging（横断ロギング）**

  * **advice/**

    * **GlobalAppExceptionAdvice**：**業務例外の集約**。`traceId` 発行 → 短期保存 → 権限別に `/user/error` or `/admin/user`。
    * **GlobalValidationMessagesAdvice**：各コンポの **ValidationMessagesContributor** を集約 → **宣言キーを一括解決**して `validationMessages` に搭載（SSOT）。
    * **GlobalPropKeysAdvice**：`PropKey`（キー群）をテンプレに供給。
  * **web/**

    * **ViewIdInterceptor**：**相関 ID（viewId）** を取得/生成し MDC に格納。
    * **LoggingWebConfig**：上記インターセプタ等の MVC 登録。
  * **service/**

    * **ServicePerfAspect**：`@Service` を横断。**500ms 超で WARN**（`SLOW_SERVICE`）。
  * **async/**

    * **MdcTaskDecorator**：**非同期でも MDC（viewId）** を継承。
  * **boot/**

    * **BootLogging**：起動完了時に APP_READY（app/profiles/pid/jvm/dbUrl）を INFO 出力。

* **error（エラー画面の機能）**

  * **api/**

    * **ErrorViewDto**（record）：画面に見せる安全なエラー内容。
    * **ErrorStateService**：`traceId → ErrorViewDto` の短期ストア IF。
  * **internal/**

    * **ErrorStateServiceImpl**：TTL 付き短期保存。
    * **ErrorReportService**：**CSV 出力**（ログから該当 trace 抽出）。
    * **LoginErrorController / USER_ErrorController / ADMIN_ErrorController**：エラー表示（認証前/後・権限で分岐）。

* **security**

  * **AuthSessionRefresher**：ユーザー名変更直後の **principal 差し替え** と **UserCache evict** のユーティリティ。

* **util/param（画面・文言・検証キーの SSOT）**

  * **PropKey**：画面・JS・サーバで共有する**キー定数**（`validationMessages` の置き場名等を統一）。
  * **PropKeyViewUtil**：`PropKey` を**View に載せる**補助（`window.PropKey` へ積む等）。
  * **ValidationMessageUtil**：Contributor から集めた**キーを一括解決**し、モデルに `validationMessages` を搭載。標準キーもここで追加。
  * **ValidationMessagesContributor / BaseValidationMessagesContributor**：各機能の**宣言口**。Base… は**全画面共通**。**宣言 → グローバルで解決**に統一。

* **util/path（ルーティングの SSOT）**

  * **AppPath**：**ルーティング定数**（`/login/error`, `/user/error`, `/admin/user` 等）と **redirect（R_*）**。
  * **TempPath**：**Thymeleaf テンプレ名**の集中管理。
  * **PackagePath**：パッケージ/スキャンの**基点名**定義。

* **validation（Bean Validation：画面入力の最終防衛）**

  * `@ValidUsername` / `@ValidPassword` / `@ValidSelectedOption` / `@ValidBookColorName` / `@ValidAuthorityName`：構文・長さ等のサーバ側ルール。

* **resources（抜粋）**

  * **config/*.properties**：`error.properties` / `labels.properties` / `regex.properties` / `db.properties` 等、**SSOT の実体**。画面側 SSOT は `window.PropKey` と `window.VALIDATION_MESSAGES`（`VALIDATION_SCHEMA_VERSION` を含む）。
  * **sql/schema & data**：**初期化 SQL**（再作成用の“綺麗な DDL”）。
  * **static/js & templates**：フロント 4 分割（param/form/service/controller）＋エラー・各画面。

---

## 3. 運用の定石（Quick チェック）

* **Controller**：Form を `@Valid`。**エラーは同テンプレ返し**／OK なら **Form → InputDto** にして Service 呼び出し。
* **ServiceImpl**：**認可・整合・IdBridge**・保存の本番。`toEntity()` は **private**。
* **Mapper**：変換だけに徹する（命名：**To〈変換先〉／from〈変換元〉**）。
* **IdBridge / DB 定数**：契約の単一情報源（仕様変更はまずここを更新）。
* **Error 系**：`traceId` 短期保存＋**権限分岐**で `/user/error` or `/admin/user`。
* **文言/検証キー**：**Contributor に宣言 → グローバルで解決**。画面での手動 `put` は禁止。
* **AOP 対象実装**：`@Service` / `@Repository` / `@Component` は **public 非final**。
* **インデント**：**全言語タブ**（HTML/Thymeleaf/CSS/JS/SQL/YAML/Markdown 含む）。

---

## 4. 変更時の影響マップ（入口）

* **画面項目追加** → Form/Validator → internal InputDto → ServiceImpl → ViewDto/テンプレ
* **ID 仕様変更** → IdConstants/IdRegex → IdBridge →（必要なら）`api/dto` の `@Pattern`
* **メッセージ追加** → 各コンポの `ValidationMessagesContributor` に**キー宣言** → 画面へ自動搭載
* **遷移先追加** → AppPath/TempPath → Controller が参照
* **権限分岐変更** → SecurityConfig（URL 保護）／Error 遷移の分岐ロジック

---

## 5. 起動方法（初回 → 通常）

本アプリは **Java 21 以上**が必要です。JAR はターミナル／PowerShell からの起動を推奨します（ダブルクリックは作業ディレクトリや環境変数が想定どおりにならない場合があります）。

### 5.1 初回起動（DB 初期化：`application-init.properties` を **クラスパス**から読み込み）

初回のみ、JAR 同梱の **`application-init.properties`** を読み込み、初期化 SQL を実行します。
**秘密鍵（`app.viewid.secret`）は `-D` で渡し、`application-init.properties` には書きません。**

**配置（ビルド時）**

```
src/main/resources/application-init.properties
# → ビルド後は JAR 内: classpath:/application-init.properties
```

**サンプル（JAR 同梱ファイルの内容）**

```properties
# application-init.properties（秘密鍵は書かない）
spring.config.import=classpath:config/db.properties,classpath:config/regex.properties

# スキーマ初期化（初回のみ）
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:sql/schema/001_base.sql
spring.sql.init.data-locations=classpath:sql/data/*.sql
spring.sql.init.separator=@@
spring.sql.init.encoding=UTF-8

spring.jpa.hibernate.ddl-auto=none
spring.jpa.defer-datasource-initialization=true
```

**起動コマンド（クラスパスから自動読込：`--spring.profiles.active=init`）**

* macOS / Linux

```bash
cd /path/to/app
java -Dapp.viewid.secret=<長い秘密鍵> \
  -jar spring_javase11_study-<version>.jar \
  --spring.profiles.active=init
```

* Windows PowerShell

```powershell
cd C:\path\to\app
java -Dapp.viewid.secret=<長い秘密鍵> `
  -jar .\spring_javase11_study-<version>.jar `
  --spring.profiles.active=init
```

> 読み込み確認用（必要な場合）
>
> ```bash
> --logging.level.org.springframework.boot.context.config=DEBUG
> ```
>
> を付与すると、`Loaded config file 'classpath:application-init.properties'` 等のログで読込を確認できます。

**代替起動（クラスパス名を直列挙 or 明示追加）**
どちらも `-Dapp.viewid.secret` を付ける点は同じです。

```bash
# A) 名前直列挙（無印＋init をクラスパスから読む）
java -Dapp.viewid.secret=<長い秘密鍵> \
  -jar spring_javase11_study-<version>.jar \
  --spring.config.name=application,application-init

# B) 追加ロケーション（クラスパスを明示）
java -Dapp.viewid.secret=<長い秘密鍵> \
  -jar spring_javase11_study-<version>.jar \
  --spring.config.additional-location=classpath:application-init.properties
```

> メモ：**秘密鍵は常に `-Dapp.viewid.secret=...` で渡す**。JAR 同梱ファイルや VCS に置かないこと。

---

### 5.2 通常起動（2 回目以降）

初回で DB が整ったら、**無印 `application.properties`** 側は通常運用設定に戻し（例：`spring.sql.init.mode=never`）、以後は通常起動します。
秘密鍵は引き続き `-Dapp.viewid.secret` で渡します。

* macOS / Linux

```bash
cd /path/to/app
java -Dapp.viewid.secret=<長い秘密鍵> -jar spring_javase11_study-<version>.jar
```

* Windows PowerShell

```powershell
cd C:\path\to\app
java -Dapp.viewid.secret=<長い秘密鍵> -jar .\spring_javase11_study-<version>.jar
```

---

### 5.3 動作確認

* ログに `Started ... in X.Y seconds` が出れば起動完了。
* 既定ポート利用時は `http://localhost:8080/` にアクセス。

---

### 5.4 トラブルシュート（抜粋）

* **`secret is missing`**

  * 起動に使っている設定に **`app.viewid.secret` が無い**。必ず `-Dapp.viewid.secret=<長い秘密鍵>` を付けて再起動。
  * （確認）`--logging.level.org.springframework.boot.context.config=DEBUG` で読み込まれた設定レイヤをログ確認。

* **ポートが使用中**

  * macOS / Linux:

    ```bash
    lsof -nP -iTCP:8080 -sTCP:LISTEN
    kill <PID>
    ```
  * Windows（PowerShell 管理者）:

    ```powershell
    netstat -ano | findstr :8080
    taskkill /PID <PID> /F
    ```
  * もしくは `application.properties` で `server.port` を空きポートへ変更。

* **設定ファイルが見つからない**

  * `--spring.profiles.active=init` を使えば、JAR 同梱の `classpath:application-init.properties` が自動読込される。
  * それ以外の場所のファイルを読む場合は `--spring.config.additional-location=file:/絶対パス/...` を使用（`file:` 必須）。

* **終了方法（優雅なシャットダウン）**

  * ターミナルで `Ctrl + C`。`Graceful shutdown complete` が出れば正常終了。

---

**補足（セキュリティ）**
本 README は **開発～検証用途**の起動方法を示します。配布・本番運用では、秘密値は**環境変数や外部シークレット管理**への移行も検討してください（アプリに同梱しない／VCS に載せない）。
