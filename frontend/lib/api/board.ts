import { fetchWithAuth } from "@/lib/fetchWithAuth";

// Board data interface
export interface BoardDto {
  boardId: number;
  title: string;
  content: string;
  region: string;
  empathyCount: number;
  comments: number;
  createdDate: string;
  districtCode: string;
  autonomousDistrictCode: string;
  autonomousDistrictName: string;
  hasAutonomousDistrictCode: boolean;
  user: {
    nickname: string;
    id: string;
    name: string;
    email: string;
  }
}

// Page response interface
export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalElements: number;
  totalPages: number;
}

/**
 * Get current user's board posts
 * @param page Page number (0-based)
 * @param size Page size
 * @returns Promise with page of board posts
 */
export async function getMyBoards(page = 0, size = 10): Promise<PageResponse<BoardDto>> {
  const response = await fetchWithAuth(`/api/boards/my-board?page=${page}&size=${size}`);

  if (!response.ok) {
    throw new Error('Failed to fetch user boards');
  }

  return response.json();
}

/**
 * Delete a board post
 * @param postId Board post ID to delete
 * @returns Promise with deletion status
 */
export async function deleteBoard(postId: number): Promise<boolean> {
  try {
    const response = await fetchWithAuth(`/api/boards/${postId}`, {
      method: 'DELETE'
    });

    if (!response.ok) {
      throw new Error('Failed to delete board post');
    }

    return true;
  } catch (error) {
    console.error('Delete board error:', error);
    return false;
  }
}
