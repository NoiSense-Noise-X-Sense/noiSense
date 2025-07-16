import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card"

export default function SeoulNoiseMap() {
  return (
    <div className="p-8 space-y-6">
      {/* 헤더 */}
      <div className="text-2xl font-bold">서울시 소음 지도</div>
      <div className="flex flex-col md:flex-row gap-6">
        {/* 왼쪽 평균값 요약 */}
        <Card className="w-full md:w-1/3 text-center">
          <CardHeader>
            <CardTitle className="text-muted-foreground text-sm">서울시 평균 소음 현황</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-4xl font-bold text-yellow-500">74.2 dB</div>
            <div className="text-sm text-muted-foreground mt-2">보통 / 업데이트: 2024.07.07 14:30</div>
          </CardContent>
        </Card>
        {/* 지도 영역 */}
        <Card className="w-full md:w-2/3">
          <CardHeader>
            <CardTitle>서울시 자치구별 소음 지도</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="w-full aspect-video bg-gray-100 rounded-xl flex items-center justify-center text-muted-foreground">
              지도 삽입 (e.g. SVG, Image, Leaflet, Mapbox 등)
            </div>
            {/* 범례 */}
            <div className="flex justify-center gap-4 text-sm mt-4">
              <Legend color="bg-green-500" label="70dB 미만" />
              <Legend color="bg-yellow-400" label="70~79dB" />
              <Legend color="bg-red-500" label="80dB 이상" />
            </div>
          </CardContent>
        </Card>
      </div>
      {/* 하단 소음 데이터 정보 */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <InfoBox title="데이터 출처" value="서울시 공공데이터" />
        <InfoBox title="측정 단위" value="데시벨 (dB)" />
        <InfoBox title="업데이트" value="실시간 (30분 간격)" />
        <InfoBox title="측정소" value="서울시 전 구역" />
      </div>
    </div>
  )
}

function InfoBox({ title, value }: { title: string; value: string }) {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-center text-sm text-muted-foreground">{title}</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="text-center text-base font-semibold">{value}</div>
      </CardContent>
    </Card>
  )
}

function Legend({ color, label }: { color: string; label: string }) {
  return (
    <div className="flex items-center gap-2">
      <span className={`w-4 h-4 rounded-full ${color}`} />
      <span>{label}</span>
    </div>
  )
}
