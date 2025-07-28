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
 * Get all administrative districts (동)
 * @returns Promise with list of administrative districts
 */
export async function getAdministrativeDistricts(): Promise<DistrictDto[]> {
  const response = await fetchWithAuth('/api/v1/district/administrativeDistrict');

  if (!response.ok) {
    throw new Error('Failed to fetch administrative districts');
  }

  const result: ApiResponse<DistrictDto[]> = await response.json();
  if (result.code !== 'Success') {
    throw new Error(result.message || 'Failed to fetch administrative districts');
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

// Cache for district mappings
let autonomousDistrictCache: { [key: string]: string } | null = null;
let administrativeDistrictCache: { [key: string]: string } | null = null;
// Reverse cache for name to code lookups
let autonomousDistrictReverseCache: { [key: string]: string } | null = null;
let administrativeDistrictReverseCache: { [key: string]: string } | null = null;

/**
 * Initialize district mappings by fetching data from APIs
 */
export async function initializeDistrictMappings(): Promise<void> {
  try {
    // Fetch autonomous districts
    const autonomousDistricts = await getAutonomousDistricts();
    autonomousDistrictCache = {};
    autonomousDistrictReverseCache = {};
    autonomousDistricts.forEach(district => {
      autonomousDistrictCache![district.districtCode] = district.districtNameKo;
      autonomousDistrictReverseCache![district.districtNameKo] = district.districtCode;
    });

    // Fetch administrative districts
    const administrativeDistricts = await getAdministrativeDistricts();
    administrativeDistrictCache = {};
    administrativeDistrictReverseCache = {};
    administrativeDistricts.forEach(district => {
      administrativeDistrictCache![district.districtCode] = district.districtNameKo;
      administrativeDistrictReverseCache![district.districtNameKo] = district.districtCode;
    });
  } catch (error) {
    console.error('Failed to initialize district mappings:', error);
  }
}

/**
 * Get autonomous district Korean name by code
 * @param code District code
 * @returns Korean name or original code if not found
 */
export function getAutonomousDistrictName(code: string): string {
  if (!autonomousDistrictCache) {
    console.warn('Autonomous district cache not initialized. Call initializeDistrictMappings() first.');
    return code;
  }
  return autonomousDistrictCache[code] || code;
}

/**
 * Get administrative district Korean name by code
 * @param code District code
 * @returns Korean name or original code if not found
 */
export function getAdministrativeDistrictName(code: string): string {
  if (!administrativeDistrictCache) {
    console.warn('Administrative district cache not initialized. Call initializeDistrictMappings() first.');
    return code;
  }
  return administrativeDistrictCache[code] || code;
}

/**
 * Get autonomous district code by Korean name
 * @param name Korean name of the district
 * @returns District code or original name if not found
 */
export function getAutonomousDistrictCode(name: string): string {
  if (!autonomousDistrictReverseCache) {
    console.warn('Autonomous district reverse cache not initialized. Call initializeDistrictMappings() first.');
    return name;
  }
  return autonomousDistrictReverseCache[name] || name;
}

/**
 * Get administrative district code by Korean name
 * @param name Korean name of the district
 * @returns District code or original name if not found
 */
export function getAdministrativeDistrictCode(name: string): string {
  if (!administrativeDistrictReverseCache) {
    console.warn('Administrative district reverse cache not initialized. Call initializeDistrictMappings() first.');
    return name;
  }
  return administrativeDistrictReverseCache[name] || name;
}

/**
 * Get all autonomous district names for form options
 * @returns Array of Korean district names
 */
export function getAutonomousDistrictNames(): string[] {
  if (!autonomousDistrictReverseCache) {
    console.warn('Autonomous district reverse cache not initialized. Call initializeDistrictMappings() first.');
    return [];
  }
  return Object.keys(autonomousDistrictReverseCache);
}

/**
 * Get all administrative district names for form options
 * @returns Array of Korean district names
 */
export function getAdministrativeDistrictNames(): string[] {
  if (!administrativeDistrictReverseCache) {
    console.warn('Administrative district reverse cache not initialized. Call initializeDistrictMappings() first.');
    return [];
  }
  return Object.keys(administrativeDistrictReverseCache);
}
