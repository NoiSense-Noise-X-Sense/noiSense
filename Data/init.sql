CREATE SCHEMA IF NOT EXISTS "noisense";

-- 이후 테이블은 해당 스키마에 생성되게 설정
SET search_path TO noisense;

-- ENUM 생성
CREATE TYPE boundary_polygon_type AS ENUM ('행정구역', '행정동');
CREATE TYPE geometry_format_type AS ENUM ('GeoJSON', 'WKT');

-- 여기서부터 테이블 생성
 CREATE TABLE IF NOT EXISTS"comment"
  (
    "comment_id"    NUMERIC(20)  NOT NULL,
    "user_id"       NUMERIC(20)  NOT NULL,
    "board_id"      NUMERIC(20)  NOT NULL, -- 외래키 설정 누락
    "nickname"      VARCHAR(100) NOT NULL,
    "content"       VARCHAR(100) NOT NULL,
    "created_date"  TIMESTAMP    NOT NULL,
    "modified_date" TIMESTAMP    NULL
  );

 CREATE TABLE IF NOT EXISTS"autonomous_district" (
                                     "code"	VARCHAR(50)		NOT NULL,
                                     "name_ko"	VARCHAR(50)		NOT NULL,
                                     "name_en"	VARCHAR(50)		NOT NULL
);

 CREATE TABLE IF NOT EXISTS"auth" (
                      "auth_id"	NUMERIC(20)		NOT NULL,
                      "user_id"	NUMERIC(20)		NOT NULL,
                      "token_type"	VARCHAR(255)		NOT NULL,
                      "access_token"	VARCHAR(255)		NOT NULL,
                      "refresh_token"	VARCHAR(255)		NOT NULL,
                      "created_date"	TIMESTAMP		NOT NULL,
                      "modified_date"	TIMESTAMP		NULL
);

 CREATE TABLE IF NOT EXISTS"users" (
                       "user_id"	NUMERIC(20)		NOT NULL,
                       "user_nm"	VARCHAR(100)		NULL,
                       "nickname"	VARCHAR(100)		NULL,
                       "email"	VARCHAR(100)		NULL,
                       "autonomous_district"	VARCHAR(50)		NULL,
                       "administrative_district"	VARCHAR(50)		NULL,
                       "created_date"	TIMESTAMP		NOT NULL,
                       "modified_date"	TIMESTAMP		NULL
);

 CREATE TABLE IF NOT EXISTS"administrative_district" (
                                         "code"	VARCHAR(50)		NOT NULL,
                                         "name_ko"	VARCHAR(50)		NOT NULL,
                                         "name_en"	VARCHAR(50)		NOT NULL,
                                         "autonomous_district"	VARCHAR(50)		NOT NULL
);

 CREATE TABLE IF NOT EXISTS"board" (
                       "board_id"	NUMERIC(20)		NOT NULL,
                       "user_id"	NUMERIC(20)		NOT NULL,
                       "nickname"	VARCHAR(100)		NULL,
                       "title"	VARCHAR(100)		NULL,
                       "content"	VARCHAR(255)		NULL,
                       "emotional_score"	NUMERIC(20)		NULL,
                       "empathy_count"	NUMERIC(20)		NULL,
                       "view_count"	NUMERIC(20)		NULL,
                       "autonomous_district"	VARCHAR(50)		NULL,
                       "administrative_district"	VARCHAR(50)		NULL,
                       "created_date"	TIMESTAMP		NOT NULL,
                       "modified_date"	TIMESTAMP		NULL
);

 CREATE TABLE IF NOT EXISTS"noise_complaints" (
                                  "noise_complaints_id"	NUMERIC(20)		NOT NULL,
                                  "autonomous_district"	VARCHAR(100)		NULL,
                                  "year"	INTEGER		NULL,
                                  "count"	INTEGER		NULL
);


 CREATE TABLE IF NOT EXISTS boundary_polygon (
                                boundary_polygon_id NUMERIC(20) NOT NULL,
                                autonomous_district VARCHAR(50),
                                administrative_district VARCHAR(50),
                                type boundary_polygon_type NOT NULL,
                                geometry_format geometry_format_type DEFAULT 'GeoJSON' NOT NULL,
                                geometry TEXT NOT NULL,
                                created_date TIMESTAMP		NOT NULL
);


 CREATE TABLE IF NOT EXISTS"sensor_data" (
                             "sensor_data_id"	NUMERIC(20)		NOT NULL,
                             "sensing_time"	TIMESTAMP		NOT NULL,
                             "region"	VARCHAR(100)		NULL,
                             "autonomous_district"	VARCHAR(50)		NOT NULL,
                             "administrative_district"	VARCHAR(50)		NOT NULL,
                             "max_noise"	NUMERIC(5, 2)		NULL,
                             "avg_noise"	NUMERIC(5, 2)		NULL,
                             "min_noise"	NUMERIC(5, 2)		NULL,
                             "max_temp"	NUMERIC(5, 2)		NULL,
                             "avg_temp"	NUMERIC(5, 2)		NULL,
                             "min_temp"	NUMERIC(5, 2)		NULL,
                             "max_humi"	NUMERIC(5, 2)		NULL,
                             "avg_humi"	NUMERIC(5, 2)		NULL,
                             "min_humi"	NUMERIC(5, 2)		NULL,
                             "batch_time"	TIMESTAMP		NOT NULL
);


