import { fetchWithAuth } from "@/lib/fetchWithAuth";

// User data interface
export interface UserData {
  id?: number;
  userNm?: string;
  nickname: string;
  email: string;
  autonomousDistrict: string;
  administrativeDistrict: string;
  createdDate?: string;
  modifiedDate?: string;
}

// User activity stats interface
export interface UserActivityStats {
  totalLikes: number;
  totalComments: number;
  autonomousDistrictKo: string;
  administrativeDistrictKo: string;
}

/**
 * Get current user information
 * @returns Promise with user data
 */
export async function getUserInfo(): Promise<UserData> {
  const response = await fetchWithAuth('/api/user');

  if (!response.ok) {
    throw new Error('Failed to fetch user information');
  }

  return response.json();
}

/**
 * Get user activity statistics
 * @returns Promise with user activity stats
 */
export async function getUserActivityStats(): Promise<UserActivityStats> {
  const response = await fetchWithAuth('/api/user/activity-stats');

  if (!response.ok) {
    throw new Error('Failed to fetch user activity statistics');
  }

  return response.json();
}

/**
 * Update user information
 * @param userData User data to update
 * @returns Promise with updated user data
 */
export async function updateUserInfo(userData: Partial<UserData>): Promise<UserData> {
  const response = await fetchWithAuth('/api/user', {
    method: 'PUT',
    body: JSON.stringify(userData)
  });

  if (!response.ok) {
    throw new Error('Failed to update user information');
  }

  return response.json();
}

/**
 * Clear cookie by setting it to expire immediately
 * @param name Cookie name to clear
 */
function clearCookie(name: string): void {
  document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/; Secure;`;
  sessionStorage.clear();
}

/**
 * Logout user
 * @returns Promise with logout status
 */
export async function logout(): Promise<boolean> {
  try {
    const response = await fetchWithAuth('/api/auth/logout', {
      method: 'POST'
    });

    if (!response.ok) {
      throw new Error('Failed to logout');
    }

    // Clear tokens from localStorage
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');

    // Clear tokens from cookies (for any non-HttpOnly cookies)
    clearCookie('accessToken');
    clearCookie('refreshToken');

    return true;
  } catch (error) {
    console.error('Logout error:', error);
    return false;
  }
}

/**
 * Delete user account
 * @returns Promise with deletion status
 */
export async function deleteUser(): Promise<boolean> {
  try {
    const response = await fetchWithAuth('/api/user', {
      method: 'DELETE'
    });

    if (!response.ok) {
      throw new Error('Failed to delete user account');
    }

    // Clear tokens from localStorage after successful deletion
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    // Clear tokens from cookies (for any non-HttpOnly cookies)
    clearCookie('accessToken');
    clearCookie('refreshToken');
    return true;
  } catch (error) {
    console.error('Delete user error:', error);
    return false;
  }
}
