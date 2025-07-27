/**
 * @file initMapUtils.ts
 * @author Gahui Baek
 * @description window.* 유틸 전역 등록 초기화 함수
 */

function initFullMapUtils() {
  if (typeof window === 'undefined') return;

  // 유틸 모듈들 동적 require로 window 전역에 등록
  window.geoUtils = require('./geoUtils').geoUtils;
  window.polygonUtils = require('./polygonUtils').polygonUtils;
  window.labelUtils = require('./labelUtils').labelUtils;
  window.models = require('./models').models;
  window.mapEventHandlers = require('./mapEventHandlers').mapEventHandlers;
  window.visualMapping = require('./visualMapping').visualMapping;
  window.mapInitializer = require('./mapInitializer').mapInitializer;
  window.mapTileUtils = require('./mapTileUtils').mapTileUtils;
  window.dataLoader = require('./dataLoader').dataLoader;
}

export const initMapUtils =  {
  initFullMapUtils,
}
