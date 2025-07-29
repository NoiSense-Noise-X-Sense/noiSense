// lib/map/kakaoLoader.ts

const KAKAO_SDK_URL = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${process.env.NEXT_PUBLIC_KAKAO_API_KEY}&libraries=services&autoload=false`;

export function loadKakaoSdk(): Promise<void> {
  return new Promise((resolve, reject) => {
    if (typeof window === 'undefined') return reject('Not in browser');

    if (window.kakao && window.kakao.maps) return resolve(); // 이미 로드됨

    const script = document.createElement('script');
    script.src = KAKAO_SDK_URL;
    script.async = true;
    script.onload = () => {
      if (window.kakao && window.kakao.maps && window.kakao.maps.load) {
        window.kakao.maps.load(() => {
          resolve();
        });
      } else {
        reject('카카오맵 SDK 로드 실패');
      }
    };
    script.onerror = () => reject('카카오맵 SDK 스크립트 로드 실패');
    document.head.appendChild(script);
  });
}
