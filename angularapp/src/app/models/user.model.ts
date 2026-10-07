export class User {
  userId?: number;
  email: string = '';
  password: string = '';
  username: string = '';
  mobileNumber: string = '';
  /** Not sent by the sign-up page any more: the server decides the role (Customer). */
  userRole?: string;
}
