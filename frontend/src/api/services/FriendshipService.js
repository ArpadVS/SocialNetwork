import axiosAPI from '../axios/axios';
import authHeader from '../AuthHeader';

class FriendshipService {

  sendFriendRequest(userId) {
    return axiosAPI.post("/requests/send/" + userId, {}, { headers: authHeader() });
  }

  acceptFriendRequest(requestId) {
    return axiosAPI.post("/requests/accept/" + requestId, {}, { headers: authHeader() });
  }

  rejectFriendRequest(requestId) {
    return axiosAPI.post("/requests/reject/" + requestId, {}, { headers: authHeader() });
  }

  getFriendRequests() {
    return axiosAPI.get("/requests", { headers: authHeader() });
  }

}

export default new FriendshipService();