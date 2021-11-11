import { AiOutlineEdit } from "react-icons/ai"
import { FaTimes } from "react-icons/fa"
import React, { useState } from 'react'
import DeletionConfirmModal from "../Modals/DeletionConfirmModal";

function PostsMyProfile({ post, handleUpdatePostShow, deletePost }) {
    const [showConfirm, setShowConfirm] = useState(false);

    // open-close delete confirm modal
    const handleConfirmClose = () => setShowConfirm(false);
    const handleConfirmShow = () => setShowConfirm(true);

    return (
        <React.Fragment>
            <div key={post.id} className="row align-items-center" style={{ marginTop: 40 }}>
                <div className="col-6 mx-auto cardPosts" >
                    <b style={{ fontFamily: "Palatino" }}>{post.text}</b> <br /><br />
                    <hr />
                    <span>
                        <span style={{ flex: 0.4, float: "left", marginTop: 3, fontFamily: "Trebuchet MS", height: 25 }}>
                            <i>Posted on: {post.created}</i>
                        </span>
                        <span style={{ flex: 0.4, float: "right", height: 30 }}>
                            <b>{post.likes} likes</b>
                            <AiOutlineEdit style={{ color: 'blue', width: 30, height: 30, cursor: 'pointer', marginLeft: 50 }} onClick={() => handleUpdatePostShow(post.id, post.text)} />
                            <FaTimes style={{ color: 'red', width: 30, height: 30, cursor: 'pointer', marginLeft: 10 }} onClick={() => handleConfirmShow()} />

                        </span>
                    </span>
                </div>
                <DeletionConfirmModal showConfirm={showConfirm} handleConfirmClose={handleConfirmClose} text="Are you sure about deleting this post?" onClickHandler={deletePost} id={post.id} />
            </div>

        </React.Fragment>
    )
}

export default PostsMyProfile
