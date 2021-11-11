function PostsOtherProfile({ post, onLike, onDislike }) {
    return (
        <div key={post.id} className="row align-items-center" style={{ marginTop: 40 }}>
            <div className="col-6 mx-auto cardPosts" >
                <b style={{ fontFamily: "Palatino" }}>{post.text}</b> <br /><br />
                <hr />
                <span>
                    <span style={{ flex: 0.4, float: "left", marginTop: 3, fontFamily: "Trebuchet MS", height: 25 }}>
                        <i>Posted on: {post.created}</i>
                    </span>
                    <span style={{ flex: 0.4, float: "right", height: 30 }}>
                        <b>{post.likes} likes</b>&nbsp;&nbsp;&nbsp;
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
        </div>
    )
}

export default PostsOtherProfile
