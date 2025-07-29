import { getUserInfo } from "@/lib/api/user";

/**
 * User information interface for compatibility with existing code
 */
export interface CurrentUser {
  userId: number;
  nickname: string;
}

/**
 * Get current user information from the /api/user endpoint
 * This replaces the old getUserFromToken function that read from localStorage
 * @returns Promise with user data or null if not authenticated
 */
export async function getCurrentUser(): Promise<CurrentUser | null> {
  try {
    const userData = await getUserInfo();

    // Map the API response to the expected format for compatibility
    return {
      userId: userData.id!,
      nickname: userData.nickname,
    };
  } catch (error) {
    // If the API call fails (e.g., user not authenticated), return null
    console.error('Failed to get current user:', error);
    return null;
  }
}

/**
 * Legacy function name for backward compatibility
 * @deprecated Use getCurrentUser() instead
 */
export const getUserFromToken = getCurrentUser;
