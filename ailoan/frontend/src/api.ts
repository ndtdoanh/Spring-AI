import type { AiCommandResponse } from "./types";

const API_BASE =
  (import.meta as any).env?.VITE_API_BASE ?? "http://localhost:8080";

function getSessionId(): string {
  const key = "aiolan_session_id";
  const existing = localStorage.getItem(key);
  if (existing) return existing;
  const sid = crypto.randomUUID();
  localStorage.setItem(key, sid);
  return sid;
}

export function getHeaders(adminUser?: string): Headers {
  const headers = new Headers();
  headers.set("Content-Type", "application/json");
  headers.set("X-Session-Id", getSessionId());
  if (adminUser) headers.set("X-Admin-User", adminUser);
  return headers;
}

export async function commandAI(command: string, adminUser?: string): Promise<AiCommandResponse> {
  const res = await fetch(`${API_BASE}/api/ai/command`, {
    method: "POST",
    headers: getHeaders(adminUser),
    body: JSON.stringify({ prompt: command }),
  });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(`HTTP ${res.status}: ${text}`);
  }
  return res.json();
}

