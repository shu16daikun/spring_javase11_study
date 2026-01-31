# 整形/Spotless・Prettier運用ランブック（Eclipse／マルチモジュール）

最終更新: 2025-10-23

---

## ゴール

* 全モジュールを **ワンコマンド** で整形 & 検証（Spotless, optional: Prettier, optional: Tests）
* Eclipse（日本語UI）からも同じフローを実行
* よくあるハマりを即解消（JDT/Lombok/Prettier/Path）

---

## 1) ワンコマンド: `fmt.sh`

プロジェクトのワークスペース直下に保存して実行。`mvn` の場所を **自動検出** するので、PATH が細い環境でも動作します。

> 保存後に `chmod +x ./fmt.sh`

```bash
#!/usr/bin/env sh
# POSIX / zsh / 古い bash OK（タブインデント推奨）

set -eu

WORKSPACE="${1:-$(pwd)}"
WITH_PRETTIER="${WITH_PRETTIER:-0}"
RUN_TESTS="${RUN_TESTS:-0}"

# --- mvn の場所を自動検出 ---------------------------------------------------
if command -v mvn >/dev/null 2>&1; then
	MVN="$(command -v mvn)"
else
	for CAND in \
		"/opt/homebrew/bin/mvn" \
		"/usr/local/bin/mvn" \
		"/usr/bin/mvn"
	do
		if [ -x "$CAND" ]; then
			MVN="$CAND"
			break
		fi
	done
fi
if [ -z "${MVN:-}" ] || [ ! -x "$MVN" ]; then
	echo "ERROR: mvn が見つかりません。'which mvn' の結果を MVN へ直書きしてください。"
	exit 127
fi
# ---------------------------------------------------------------------------

if [ "$RUN_TESTS" = "1" ]; then
	SKIP_TESTS=""
else
	SKIP_TESTS="-DskipTests"
fi

if [ "$WITH_PRETTIER" = "1" ]; then
	PRFL="-Pwith-prettier"
else
	PRFL=""
fi

echo "==> workspace: $WORKSPACE"
echo "==> mvn      : $MVN"
echo "==> options  : WITH_PRETTIER=$WITH_PRETTIER, RUN_TESTS=$RUN_TESTS"

TMP_FILE="$(mktemp)"
find "$WORKSPACE" -maxdepth 2 -type f -name pom.xml \
	! -path "*/.metadata/*" \
	! -path "*/.git/*" \
	! -path "*/target/*" \
	-print | sort > "$TMP_FILE"

while IFS= read -r POM; do
	MOD_DIR="$(dirname "$POM")"
	echo
	echo "==> module: $MOD_DIR"
	cd "$MOD_DIR"

	"$MVN" -U -q $SKIP_TESTS dependency:go-offline || true
	if [ "$WITH_PRETTIER" = "1" ]; then
		"$MVN" -q $SKIP_TESTS $PRFL spotless:apply
	else
		"$MVN" -q $SKIP_TESTS spotless:apply
	fi
	"$MVN" -q $SKIP_TESTS clean verify

	cd "$WORKSPACE"
done < "$TMP_FILE"

rm -f "$TMP_FILE"

echo

echo "==> All modules formatted & verified."
```

### 実行例

* 整形のみ（テスト無効）

  ```bash
  ./fmt.sh /Applications/Eclipse_2023-12.app/Contents/workspace
  ```
* Prettier も含める

  ```bash
  WITH_PRETTIER=1 ./fmt.sh /Applications/Eclipse_2023-12.app/Contents/workspace
  ```
* テストも回す

  ```bash
  RUN_TESTS=1 ./fmt.sh /Applications/Eclipse_2023-12.app/Contents/workspace
  ```

---

## 2) Eclipse（日本語UI）から実行

**実行 → 外部ツール → 外部ツールの構成…** で新規作成。

* **メイン**

  * **場所**: `/bin/zsh`
  * **引数**: `-lic "WITH_PRETTIER=0 RUN_TESTS=0 ./fmt.sh /Applications/Eclipse_2023-12.app/Contents/workspace"`

    * Prettier を有効: `WITH_PRETTIER=1`
    * テストを有効: `RUN_TESTS=1`
  * **作業ディレクトリー**: `/Applications/Eclipse_2023-12.app/Contents/workspace`
* **環境**（必要時）

  * 変数: `PATH` → ターミナルの `echo $PATH` を貼り付け
  * オプション: **ネイティブ環境に追加**（Append）

> `-lic` は zsh に **ログイン & 対話** と `fmt.sh` 実行を一発で渡すため。PATH や SDKMAN! が読み込まれます。

---

## 3) 依存のウォームアップ（オフラインビルド）

初回やキャッシュ削除後は、各モジュールで以下（スクリプト内でも自動実行）：

```bash
mvn -U -q -DskipTests dependency:go-offline
```

---

## 4) キャッシュクリア（JDT/Lombok 壊れた時）

Spotless が JDT/Lombok で転ぶ場合の復旧手順：

```bash
rm -rf ~/.m2/repository/com/diffplug/spotless/spotless-eclipse-jdt
rm -rf ~/.m2/repository/org/projectlombok/lombok
# 直後に 3) の go-offline 実行
```

---

## 5) Thymeleaf/HTML を Prettier から除外

`th:*` 属性のある HTML が Prettier で 500 になる場合、以下をプロジェクトルートに追加：

```bash
printf "src/main/resources/templates/**\n" >> .prettierignore
```

> 併せて、**Prettier を使う時だけ** `-Pwith-prettier` プロファイルを有効化（POM で既に分離済み）。

---

## 6) よくあるエラーと対処

* **`lombok/patcher/Symbols` が無い**

  * → §4 のキャッシュクリア → §3 の go-offline。
* **JDT 4.33 が古い**

  * → §4 のキャッシュクリア（Spotless が 4.34 を取り直す）
* **`mvn: command not found`**

  * → `fmt.sh` が自動検出。ダメなら `MVN="/opt/homebrew/bin/mvn"` を直書き。
* **Prettier が HTML で 500**

  * → §5 の `.prettierignore` で除外。
* **テストが @SpringBootConfiguration 不足で落ちる**

  * → まずは `RUN_TESTS=0` で通し、後からテスト側で `@SpringBootTest(classes=...)` などを整備。

---

## 7) 運用メモ（任意）

* **pre-commit フック** で `./fmt.sh` を走らせると手癖化：

  ```bash
  printf "#!/usr/bin/env sh\nWITH_PRETTIER=0 RUN_TESTS=0 ./fmt.sh \"$(git rev-parse --show-toplevel)\"\n" > .git/hooks/pre-commit
  chmod +x .git/hooks/pre-commit
  ```
* **.zshrc の brew エラーがうるさい場合** はガード：

  ```zsh
  # Homebrew が無い環境でも静かに
  if command -v /opt/homebrew/bin/brew >/dev/null 2>&1; then
  	export PATH="/opt/homebrew/bin:$PATH"
  	# 必要な初期化があればここで eval
  fi
  ```

---

以上。困ったら §6 を見ればだいたい戻れます。
