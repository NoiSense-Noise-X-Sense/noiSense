import { fetchWithAuth } from './fetchWithAuth';

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';

export interface BoardPost {
  boardId: number;
  userId: number;
  nickname: string;
  title: string;
  content: string;
  emotionalScore: number;
  empathyCount: number;
  viewCount: number;
  commentCount: number;
  autonomousDistrict: string;
  administrativeDistrict: string;
  createdDate: string; // LocalDateTime이지만 프론트에서는 string으로 받음
  modifiedDate: string;
  isEmpathized?: boolean; // 내가 이미 공감했는지 여부(상세)
}

export interface BoardSearchResponse {
  content: BoardPost[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export async function getBoards(page = 0, size = 10): Promise<BoardSearchResponse> {
  const res = await fetchWithAuth(`${BASE_URL}/api/boards?page=${page}&size=${size}`);
  if (!res.ok) throw new Error('게시글 목록을 불러오지 못했습니다.');
  return res.json();
}

export async function searchBoards(keyword: string, page = 0, size = 10): Promise<BoardSearchResponse> {
  const res = await fetchWithAuth(`${BASE_URL}/api/boards/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`);
  if (!res.ok) throw new Error('게시글 검색에 실패했습니다.');
  return res.json();
}

export async function getBoardById(id: number): Promise<BoardPost> {
  const res = await fetchWithAuth(`${BASE_URL}/api/boards/${id}`);
  if (!res.ok) throw new Error('게시글 상세 조회에 실패했습니다.');
  return res.json();
}

export async function createBoard(data: Omit<BoardPost, 'boardId' | 'createdDate' | 'modifiedDate' | 'viewCount' | 'empathyCount' | 'commentCount'>): Promise<BoardPost | null> {
  // 백엔드에서 List<BoardDto>로 받으므로 배열로 감싸서 전송
  const res = await fetchWithAuth(`${BASE_URL}/api/boards`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify([data]),
  });
  if (!res.ok) throw new Error('게시글 작성에 실패했습니다.');
  // 응답이 body 없이 200 OK만 내려올 수 있으므로 예외처리
  try {
    return await res.json();
  } catch {
    return null;
  }
}

export async function updateBoard(id: number, userId: number, data: Partial<BoardPost>): Promise<BoardPost> {
  const res = await fetchWithAuth(`${BASE_URL}/api/boards/${id}?userId=${userId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error('게시글 수정에 실패했습니다.');
  return res.json();
}

export async function deleteBoard(id: number, userId: number): Promise<void> {
  const res = await fetchWithAuth(`${BASE_URL}/api/boards/${id}?userId=${userId}`, {
    method: 'DELETE',
  });
  if (!res.ok) throw new Error('게시글 삭제에 실패했습니다.');
}

export async function toggleEmpathy(boardId: number): Promise<void> {
  const res = await fetchWithAuth(`${BASE_URL}/api/boards/${boardId}/empathy`, {
    method: 'POST',
  });
  if (!res.ok) throw new Error('공감 처리에 실패했습니다.');
}

// 댓글(Comment) 관련 API
export interface Comment {
  id: number;
  board_id: number;
  user_id: number;
  nickname: string;
  content: string;
  created_date: string;
  updated_date: string;
}

export async function getComments(boardId: number): Promise<Comment[]> {
  const res = await fetchWithAuth(`${BASE_URL}/api/comments?boardId=${boardId}`);
  if (!res.ok) throw new Error('댓글 목록을 불러오지 못했습니다.');
  return res.json();
}

export async function createComment(data: { boardId: number; content: string }): Promise<Comment> {
  const res = await fetchWithAuth(`${BASE_URL}/api/comments`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      board_id: data.boardId,
      content: data.content
    }),
  });
  if (!res.ok) throw new Error('댓글 작성에 실패했습니다.');
  return res.json();
}



export async function deleteComment(id: number): Promise<void> {
  const res = await fetchWithAuth(`${BASE_URL}/api/comments/${id}`, {
    method: 'DELETE',
  });
  if (!res.ok) throw new Error('댓글 삭제에 실패했습니다.');
} 