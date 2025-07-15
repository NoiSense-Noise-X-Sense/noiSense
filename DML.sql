INSERT INTO autonomous_district (code, name_en, name_ko) VALUES
                                                           (1, 'Gangnam-gu', '강남구'),
                                                           (2, 'Jung-gu', '중구'),
                                                           (3, 'Gangseo-gu', '강서구'),
                                                           (4, 'Seodaemun-gu', '서대문구'),
                                                           (5, 'Jongno-gu', '종로구'),
                                                           (6, 'Gangdong-gu', '강동구'),
                                                           (7, 'Gwangjin-gu', '광진구'),
                                                           (8, 'Gwanak-gu', '관악구'),
                                                           (9, 'Seongdong-gu', '성동구'),
                                                           (10, 'Dobong-gu', '도봉구'),
                                                           (11, 'Yongsan-gu', '용산구'),
                                                           (12, 'Songpa-gu', '송파구'),
                                                           (13, 'Eunpyeong-gu', '은평구'),
                                                           (14, 'Yangcheon-gu', '양천구'),
                                                           (15, 'Seocho-gu', '서초구'),
                                                           (16, 'Gangbuk-gu', '강북구'),
                                                           (17, 'Geumcheon-gu', '금천구'),
                                                           (18, 'Dongjak-gu', '동작구'),
                                                           (19, 'Mapo-gu', '마포구'),
                                                           (20, 'Nowon-gu', '노원구'),
                                                           (21, 'Guro-gu', '구로구'),
                                                           (22, 'Yeongdeungpo-gu', '영등포구'),
                                                           (23, 'Jungnang-gu', '중랑구'),
                                                           (24, 'Seongbuk-gu', '성북구'),
                                                           (25, 'Dongdaemun-gu', '동대문구');

commit;

select * from autonomous_district;

select * from sensor_data where sensing_time between '2025-07-14 00:00:00' and '2025-07-14 23:59:59' ;

select * from sensor_data;

select count(*) from sensor_data;

TRUNCATE TABLE sensor_data RESTART IDENTITY;

delete from sensor_data;


commit;
