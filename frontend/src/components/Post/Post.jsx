import React from "react";
import { useHistory } from "react-router-dom";
function Post({ post, onLike, onDislike }) {
    const history = useHistory();

    const visitUserPage = (id) => {
        history.push("/user/" + id);
    }

    return (
        <React.Fragment>
            <div className="col-2" style={{ borderColor: "lightblue" }}></div>
            <div className="col-2" style={{ cursor: "pointer", textAlign: "center" }} onClick={() => { visitUserPage(post.userId) }}>

                <img className="post-profile-picture" src={process.env.REACT_APP_BASE_URL + '/users/picture/' + post.picture}
                    alt="Avatar" /> &nbsp;&nbsp;
                <b>{post.firstName} {post.lastName}</b>
            </div>

            <div className="col-6 cardPosts" >
                <b style={{ fontFamily: "Palatino" }}>{post.text}</b> <br /><br />
                <hr />
                <span>
                    <span style={{ flex: 0.4, float: "left", marginTop: 3, fontFamily: "Trebuchet MS", height: 25 }}>
                        <i>Posted on: {post.created}</i>
                    </span>
                    <span style={{ flex: 0.4, float: "right", height: 25 }}>

                        <b>{post.likes} likes</b> &nbsp;&nbsp;&nbsp;
                        {
                            post.isLiked ? (
                                <button onClick={() => onDislike(post.id)}> Dislike</button>
                            ) : (
                                <button onClick={() => onLike(post.id)} style={{ cursor: "pointer" }}> Like</button>
                            )
                        }
                    </span>
                </span>
            </div>

            <div className="col-2"></div>
        </React.Fragment>
    )
}

export default Post