--  컬럼 주석

COMMENT ON COLUMN "sensor_data"."region" IS '입력되는  데이터값들
residential_area
roads_and_parks
industrial_area
traditional_markets
main_street
commercial_area
public_facilities
parks';

COMMENT ON COLUMN "sensor_data"."max_noise" IS '소음 최대(dB)';

COMMENT ON COLUMN "sensor_data"."avg_noise" IS '소음 평균(dB)';

COMMENT ON COLUMN "sensor_data"."min_noise" IS '소음 최소(dB)';


-- 제약 조건
ALTER TABLE "autonomous_district" ADD CONSTRAINT "PK_AUTONOMOUS_DISTRICT" PRIMARY KEY (
                                                                                       "code"
  );

ALTER TABLE "auth" ADD CONSTRAINT "PK_AUTH" PRIMARY KEY (
                                                         "auth_id",
                                                         "user_id"
  );

ALTER TABLE "users" ADD CONSTRAINT "PK_USERS" PRIMARY KEY (
                                                           "user_id"
  );

ALTER TABLE "administrative_district" ADD CONSTRAINT "PK_ADMINISTRATIVE_DISTRICT" PRIMARY KEY (
                                                                                               "code"
  );

ALTER TABLE "board" ADD CONSTRAINT "PK_BOARD" PRIMARY KEY (
                                                           "board_id"
  );

ALTER TABLE "noise_complaints" ADD CONSTRAINT "PK_NOISE_COMPLAINTS" PRIMARY KEY (
                                                                                 "noise_complaints_id"
  );

ALTER TABLE "boundary_polygon" ADD CONSTRAINT "PK_BOUNDARY_POLYGON" PRIMARY KEY (
                                                                                 "boundary_polygon_id"
  );

ALTER TABLE "sensor_data" ADD CONSTRAINT "PK_SENSOR_DATA" PRIMARY KEY (
                                                                       "sensor_data_id"
  );

ALTER TABLE "auth" ADD CONSTRAINT "FK_users_TO_auth_1" FOREIGN KEY (
                                                                    "user_id"
  )
  REFERENCES "users" (
                      "user_id"
    );


ALTER TABLE "comment" ADD CONSTRAINT "PK_COMMENT" PRIMARY KEY (
                                                               "comment_id"
  );


ALTER TABLE "comment" -- 댓글
  ADD CONSTRAINT FK_comment_user FOREIGN KEY ("user_id") REFERENCES "users" ("user_id"),
  ADD CONSTRAINT FK_comment_board FOREIGN KEY ("board_id") REFERENCES "board" ("board_id");


ALTER TABLE "administrative_district" -- 행정동 - 자치구 연관 관계
  ADD CONSTRAINT FK_admin_district_autonomous FOREIGN KEY ("autonomous_district") REFERENCES "autonomous_district" ("code");

ALTER TABLE "users" -- 사용자에게 지정되는 행정동, 자치구 연관 관계, 이메일 유니크 키
  ADD CONSTRAINT FK_user_autonomous FOREIGN KEY ("autonomous_district") REFERENCES "autonomous_district" ("code"),
  ADD CONSTRAINT FK_user_administrative FOREIGN KEY ("administrative_district") REFERENCES "administrative_district" ("code"),
  ADD CONSTRAINT UQ_user_email UNIQUE ("email");

--- Spring Batch Start
-- 🎯 1. Job 인스턴스 (Job 정의 단위)
CREATE TABLE BATCH_JOB_INSTANCE  (
                                   JOB_INSTANCE_ID BIGINT PRIMARY KEY,
                                   VERSION BIGINT,
                                   JOB_NAME VARCHAR(100) NOT NULL,
                                   JOB_KEY VARCHAR(32) NOT NULL,
                                   CONSTRAINT JOB_INST_UN UNIQUE (JOB_NAME, JOB_KEY)
);

