import React, { useEffect, useMemo, useState } from "react";
import type { AuditLog, ChatHistory, Loan, Scheme } from "./types";
import {
  commandAI,
  getCurrentSessionId,
  listAuditLog,
  listChatHistory,
  listLoans,
  listSchemes,
} from "./api";

export default function App() {
  const [adminUser, setAdminUser] = useState<string>("admin");
  const [command, setCommand] = useState<string>("Tìm tất cả khoản vay scheme A");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [commandResult, setCommandResult] = useState<null | {
    message: string;
    toolCalled: string;
    affectedCount: number;
  }>(null);

  const [schemes, setSchemes] = useState<Scheme[]>([]);
  const [selectedScheme, setSelectedScheme] = useState<string>("A");
  const [loans, setLoans] = useState<Loan[]>([]);
  const [auditLog, setAuditLog] = useState<AuditLog[]>([]);
  const [chatHistory, setChatHistory] = useState<ChatHistory[]>([]);

  const sessionId = useMemo(() => getCurrentSessionId(), []);

  async function refreshAll() {
    const [s, l, a, c] = await Promise.all([
      listSchemes(),
      listLoans(selectedScheme),
      listAuditLog(),
      listChatHistory(),
    ]);
    setSchemes(s);
    setLoans(l);
    setAuditLog(a);
    setChatHistory(c);
  }

  useEffect(() => {
    refreshAll().catch((e) => setError(String(e?.message ?? e)));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    listLoans(selectedScheme)
      .then(setLoans)
      .catch((e) => setError(String(e?.message ?? e)));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedScheme]);

  async function onSend() {
    setLoading(true);
    setError(null);
    try {
      const res = await commandAI(command, adminUser);
      setCommandResult(res);
      // làm mới các bảng để thấy tool đã ảnh hưởng gì
      const next = await Promise.all([
        listSchemes(),
        listLoans(selectedScheme),
        listAuditLog(),
        listChatHistory(),
      ]);
      setSchemes(next[0]);
      setLoans(next[1]);
      setAuditLog(next[2]);
      setChatHistory(next[3]);
      // nếu admin vừa sửa scheme nhưng đang chọn scheme khác, vẫn để user tự refresh
    } catch (e: any) {
      setError(String(e?.message ?? e));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="container">
      <h2>AI Loan Admin (Demo)</h2>
      <div className="muted">
        Session: <span className="mono">{sessionId}</span>
      </div>

      <div style={{ height: 12 }} />

      <div className="grid">
        <div className="card">
          <div className="row" style={{ marginBottom: 10 }}>
            <div style={{ flex: 1 }}>
              <div className="muted" style={{ marginBottom: 6 }}>
                X-Admin-User
              </div>
              <input type="text" value={adminUser} onChange={(e) => setAdminUser(e.target.value)} />
            </div>
          </div>

          <div className="muted" style={{ marginBottom: 6 }}>
            Lệnh tiếng Việt
          </div>
          <textarea value={command} onChange={(e) => setCommand(e.target.value)} />

          <div style={{ height: 10 }} />

          <button onClick={onSend} disabled={loading}>
            {loading ? "Đang chạy..." : "Gửi lệnh"}
          </button>

          {error ? (
            <>
              <div style={{ height: 10 }} />
              <div style={{ color: "#b00020" }} className="mono">
                {error}
              </div>
            </>
          ) : null}

          {commandResult ? (
            <>
              <div style={{ height: 12 }} />
              <div className="muted">Kết quả lệnh</div>
              <div style={{ marginTop: 6 }}>
                <div>
                  <b>message:</b> {commandResult.message}
                </div>
                <div>
                  <b>toolCalled:</b> {commandResult.toolCalled}
                </div>
                <div>
                  <b>affectedCount:</b> {commandResult.affectedCount}
                </div>
              </div>
            </>
          ) : null}

          <div style={{ height: 14 }} />
          <button className="secondary" onClick={() => refreshAll().catch((e) => setError(String(e?.message ?? e)))}>
            Refresh
          </button>
        </div>

        <div className="card">
          <div className="muted" style={{ marginBottom: 6 }}>
            Scheme đang có
          </div>
          <select
            value={selectedScheme}
            onChange={(e) => setSelectedScheme(e.target.value)}
            style={{ width: "100%", padding: 10, borderRadius: 8, border: "1px solid #ddd" }}
          >
            {schemes.map((s) => (
              <option key={s.id} value={s.name}>
                {s.name}
              </option>
            ))}
          </select>

          <div className="muted" style={{ marginTop: 12, marginBottom: 6 }}>
            Loans (lọc theo scheme)
          </div>
          <div className="list">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Customer</th>
                  <th>Số tiền</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {loans.map((l) => (
                  <tr key={l.id}>
                    <td className="mono">{l.id}</td>
                    <td>{l.customerName}</td>
                    <td className="mono">{Number(l.amount).toLocaleString("vi-VN")}</td>
                    <td>{l.status}</td>
                  </tr>
                ))}
                {loans.length === 0 ? (
                  <tr>
                    <td colSpan={4} className="muted">
                      Không có dữ liệu
                    </td>
                  </tr>
                ) : null}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div style={{ height: 14 }} />

      <div className="grid" style={{ gridTemplateColumns: "1fr 1fr" }}>
        <div className="card">
          <div className="muted" style={{ marginBottom: 6 }}>
            Audit Log (tool đã gọi)
          </div>
          <div className="list">
            <table>
              <thead>
                <tr>
                  <th>Time</th>
                  <th>Admin</th>
                  <th>Tool</th>
                </tr>
              </thead>
              <tbody>
                {auditLog.slice(0, 30).map((a) => (
                  <tr key={a.id}>
                    <td className="mono">{a.createdAt}</td>
                    <td>{a.adminUser}</td>
                    <td className="mono">{a.toolCalled}</td>
                  </tr>
                ))}
                {auditLog.length === 0 ? (
                  <tr>
                    <td colSpan={3} className="muted">
                      Chưa có audit
                    </td>
                  </tr>
                ) : null}
              </tbody>
            </table>
          </div>
        </div>

        <div className="card">
          <div className="muted" style={{ marginBottom: 6 }}>
            Chat History
          </div>
          <div className="list">
            <table>
              <thead>
                <tr>
                  <th>Role</th>
                  <th>Nội dung</th>
                </tr>
              </thead>
              <tbody>
                {chatHistory.slice(-30).map((c) => (
                  <tr key={c.id}>
                    <td className="mono">{c.role}</td>
                    <td>{c.content}</td>
                  </tr>
                ))}
                {chatHistory.length === 0 ? (
                  <tr>
                    <td colSpan={2} className="muted">
                      Chưa có hội thoại
                    </td>
                  </tr>
                ) : null}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}

