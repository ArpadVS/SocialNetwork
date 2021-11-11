import axiosAPI from '../axios/axios';
import authHeader from '../AuthHeader'

class UserService {

    getUserByID(id) {
        return axiosAPI.get(`/users/${id}`, { headers: authHeader() });
    }

    getRelationshipWithUser(id) {
        return axiosAPI.get(`/users/relationship/${id}`, { headers: authHeader() });
    }

    searchUsers(text) {
        return axiosAPI.post("/users/search", { "text": text }, { headers: authHeader() });
    }

    getMyFriends() {
        return axiosAPI.get("/users/friends", { headers: authHeader() });
    }

    updateUser(email, firstName, lastName) {
        let updUser = {
            "email": email,
            "firstName": firstName,
            "lastName": lastName
        }

        return axiosAPI.put("/users", updUser, { headers: authHeader() });
    }

    uploadPicture(formData) {
        return axiosAPI.post("/users/picture", formData, { headers: authHeader() });
    }


}

export default new UserService();