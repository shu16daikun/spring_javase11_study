-- ===========================
-- Schema
-- ===========================
CREATE SCHEMA IF NOT EXISTS public;
@@

-- ===========================
-- Sequences（先に作成）
-- ===========================
CREATE SEQUENCE IF NOT EXISTS au_id_seq START 1;
CREATE SEQUENCE IF NOT EXISTS us_id_seq START 1;
CREATE SEQUENCE IF NOT EXISTS sc_id_seq START 1;
CREATE SEQUENCE IF NOT EXISTS sa_id_seq START 1;
CREATE SEQUENCE IF NOT EXISTS ch_id_seq START 1;
CREATE SEQUENCE IF NOT EXISTS kq_id_seq START 1;
CREATE SEQUENCE IF NOT EXISTS as_id_seq START 1;
CREATE SEQUENCE IF NOT EXISTS ar_id_seq START 1;
CREATE SEQUENCE IF NOT EXISTS we_id_seq START 1;
@@

-- ===========================
-- Functions（既定値の一元化）
-- ===========================
-- users.password_encode の既定 bcrypt をDB側に集約
CREATE OR REPLACE FUNCTION public.app_default_password_encode()
RETURNS text
LANGUAGE sql
IMMUTABLE
AS $$
	SELECT '$2a$08$hlbqvXkXId77o29MEnk9peZvB.JENII1N6I79Ly0K9h0Z8pvYN9Ny'::text
$$;
@@

-- ===========================
-- 1) 権限
-- ===========================
CREATE TABLE IF NOT EXISTS public.authority (
	id		varchar(6)	PRIMARY KEY DEFAULT ('AU' || lpad(nextval('au_id_seq')::text, 4, '0')),
	name	varchar(20)	NOT NULL UNIQUE
);
@@

-- ===========================
-- 2) ユーザー（DB既定/トリガで“任せる”）
-- ===========================
CREATE TABLE IF NOT EXISTS public.users (
	id				varchar(6)		PRIMARY KEY DEFAULT ('US' || lpad(nextval('us_id_seq')::text, 4, '0')),
	username		varchar(30)		NOT NULL UNIQUE,
	password_encode	varchar(255)	NOT NULL DEFAULT public.app_default_password_encode(),
	authority_id	varchar(6)		NOT NULL REFERENCES public.authority(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
	is_first_login	boolean			NOT NULL DEFAULT true,
	reset_password	boolean			NOT NULL DEFAULT false -- ← JPQLでtrueにするとDBが既定に戻す
);
@@

-- リセット適用トリガ：reset_password=true の更新時に DB が既定化
CREATE OR REPLACE FUNCTION public.users_apply_reset_defaults()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
	IF NEW.reset_password IS TRUE THEN
		NEW.password_encode := public.app_default_password_encode();
		NEW.is_first_login  := TRUE;
		NEW.reset_password  := FALSE; -- ワンショットで自動解除
	END IF;
	RETURN NEW;
END;
$$;
@@

DO $$
BEGIN
	IF NOT EXISTS (
		SELECT 1
		FROM pg_trigger t
		JOIN pg_class c ON c.oid = t.tgrelid
		JOIN pg_namespace n ON n.oid = c.relnamespace
		WHERE t.tgname = 'trg_users_apply_reset_defaults'
		  AND n.nspname = 'public'
		  AND c.relname = 'users'
	) THEN
		CREATE TRIGGER trg_users_apply_reset_defaults
		BEFORE UPDATE ON public.users
		FOR EACH ROW
		EXECUTE FUNCTION public.users_apply_reset_defaults();
	END IF;
END $$;
@@

-- ===========================
-- 3) 参考書カラー
-- ===========================
CREATE TABLE IF NOT EXISTS public.sankou_book_color (
	id		varchar(5)	PRIMARY KEY DEFAULT ('SC' || lpad(nextval('sc_id_seq')::text, 3, '0')),
	name	varchar(20)	NOT NULL UNIQUE
);
@@

-- ===========================
-- 4) 参考書
-- ===========================
CREATE TABLE IF NOT EXISTS public.sankou_books (
	id			varchar(7)		PRIMARY KEY DEFAULT ('SA' || lpad(nextval('sa_id_seq')::text, 5, '0')),
	name		varchar(100)	NOT NULL UNIQUE,
	color_id	varchar(5)		REFERENCES public.sankou_book_color(id)
);
@@

