CREATE MATERIALIZED VIEW daily_district_summary_view AS
SELECT
  sensing_time::date AS summary_date,
  ad.code AS administrative_code,
  au.code AS autonomous_code,
  ad.name_ko AS administrative_nameKo,
  au.name_ko AS autonomous_nameKo,
  avg(s.avg_noise) AS avg_noise,
  avg(s.max_noise) AS max_noise,
  avg(s.min_noise) AS min_noise,
  count(s.avg_noise) AS data_count
FROM sensor_data s
JOIN
  sensor_district_mapping sdm ON
    sdm.sensor_auto_district_en = s.autonomous_district
    AND sdm.sensor_admin_district_en = s.administrative_district
JOIN
  administrative_district ad ON ad.code = sdm.admin_district_code
JOIN
  autonomous_district au ON au.code = ad.autonomous_district
WHERE s.avg_noise IS NOT NULL
GROUP BY
  summary_date,
  ad.code,
  au.code,
  ad.name_ko,
  au.name_ko;

CREATE INDEX idx_summary_view_date ON daily_district_summary_view (summary_date);
CREATE INDEX idx_summary_view_auto_code ON daily_district_summary_view (autonomous_code);


CREATE MATERIALIZED VIEW hour_district_summary_view AS
SELECT
  date_trunc('hour', s.sensing_time) AS sensing_hour,
  avg(s.avg_noise) AS avg_noise,
  ad.code AS administrative_code,
  ad.name_ko AS administrative_name_ko,
  ad.name_en AS administrative_name_en,
  au.code AS autonomous_code,
  au.name_ko AS autonomous_name_ko,
  au.name_en AS autonomous_name_en,
  s.region,
  count(s.avg_noise) AS data_count
FROM
  sensor_data s
JOIN
  sensor_district_mapping sdm ON
    sdm.sensor_auto_district_en = s.autonomous_district
    AND sdm.sensor_admin_district_en = s.administrative_district
JOIN
  administrative_district ad ON ad.code = sdm.admin_district_code
JOIN
  autonomous_district au ON au.code = ad.autonomous_district
WHERE s.avg_noise IS NOT NULL
GROUP BY
  sensing_hour,
  ad.code,
  ad.name_ko,
  ad.name_en,
  au.code,
  au.name_ko,
  au.name_en,
  s.region;

CREATE INDEX idx_hour_view_time ON hour_district_summary_view (sensing_hour);
CREATE INDEX idx_hour_view_auto_code ON hour_district_summary_view (autonomous_code);
