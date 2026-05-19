import z from "zod";

export const UserLoginRequestSchema = z.object({
  email: z.email("Invalid email address"),
  password: z.string().min(1, "Password is required"),
});
export type UserLoginRequest = z.infer<typeof UserLoginRequestSchema>;

export const UserRegisterRequestSchema = z.object({
  email: z.email("Invalid email address"),
  password: z.string().min(1, "Password is required"),
  name: z.string().min(1, "Name is required"),
});
export type UserRegisterRequest = z.infer<typeof UserRegisterRequestSchema>;