-- ===========================
-- 5) 章
-- ===========================
CREATE TABLE IF NOT EXISTS public.chapter (
	id				varchar(10)	PRIMARY KEY DEFAULT ('CH' || lpad(nextval('ch_id_seq')::text, 8, '0')),
	name			varchar(50)	NOT NULL,
	sankou_book_id	varchar(7)	NOT NULL REFERENCES public.sankou_books(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
	no				varchar(2)	NOT NULL,
	CONSTRAINT uq_chapter_book_no UNIQUE (sankou_book_id, no)
);
@@
CREATE INDEX IF NOT EXISTS idx_chapter_book_no ON public.chapter (sankou_book_id, no);
@@

-- ===========================
-- 6) 黒本：問題
-- ===========================
CREATE TABLE IF NOT EXISTS public.kurohon_questions (
	id					varchar(10)	PRIMARY KEY DEFAULT ('KQ' || lpad(nextval('kq_id_seq')::text, 8, '0')),
	chapter_id			varchar(10)	NOT NULL REFERENCES public.chapter(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
	question_no			varchar(3)	NOT NULL,
	question_html		text		NOT NULL DEFAULT '',
	correct_option		varchar(20)	NOT NULL,
	explanation_html	text		NOT NULL DEFAULT '',
	answer_count_max	smallint	NOT NULL,
	option_count		smallint	NOT NULL,
	sankou_book_id		varchar(7)	NOT NULL REFERENCES public.sankou_books(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
	CONSTRAINT uq_kq_book_chapter_qno UNIQUE (chapter_id, sankou_book_id, question_no)
);
@@
CREATE INDEX IF NOT EXISTS idx_kq_chapter_qno ON public.kurohon_questions (chapter_id, question_no);
CREATE INDEX IF NOT EXISTS idx_kq_book       ON public.kurohon_questions (sankou_book_id);
@@

-- ===========================
-- 7) 解答セッション（attempt_session）
-- ===========================
CREATE TABLE IF NOT EXISTS public.attempt_session (
	id				varchar(12)	PRIMARY KEY DEFAULT ('AS' || lpad(nextval('as_id_seq')::text, 10, '0')),
	user_id			varchar(12)	NOT NULL,  -- users.id（FKは必要に応じて別管理でもOK）
	sankou_book_id	varchar(12)	NOT NULL,  -- sankou_books.id
	started_at		timestamp	NOT NULL DEFAULT CURRENT_TIMESTAMP,
	finished_at		timestamp	NULL
);
@@
CREATE INDEX IF NOT EXISTS idx_attempt_session_user_book ON public.attempt_session (user_id, sankou_book_id);
@@

-- ===========================
-- 8) 解答履歴（answer_records）
-- ===========================
CREATE TABLE IF NOT EXISTS public.answer_records (
	id					varchar(10)	PRIMARY KEY DEFAULT ('AR' || lpad(nextval('ar_id_seq')::text, 8, '0')),
	user_id				varchar(6)	NOT NULL,
	kurohon_question_id	varchar(10)	NOT NULL REFERENCES public.kurohon_questions(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
	sankou_book_id		varchar(7)	NOT NULL DEFAULT '',
	chapter_id			varchar(10)	NOT NULL DEFAULT '',
	attempt_no			int			NOT NULL,
	selected_option		varchar(20)	NOT NULL,
	is_correct			boolean		NOT NULL,
	answered_at			timestamptz	NOT NULL DEFAULT now(),
	attempt_session_id	varchar(12)	NULL REFERENCES public.attempt_session(id),
	UNIQUE (user_id, sankou_book_id, chapter_id, attempt_no, kurohon_question_id)
);
@@
CREATE INDEX IF NOT EXISTS idx_ar_user_attempt         ON public.answer_records (user_id, sankou_book_id, chapter_id, kurohon_question_id, attempt_no);
CREATE INDEX IF NOT EXISTS idx_ar_question             ON public.answer_records (kurohon_question_id);
CREATE INDEX IF NOT EXISTS idx_answer_records_session  ON public.answer_records (attempt_session_id);
@@

-- ===========================
-- attempt_no 採番（関数 + トリガ）※重複なし
-- ===========================
CREATE OR REPLACE FUNCTION public.answer_records_set_attempt_no()
RETURNS trigger
LANGUAGE plpgsql
AS $answer_func$
DECLARE
	v_next int;
BEGIN
	-- すでに明示指定されていれば何もしない
	IF NEW.attempt_no IS NOT NULL THEN
		RETURN NEW;
	END IF;

	IF NEW.kurohon_question_id IS NULL THEN
		RAISE EXCEPTION 'kurohon_question_id is required to auto-number attempt_no';
	END IF;

	-- 採番競合回避：ユーザー×本×章×問題 でアドバイザリロック
	PERFORM pg_advisory_xact_lock(
		hashtextextended(
			COALESCE(NEW.user_id::text,'') || '|' ||
			COALESCE(NEW.sankou_book_id::text,'') || '|' ||
			COALESCE(NEW.chapter_id::text,'') || '|' ||
			COALESCE(NEW.kurohon_question_id::text,''), 0)
	);

	-- 同一ユーザー×本×章×問題の中で連番採番
	SELECT COALESCE(MAX(ar.attempt_no), 0) + 1
		INTO v_next
		FROM public.answer_records ar
		WHERE ar.user_id             = NEW.user_id
		  AND ar.sankou_book_id      = NEW.sankou_book_id
		  AND ar.chapter_id          = NEW.chapter_id
		  AND ar.kurohon_question_id = NEW.kurohon_question_id;

	NEW.attempt_no := v_next;
	RETURN NEW;
END;
$answer_func$;
@@

DO $$
BEGIN
	IF NOT EXISTS (
		SELECT 1
		FROM pg_trigger t
		JOIN pg_class c ON c.oid = t.tgrelid
		JOIN pg_namespace n ON n.oid = c.relnamespace
		WHERE t.tgname = 'trg_answer_records_set_attempt_no'
		  AND n.nspname = 'public'
		  AND c.relname = 'answer_records'
	) THEN
		CREATE TRIGGER trg_answer_records_set_attempt_no
		BEFORE INSERT ON public.answer_records
		FOR EACH ROW
		EXECUTE FUNCTION public.answer_records_set_attempt_no();
	END IF;
END $$;
@@

-- ===========================
-- 9) 苦手分析（weakness）と UPSERT トリガ
-- ===========================
CREATE TABLE IF NOT EXISTS public.weakness (
	id					varchar(16)	PRIMARY KEY DEFAULT ('WE' || lpad(nextval('we_id_seq')::text, 8, '0')),
	user_id				varchar(6)	NOT NULL,
	sankou_book_id		varchar(7)	NOT NULL DEFAULT '',
	chapter_id			varchar(10)	NOT NULL DEFAULT '',
	kurohon_question_id	varchar(10)	NOT NULL REFERENCES public.kurohon_questions(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
	total_attempts		int			NOT NULL DEFAULT 0,
	correct_count		int			NOT NULL DEFAULT 0,
	wrong_count			int			NOT NULL DEFAULT 0,
	correct_rate		numeric(5,2) GENERATED ALWAYS AS (
		CASE WHEN total_attempts > 0
			 THEN ROUND((correct_count::numeric*100.0)/total_attempts, 2)
			 ELSE 0 END
	) STORED,
	last_answered_at		timestamptz,
	last_selected_option	varchar(20),
	last_is_correct			boolean,
	CONSTRAINT uq_we_user_scope_q UNIQUE (user_id, sankou_book_id, chapter_id, kurohon_question_id)
);
@@
CREATE INDEX IF NOT EXISTS idx_we_user_order ON public.weakness (user_id, wrong_count DESC, correct_rate);
CREATE INDEX IF NOT EXISTS idx_we_scope      ON public.weakness (sankou_book_id, chapter_id);
@@

CREATE OR REPLACE FUNCTION public.weakeness_upsert_on_answer()
RETURNS trigger
LANGUAGE plpgsql
AS $weak_upsert$
BEGIN
	INSERT INTO public.weakness (
		id, user_id, sankou_book_id, chapter_id, kurohon_question_id,
		total_attempts, correct_count, wrong_count,
		last_answered_at, last_selected_option, last_is_correct
	)
	VALUES (
		DEFAULT,
		NEW.user_id,
		NEW.sankou_book_id,
		NEW.chapter_id,
		NEW.kurohon_question_id,
		1,
		CASE WHEN NEW.is_correct THEN 1 ELSE 0 END,
		CASE WHEN NEW.is_correct THEN 0 ELSE 1 END,
		NEW.answered_at,
		NEW.selected_option,
		NEW.is_correct
	)
	ON CONFLICT (user_id, sankou_book_id, chapter_id, kurohon_question_id)
	DO UPDATE SET
		total_attempts       = weakness.total_attempts + 1,
		correct_count        = weakness.correct_count
			+ CASE WHEN EXCLUDED.last_is_correct THEN 1 ELSE 0 END,
		wrong_count          = weakness.wrong_count
			+ CASE WHEN EXCLUDED.last_is_correct THEN 0 ELSE 1 END,
		last_answered_at     = GREATEST(weakness.last_answered_at, EXCLUDED.last_answered_at),
		last_selected_option = EXCLUDED.last_selected_option,
		last_is_correct      = EXCLUDED.last_is_correct;

	RETURN NEW;
END;
$weak_upsert$;
@@

DO $$
BEGIN
	IF NOT EXISTS (
		SELECT 1
		FROM pg_trigger t
		JOIN pg_class c ON c.oid = t.tgrelid
		JOIN pg_namespace n ON n.oid = c.relnamespace
		WHERE t.tgname = 'trg_weakeness_upsert_on_answer'
		  AND n.nspname = 'public'
		  AND c.relname = 'answer_records'
	) THEN
		CREATE TRIGGER trg_weakeness_upsert_on_answer
		AFTER INSERT ON public.answer_records
		FOR EACH ROW
		EXECUTE FUNCTION public.weakeness_upsert_on_answer();
	END IF;
END $$;
@@
