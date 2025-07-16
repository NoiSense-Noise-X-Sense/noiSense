import { Card, CardContent } from "@/components/ui/card"
import { AlertTriangle, Moon } from "lucide-react"

export default function NoisePeakTimeCards() {
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
      {/* Peak Noise Time Card */}
      <Card className="bg-orange-50 shadow-lg border-0 hover:shadow-xl transition-all duration-300">
        <CardContent className="p-5 flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-orange-100 rounded-lg">
              <AlertTriangle className="text-orange-500 w-6 h-6" />
            </div>
            <div>
              <p className="text-sm text-orange-600 font-medium">소음 집중 시간</p>
              <p className="text-xl font-bold text-orange-900">오후 6시 (75 dB)</p>
              <p className="text-xs text-gray-500 mt-1">분석 기간: 2025.06.06 ~ 2025.07.06</p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Quietest Time Card */}
      <Card className="bg-purple-50 shadow-lg border-0 hover:shadow-xl transition-all duration-300">
        <CardContent className="p-5 flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-purple-100 rounded-lg">
              <Moon className="text-purple-500 w-6 h-6" />
            </div>
            <div>
              <p className="text-sm text-purple-600 font-medium">소음 안정 시간</p>
              <p className="text-xl font-bold text-purple-900">새벽 4시 (56 dB)</p>
              <p className="text-xs text-gray-500 mt-1">분석 기간: 2025.06.06 ~ 2025.07.06</p>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
