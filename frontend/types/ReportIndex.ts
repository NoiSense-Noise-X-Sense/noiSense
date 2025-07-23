
export interface RankItem {
    region: string;
    avgNoise: number;
}

export interface DeviationRankItem {
    region: string;
    avgNoise: number;
    maxNoise: number;
    minNoise: number;
    deviation: number;
}


export interface OverallChartItem {
    avgNoise: number;
    xaxis: number;
}

export interface TrendChartItem {
    avgNoiseByRegion: Record<string, number>;
    xaxis: number;
}

export interface ChartItem{
    overallHourAvgNoiseData: OverallChartItem[];
    overallDayAvgNoiseData: OverallChartItem[];
    trendPointHourAvgNoiseData: TrendChartItem[];
    trendPointDayOfWeekAvgNoiseData: TrendChartItem[];
}


export interface ReportData{
    avgNoise: number;
    maxNoiseRegion: string;
    maxNoiseTime: string;
    perceivedNoise: number;

    topRankDtoList: RankItem[];
    bottomRankDtoList: RankItem[];
    deviationRankDtoList: DeviationRankItem[];

    totalChartDto: ChartItem;
}
