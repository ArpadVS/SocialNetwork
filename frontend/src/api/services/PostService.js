import axiosAPI from '../axios/axios';
import authHeader from '../AuthHeader'

class PostService {

  createPost(text) {
    let postForm = {
      "text": text
    }
    return axiosAPI.post("/posts", postForm, { headers: authHeader() });
  }

  updatePost(id, text) {
    let updatedPost = {
      "text": text
    }
    return axiosAPI.put("/posts/update/" + id, updatedPost, { headers: authHeader() });
  }

  deletePost(id) {
    return axiosAPI.delete("/posts/" + id, { headers: authHeader() });
  }

  getTimeline() {
    return axiosAPI.get('/posts/timeline', { headers: authHeader() });
  }

  likePost(id) {
    return axiosAPI.post("/posts/like/" + id, {}, { headers: authHeader() });
  }


  dislikePost(id) {
    return axiosAPI.post("/posts/dislike/" + id, {}, { headers: authHeader() });
  }


  getPostsFromUser(id) {
    return axiosAPI.get('/posts/user/' + id, { headers: authHeader() });
  }

}

export default new PostService();