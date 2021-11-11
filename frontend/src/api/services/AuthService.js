import axiosAPI from '../axios/axios';
import authHeader from '../AuthHeader';

class AuthService {
  login(email, password) {
    let loginForm = {
      "email": email,
      "password": password
    }
    return axiosAPI.post("/auth/login", loginForm);
  }

  logout() {
    localStorage.removeItem("user");
  }

  register(email, password, firstName, lastName) {
    let userFrom = {
      "email": email,
      "password": password,
      "firstName": firstName,
      "lastName": lastName
    }
    return axiosAPI.post("/auth/register", userFrom);
  }

  whoami() {
    return axiosAPI.get("/auth/whoami", { headers: authHeader() });
  }


  getCurrentUser() {
    return JSON.parse(localStorage.getItem('user'));;
  }

  getCurrentUserId() {
    return JSON.parse(localStorage.getItem('user')).userId;
  }

}

export default new AuthService();
