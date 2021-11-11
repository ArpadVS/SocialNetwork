
export default function authHeader() {
  const user = JSON.parse(localStorage.getItem('user'));

  if (user && user.accessToken) {
    return { jw_token: user.accessToken };
  } else {
    return {};
  }
}