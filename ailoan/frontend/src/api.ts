import type { AiCommandResponse, AuditLog, ChatHistory, Loan, Scheme } from "./types";

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
  const res = await fetch(`${API_BASE}/api/admin/ai/command`, {
    method: "POST",
    headers: getHeaders(adminUser),
    body: JSON.stringify({ command }),
  });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(`HTTP ${res.status}: ${text}`);
  }
  return res.json();
}

export async function listSchemes(): Promise<Scheme[]> {
  const res = await fetch(`${API_BASE}/api/schemes`, { method: "GET" });
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return res.json();
}

export async function listLoans(scheme?: string): Promise<Loan[]> {
  const url = new URL(`${API_BASE}/api/loans`);
  if (scheme) url.searchParams.set("scheme", scheme);
  const res = await fetch(url.toString(), { method: "GET" });
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return res.json();
}

export async function listAuditLog(): Promise<AuditLog[]> {
  const res = await fetch(`${API_BASE}/api/audit-log`, { method: "GET" });
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return res.json();
}

export async function listChatHistory(): Promise<ChatHistory[]> {
  const headers = getHeaders();
  const sid = headers.get("X-Session-Id")!;
  const res = await fetch(`${API_BASE}/api/chat-history?sessionId=${encodeURIComponent(sid)}`, {
    method: "GET",
    headers,
  });
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return res.json();
}

export function getCurrentSessionId(): string {
  return getSessionId();
}

