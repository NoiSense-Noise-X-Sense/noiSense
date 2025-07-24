#!/bin/bash
set -e

#  # 1단계: postgres DB에서 DB 생성 compose up 시 자동 생성하므로 생략
#echo "📌 Creating database 'noisense'..."
#psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER"  --dbname=postgres <<-EOSQL
#CREATE DATABASE noisense;
#EOSQL

echo "📌 Step 1: SKIP Creating database 'noisense'..."

# 2단계: noisense DB에 접속해서 스키마 생성
echo "📌 Step 2: Creating schema 'noisense'..."
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname=noisense <<-EOSQL
CREATE SCHEMA IF NOT EXISTS noisense;
SET search_path TO noisense;
EOSQL

# 3단계: 외부 SQL 파일(table.sql) 실행하여 테이블 생성
echo "📌Step 3: Executing table.sql to create enums and create tables..."
#psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname=noisense -f /docker-entrypoint-initdb.d/table.sql
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname=noisense <<EOSQL
SET search_path TO noisense;
\i /tmp/table.sql
EOSQL

# 4단계 insert district code
echo "📌 Step 4: Insert data (autonomous_district.sql)"
#psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname=noisense -f /docker-entrypoint-initdb.d/autonomous_district.sql
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname=noisense <<EOSQL
SET search_path TO noisense;
\i /tmp/autonomous_district.sql
EOSQL


echo "📌 Step 5: Insert data (administrative_district.sql)"
#psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname=noisense -f /docker-entrypoint-initdb.d/administrative_district.sql
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname=noisense <<EOSQL
SET search_path TO noisense;
\i /tmp/administrative_district.sql
EOSQL


echo "📌 Step 6: Insert data (boundary_polygon.sql)"
#psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname=noisense -f /docker-entrypoint-initdb.d/administrative_district.sql
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname=noisense <<EOSQL
SET search_path TO noisense;
\i /tmp/boundary_polygon.sql
EOSQL

echo "📌 Step 6: Insert data (sensor_district_mapping.sql)"
#psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname=noisense -f /docker-entrypoint-initdb.d/administrative_district.sql
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname=noisense <<EOSQL
SET search_path TO noisense;
\i /tmp/sensor_district_mapping.sql
EOSQL

echo "✅ All initialization complete!"
