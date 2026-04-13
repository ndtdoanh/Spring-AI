import React, { useState } from "react";
import { commandAI } from "./api";
import type { AiCommandResponse } from "./types";

export default function App() {
  const [adminUser, setAdminUser] = useState<string>("admin");
  const [command, setCommand] = useState<string>("Lấy thông tin sản phẩm id 1");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [answer, setAnswer] = useState<string>("");
  const [dbProduct, setDbProduct] = useState<AiCommandResponse["product"] | null>(null);

  async function onSend() {
    setLoading(true);
    setError(null);
    try {
      const res = await commandAI(command, adminUser);
      setAnswer(res.answer);
      setDbProduct(res.product);
    } catch (e: any) {
      setError(String(e?.message ?? e));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="container">
      <h2>AI Product Admin (Minimal)</h2>
      <div className="card">
        <div className="muted" style={{ marginBottom: 6 }}>
          X-Admin-User
        </div>
        <input type="text" value={adminUser} onChange={(e) => setAdminUser(e.target.value)} />

        <div className="muted" style={{ marginTop: 12, marginBottom: 6 }}>
          Prompt cho AI (get/update product)
        </div>
        <textarea value={command} onChange={(e) => setCommand(e.target.value)} />

        <div style={{ height: 10 }} />
        <button onClick={onSend} disabled={loading}>
          {loading ? "Đang xử lý..." : "Gửi prompt"}
        </button>

        {error ? (
          <div style={{ marginTop: 10, color: "#b00020" }} className="mono">
            {error}
          </div>
        ) : null}

        <div style={{ marginTop: 14 }}>
          <div className="muted" style={{ marginBottom: 6 }}>
            AI trả lời
          </div>
          <div className="answer-box">{answer || "Chưa có kết quả."}</div>
        </div>

        <div style={{ marginTop: 14 }}>
          <div className="muted" style={{ marginBottom: 6 }}>
            Dữ liệu DB trả về
          </div>
          <div className="answer-box mono">
            {dbProduct
              ? JSON.stringify(dbProduct, null, 2)
              : "Chưa có dữ liệu DB."}
          </div>
        </div>
      </div>
    </div>
  );
}

