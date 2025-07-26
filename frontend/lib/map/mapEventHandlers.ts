/**
 * @file mapEventHandlers.ts
 * @author Gahui Baek
 * @description 카카오맵 이벤트 핸들러 (줌 변경, 이동 제한, 마우스 이동 등)
 */

// =====================
// 타입 정의
// =====================

interface Area {
  name: string;
  districtCode: string;
  center: kakao.maps.LatLng;
}

type PolygonMap = Map<string, kakao.maps.Polygon>;
type LabelMap = Map<string, kakao.maps.CustomOverlay>;

// =====================
// 상태 변수
// =====================

let isInAutonomousMode = true;
let isInZoomPanMode = false;
let currentLevel = 9;

const autonomousPolygons = new Map<string, kakao.maps.Polygon>();
const administrativePolygons = new Map<string, kakao.maps.Polygon>();
const polygonMap = new Map<string, kakao.maps.Polygon>();

const autonomousLabels = new Map<string, kakao.maps.CustomOverlay>();
const administrativeLabels = new Map<string, kakao.maps.CustomOverlay>();

// =====================
// 이벤트 핸들러
// =====================

function handleZoomChanged(
  map: kakao.maps.Map,
  currentCenter: kakao.maps.LatLng,
  maxDistance: number
) {
  const level = map.getLevel();
  console.log('zoom_changed 발생, 현재 level:', level);

  window.mapEventHandlers.updateViewByZoomLevel(level, map);

  if (level > window.geoUtils.MAX_ZOOM_LEVEL) {
    console.log('레벨 9보다 큼 → 다시 되돌림');
    map.setLevel(window.geoUtils.MAX_ZOOM_LEVEL);
    setTimeout(() => {
      map.panTo(window.geoUtils.getSeoulCenter());
    }, 100);
  }

  if (level < window.geoUtils.MIN_ZOOM_LEVEL) {
    console.log('레벨 6보다 작음 → 다시 6으로 되돌림');
    map.setLevel(window.geoUtils.MIN_ZOOM_LEVEL);
    window.isInZoomPanMode = true;
  } else {
    window.isInZoomPanMode = false;
  }
}

function updateViewByZoomLevel(level: number, map: kakao.maps.Map) {
  const showAutonomous = level > 7;
  const showAdministrative = level >= window.geoUtils.MIN_ZOOM_LEVEL && level <= 7;

  window.mapEventHandlers.isInAutonomousMode = showAutonomous;

  window.mapEventHandlers.polygons.autonomousPolygons.forEach(p =>
    p.setMap(showAutonomous ? map : null)
  );
  window.mapEventHandlers.polygons.administrativePolygons.forEach(p =>
    p.setMap(showAdministrative ? map : null)
  );
  window.mapEventHandlers.labels.autonomousLabels.forEach(l =>
    l.setMap(showAutonomous ? map : null)
  );
  window.mapEventHandlers.labels.administrativeLabels.forEach(l =>
    l.setMap(showAdministrative ? map : null)
  );
}

function handleIdle(
  map: kakao.maps.Map,
  autonomousAreas: Area[],
  maxDistance: number
) {
  const center = map.getCenter();
  const d = window.geoUtils.haversineDistance(
    center.getLat(),
    center.getLng(),
    window.geoUtils.getSeoulCenter().getLat(),
    window.geoUtils.getSeoulCenter().getLng()
  );

  if (d > maxDistance) {
    const nearest = window.geoUtils.findNearestAreaCenter(center, autonomousAreas);
    if (nearest) {
      console.log(`중심 좌표 복원 → ${nearest.name}`);
      map.panTo(nearest.center);
    }
  }
}

function handleMouseMove(
  map: kakao.maps.Map,
  cursor: kakao.maps.LatLng,
  autonomousAreas: Area[],
  polygonMap: PolygonMap
) {
  if (!window.isInZoomPanMode) return;

  const nearest = window.geoUtils.findNearestAreaCenter(cursor, autonomousAreas);
  if (!nearest || nearest.districtCode === window.lastDistrictCode) return;

  window.lastDistrictCode = nearest.districtCode;
  map.panTo(nearest.center);
  console.log(`가장 가까운 지역: ${nearest.name}`);

  const polygon = polygonMap.get(nearest.districtCode);
  if (polygon) {
    if (window.lastPolygon) {
      window.lastPolygon.setOptions({ strokeWeight: 1, strokeColor: '#424242' });
    }
    polygon.setOptions({ strokeWeight: 4, strokeColor: '#000000' });
    window.lastPolygon = polygon;

    clearTimeout(window.highlightTimer);
    window.highlightTimer = setTimeout(() => {
      polygon.setOptions({ strokeWeight: 1, strokeColor: '#424242' });
      window.lastPolygon = null;
    }, 500);
  }
}

// =====================
// 객체로 묶어 전역 등록
// =====================

declare global {
  interface Window {
    mapEventHandlers: {
      isInAutonomousMode: boolean;
      isInZoomPanMode: boolean;
      currentLevel: number;
      polygons: {
        autonomousPolygons: PolygonMap;
        administrativePolygons: PolygonMap;
        polygonMap: PolygonMap;
      };
      labels: {
        autonomousLabels: LabelMap;
        administrativeLabels: LabelMap;
      };
      handleZoomChanged: typeof handleZoomChanged;
      updateViewByZoomLevel: typeof updateViewByZoomLevel;
      handleIdle: typeof handleIdle;
      handleMouseMove: typeof handleMouseMove;
    };
    isInZoomPanMode: boolean;
    lastDistrictCode?: string;
    lastPolygon?: kakao.maps.Polygon | null;
    highlightTimer?: ReturnType<typeof setTimeout>;
  }
}

export const mapEventHandlers = {
  isInAutonomousMode,
  isInZoomPanMode,
  currentLevel,
  polygons: {
    autonomousPolygons,
    administrativePolygons,
    polygonMap
  },
  labels: {
    autonomousLabels,
    administrativeLabels
  },
  handleZoomChanged,
  updateViewByZoomLevel,
  handleIdle,
  handleMouseMove
};
