// 차트의 각 데이터 포인트를 위한 타입
// 실제 데이터 구조에 맞게 수정해야 합니다. (예: x축이 날짜, y축이 소음)
export interface ChartDataPoint {
  hour?: string;      // 백엔드에서 오는 '시간' 키 (선택적)
  day?: string;       // 백엔드에서 오는 '일' 키 (선택적)
  avgNoise: number | null; // null일 수 있는 소음 값

  // top1, top2... bottom3 등 다른 키들도 받을 수 있도록 설정
  [key: string]: any;
}


// 랭킹 항목의 타입
export interface RankDto {
  region: string;
  avgNoise: number;
}

// 편차 랭킹 항목의 타입
export interface DeviationDto {
  region: string;
  avgNoise: number;
  maxNoise: number;
  minNoise: number;
  deviation: number;
}

// 전체 차트 데이터의 타입
export interface TotalChartDto {
  overallHourAvgNoiseData: ChartDataPoint[];
  overallDayAvgNoiseData: ChartDataPoint[];
  trendPointHourAvgNoiseData: any[]; // 이 부분도 실제 구조에 맞는 타입으로 바꿔주면 좋습니다.
  trendPointDayOfWeekAvgNoiseData: any[]; // 이 부분도 실제 구조에 맞는 타입으로 바꿔주면 좋습니다.
}

// 전체 리포트 데이터의 타입
export interface ReportDataDto {
  avgNoise: number;
  perceivedNoise: number;
  maxNoiseRegion: string;
  maxNoiseTime: string; // 혹은 Date 타입일 수 있습니다.
  maxNoiseTimeValue: number;
  topRankDtoList: RankDto[];
  bottomRankDtoList: RankDto[];
  deviationRankDtoList: DeviationDto[];
  totalChartDto: TotalChartDto;
}

// 자치구 데이터의 타입
export interface DistrictDto {
  code: string;      // 구 코드
  nameKo: string;    // 한국어 구 이름
  nameEn: string;    // 영어 구 이름
}
