import React from 'react'
import Post from './Post'

function Posts({ posts, onLike, onDislike }) {
    return (
        <React.Fragment>
            {posts.map(p => (
                <div key={p.id} className="row align-items-center">
                    <Post key={p.id} post={p} onLike={onLike} onDislike={onDislike}></Post>
                </div>
            ))}
        </React.Fragment>
    )
}

export default Posts
