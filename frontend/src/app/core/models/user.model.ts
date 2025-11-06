export interface User {
  id?: number;
  uuid?: string;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  active?: boolean;
  createdAt?: Date;
  updatedAt?: Date;
}