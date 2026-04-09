export type AiCommandResponse = {
  answer: string;
  product: {
    id: number;
    name: string;
    price: number;
    status: string;
  };
};
