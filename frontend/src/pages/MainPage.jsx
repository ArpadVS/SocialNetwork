import React from 'react'
import { useState, useEffect } from 'react'

import { ToastrHelper } from '../utils/toastrj';
import PostService from "../api/services/PostService"
import Posts from '../components/Post/Posts'
import WritePostModal from '../components/Modals/WritePostModal';

function MainPage() {
    const [show, setShow] = useState(false);
    const [text, setText] = useState("");
    const [dataLoaded, setDataLoaded] = useState(false);

    const [posts, setPosts] = useState([])

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    useEffect(() => {
        fetchPosts()
    }, [])

    const fetchPosts = () => {
        PostService.getTimeline().then((response) => {
            console.log(response.data);
            setPosts(response.data)
            setDataLoaded(true)
        }).catch(error => {
            console.log(error.response.data);
            ToastrHelper.error(error.response.data)
            setDataLoaded(true)
        })
    }

    // pass these down to the Post tag, the update
    const likePost = (id) => {
        PostService.likePost(id).then((response) => {
            console.log(response.data);
            setPosts(posts.map((post) => {
                let prevLikes = post.likes
                return post.id === id ? { ...post, likes: prevLikes + 1, isLiked: true } : post
            }
            ))
            //ToastrHelper.info("Post liked.")
        }).catch(error => {
            console.log(error.response.data);
            ToastrHelper.error(error.response.data)
        })
    }

    const dislikePost = (id) => {
        PostService.dislikePost(id).then((response) => {
            console.log(response.data);
            setPosts(posts.map((post) => {
                let prevLikes = post.likes
                return post.id === id ? { ...post, likes: prevLikes - 1, isLiked: false } : post
            }
            ))
            //ToastrHelper.info("Post disliked.")
        }).catch(error => {
            console.log(error.response.data);
            ToastrHelper.error(error.response.data)
        })
    }


    const onSubmit = (e) => {
        e.preventDefault()

        PostService.createPost(text.trim()).then((response) => {
            setText('');
            handleClose();
            ToastrHelper.success('Post created!');
            fetchPosts()

        }).catch(error => {

            console.log(error.response.data);
            ToastrHelper.error(error.response.data)
        })

    }

    return (
        <div>

            <div className="row justify-content-md-center" style={{ marginTop: 20 }} >
                <span style={{ flex: 0.35 }} />
                <textarea id="createPostTextarea" style={{ flex: 0.3 }} rows="2" placeholder="What's on your mind?" onClick={handleShow} />
                <span style={{ flex: 0.35 }} />
            </div>
            {(posts.length > 0 && (
                <Posts posts={posts} onLike={likePost} onDislike={dislikePost} />
            ))}
            {
                (posts.length === 0 && dataLoaded && (
                    <div className="row" style={{ marginTop: 40 }}>
                        <div className="col-3" />
                        <div className="col-6 mx-auto" style={{ textAlign: "center" }}>
                            <h2>Sadly there are no posts to show. <br />Try adding new friends!</h2>
                        </div>
                        <div className="col-3" />
                    </div>
                ))
            }

            <WritePostModal show={show} handleClose={handleClose} text={text} onSubmit={onSubmit} setText={setText}></WritePostModal>
        </div>
    )
}

export default MainPage
