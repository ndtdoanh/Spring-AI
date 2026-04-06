export type Scheme = {
  id: number;
  name: string;
  infoA: string | null;
  infoB: string | null;
  infoC: string | null;
  infoD: string | null;
  updatedAt: string;
};

export type Loan = {
  id: number;
  scheme: string;
  customerName: string;
  amount: number;
  status: string;
  createdAt: string;
};

export type AuditLog = {
  id: number;
  adminUser: string;
  prompt: string;
  toolCalled: string;
  params: string | null;
  result: string | null;
  createdAt: string;
};

export type ChatHistory = {
  id: number;
  sessionId: string;
  role: string;
  content: string;
  createdAt: string;
};

export type AiCommandResponse = {
  message: string;
  toolCalled: string;
  affectedCount: number;
};

