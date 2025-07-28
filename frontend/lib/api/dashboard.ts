import {fetchWithAuth} from "@/lib/fetchWithAuth";

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';

export async function fetchSummary(district: string) {
  const res = await fetchWithAuth(`/api/dashboard/summary?district=${district}`);
  if (!res.ok) throw new Error('Failed to fetch summary data');
  return res.json();
}

export async function fetchHourly(district: string) {
  const res = await fetchWithAuth(`/api/dashboard/hourly?district=${district}`);
  if (!res.ok) throw new Error('Failed to fetch hourly data');
  return res.json();
}

export async function fetchYearly(district: string) {
  const res = await fetchWithAuth(`/api/dashboard/yearly?district=${district}`);
  if (!res.ok) throw new Error('Failed to fetch yearly data');
  return res.json();
}

export async function fetchZone(district: string) {
  const res = await fetchWithAuth(`/api/dashboard/zone?district=${district}`);
  if (!res.ok) throw new Error('Failed to fetch zone data');
  return res.json();
}

export async function fetchComplaints(district: string) {
  const res = await fetchWithAuth(`/api/dashboard/complaints?district=${district}`);
  if (!res.ok) throw new Error('Failed to fetch complaints data');
  return res.json();
}

export async function fetchDistrictList() {
  const res = await fetchWithAuth(`/api/dashboard/districts`);
  if (!res.ok) throw new Error('Failed to fetch district list');
  return res.json(); // [{ code: "11230", nameKo: "강남구", nameEn: "Gangnam-gu" }, ...]
}