-- 🎯 2. Job 실행 이력
CREATE TABLE BATCH_JOB_EXECUTION  (
                                    JOB_EXECUTION_ID BIGINT PRIMARY KEY,
                                    VERSION BIGINT,
                                    JOB_INSTANCE_ID BIGINT NOT NULL,
                                    CREATE_TIME TIMESTAMP NOT NULL,
                                    START_TIME TIMESTAMP,
                                    END_TIME TIMESTAMP,
                                    STATUS VARCHAR(10),
                                    EXIT_CODE VARCHAR(2500),
                                    EXIT_MESSAGE VARCHAR(2500),
                                    LAST_UPDATED TIMESTAMP,
                                    JOB_CONFIGURATION_LOCATION VARCHAR(2500),
                                    CONSTRAINT JOB_INST_EXEC_FK FOREIGN KEY (JOB_INSTANCE_ID)
                                      REFERENCES BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
);

-- 🎯 3. Job 실행 시 전달된 파라미터 (Spring Batch 5.x 기준 컬럼명)
CREATE TABLE BATCH_JOB_EXECUTION_PARAMS  (
                                           JOB_EXECUTION_ID BIGINT NOT NULL,
                                           PARAMETER_NAME VARCHAR(100) NOT NULL,
                                           PARAMETER_TYPE VARCHAR(50) NOT NULL,
                                           PARAMETER_VALUE VARCHAR(250),
                                           IDENTIFYING CHAR(1) NOT NULL,
                                           CONSTRAINT JOB_EXEC_PARAMS_FK FOREIGN KEY (JOB_EXECUTION_ID)
                                             REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
);

-- 🎯 4. Step 실행 이력
CREATE TABLE BATCH_STEP_EXECUTION  (
                                     STEP_EXECUTION_ID BIGINT PRIMARY KEY,
                                     VERSION BIGINT NOT NULL,
                                     STEP_NAME VARCHAR(100) NOT NULL,
                                     JOB_EXECUTION_ID BIGINT NOT NULL,
                                     CREATE_TIME TIMESTAMP NOT NULL,
                                     START_TIME TIMESTAMP,
                                     END_TIME TIMESTAMP,
                                     STATUS VARCHAR(10),
                                     COMMIT_COUNT BIGINT,
                                     READ_COUNT BIGINT,
                                     FILTER_COUNT BIGINT,
                                     WRITE_COUNT BIGINT,
                                     READ_SKIP_COUNT BIGINT,
                                     WRITE_SKIP_COUNT BIGINT,
                                     PROCESS_SKIP_COUNT BIGINT,
                                     ROLLBACK_COUNT BIGINT,
                                     EXIT_CODE VARCHAR(2500),
                                     EXIT_MESSAGE VARCHAR(2500),
                                     LAST_UPDATED TIMESTAMP,
                                     CONSTRAINT JOB_EXEC_STEP_FK FOREIGN KEY (JOB_EXECUTION_ID)
                                       REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
);

-- 🎯 5. Step 실행 컨텍스트 (JSON 등 직렬화 저장)
CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT  (
                                             STEP_EXECUTION_ID BIGINT PRIMARY KEY,
                                             SHORT_CONTEXT VARCHAR(2500) NOT NULL,
                                             SERIALIZED_CONTEXT TEXT,
                                             CONSTRAINT STEP_EXEC_CTX_FK FOREIGN KEY (STEP_EXECUTION_ID)
                                               REFERENCES BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
);

-- 🎯 6. Job 실행 컨텍스트
CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT  (
                                            JOB_EXECUTION_ID BIGINT PRIMARY KEY,
                                            SHORT_CONTEXT VARCHAR(2500) NOT NULL,
                                            SERIALIZED_CONTEXT TEXT,
                                            CONSTRAINT JOB_EXEC_CTX_FK FOREIGN KEY (JOB_EXECUTION_ID)
                                              REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
);

-- 🎯 7. 실행결과
CREATE TABLE batch_result_log (
                                id BIGSERIAL PRIMARY KEY,
                                job_name VARCHAR(100) NOT NULL,
                                message TEXT,
                                created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 🎯 8. 시퀀스
CREATE SEQUENCE BATCH_JOB_SEQ START 1;
CREATE SEQUENCE BATCH_JOB_EXECUTION_SEQ START 1;
CREATE SEQUENCE BATCH_STEP_EXECUTION_SEQ START 1;

--- Spring Batch End

