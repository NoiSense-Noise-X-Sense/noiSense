"use client"

import SeoulNoiseDashboard from "./seoul-noise-dashboard"

export default function MainDashboard() {
  return <SeoulNoiseDashboard onDistrictClick={function(district: string): void {
      throw new Error("Function not implemented.")
  } } />
}
