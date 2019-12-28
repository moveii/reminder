/**
 * Returned by the server when successfully logged in. Contains the JWT and the corresponding username.
 */
export class Token {

  username: string;
  token: string;

}
