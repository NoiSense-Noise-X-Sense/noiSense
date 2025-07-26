import { fetchWithAuth } from "@/lib/fetchWithAuth";

// District data interface matching backend DistrictDto
export interface DistrictDto {
  districtNameEn: string;
  districtNameKo: string;
  districtCode: string;
  autonomousDistrictCode: string;
  hasAutonomousDistrictCode: boolean;
}

// API response wrapper interface
interface ApiResponse<T> {
  code: string;
  data: T;
  message?: string;
  error?: string;
}

/**
 * Get all autonomous districts (구)
 * @returns Promise with list of autonomous districts
 */
export async function getAutonomousDistricts(): Promise<DistrictDto[]> {
  const response = await fetchWithAuth('/api/v1/district/autonomousDistrict');

  if (!response.ok) {
    throw new Error('Failed to fetch autonomous districts');
  }

  const result: ApiResponse<DistrictDto[]> = await response.json();
  if (result.code !== 'Success') {
    throw new Error(result.message || 'Failed to fetch autonomous districts');
  }

  return result.data;
}

/**
 * Get dongs (동) for a specific gu code
 * @param guCode The autonomous district code
 * @returns Promise with list of dongs in the specified gu
 */
export async function getDongsByGu(guCode: string): Promise<DistrictDto[]> {
  const response = await fetchWithAuth(`/api/v1/district/gu/${guCode}/dongs`);

  if (!response.ok) {
    throw new Error(`Failed to fetch dongs for gu: ${guCode}`);
  }

  const result: ApiResponse<DistrictDto[]> = await response.json();

  if (result.code !== 'Success') {
    throw new Error(result.message || 'Failed to fetch autonomous districts');
  }

  return result.data;
}
