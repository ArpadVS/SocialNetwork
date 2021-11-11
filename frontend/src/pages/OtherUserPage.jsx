import React, { useState, useEffect } from 'react'
import { ToastrHelper } from '../utils/toastrj';
import { useHistory } from "react-router-dom";

import UserService from "../api/services/UserService"
import PostService from "../api/services/PostService"
import AuthService from '../api/services/AuthService';
import FriendshipService from '../api/services/FriendshipService';

import "./ProfilePage.css";
import PostsOtherProfile from '../components/Post/PostsOtherProfile';

function OtherUserPage() {
    let emptyUser = {
        "firstName": "",
        "lastName": "",
        "email": "",
        "picture": "default.png",
        "registrationDate": "",
        "id": 0
    }

    const [userInfo, setUserInfo] = useState(emptyUser)
    const [isFriend, setIsFriend] = useState(false)
    const [isRequestSent, setIsRequestSent] = useState(false)
    const [isRequestReceived, setIsRequestReceived] = useState(false)
    const [requestId, setRequestId] = useState(-1)

    const [isRelationshipLoaded, setRelationshipLoaded] = useState(false)


    const [posts, setPosts] = useState([])
    const [postsLoaded, setPostsLoaded] = useState(false)

    const history = useHistory()


    useEffect(() => {
        extractUserIdFromUrl()
        // eslint-disable-next-line
    }, [])

    function isNumeric(value) {
        return /^\d+$/.test(value);
    }

    const extractUserIdFromUrl = () => {
        var url = document.URL
        var id = url.substring(url.lastIndexOf('/') + 1);

        // if url is not valid, redirect to all users
        if (!isNumeric(id)) {
            history.push('/users')
            return
        }

        // if user id is logged in user's, redirect to profile page
        if ((AuthService.getCurrentUserId() + '') === id) {
            history.push('/profile')
            return
        }

        getUserInfo(id)
        getRelationship(id)
        return true
    }

    const getUserInfo = (id) => {
        UserService.getUserByID(id).then((response) => {
            console.log(response.data);
            setUserInfo(response.data)
            //ToastrHelper.info("Post liked.")
        }).catch(error => {
            console.log(error.response.data);
            ToastrHelper.error(error.response.data)
        })
    }


    const getRelationship = (id) => {
        UserService.getRelationshipWithUser(id).then((response) => {
            console.log(response.data);
            setIsFriend(response.data.isFriend)
            setIsRequestSent(response.data.isRequestSent)
            setIsRequestReceived(response.data.isRequestReceived)
            setRequestId(response.data.requestId)
            setRelationshipLoaded(true)
            if (response.data.isFriend) {
                getPosts(id)
            }
        }).catch(error => {
            console.log(error.response.data);
            ToastrHelper.error(error.response.data)
        })
    }


    const getPosts = (id) => {
        PostService.getPostsFromUser(id).then((response) => {
            console.log(response.data)
            setPosts(response.data)
            setPostsLoaded(true)
        }).catch(error => {
            console.log(error.response.data);
            ToastrHelper.error(error.response.data)
            setPostsLoaded(true)
        })
    }

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
        }).catch(error => {
            console.log(error.response.data);
            ToastrHelper.error(error.response.data)
        })
    }

    const acceptRequest = () => {
        FriendshipService.acceptFriendRequest(requestId).then((response) => {
            window.location.reload()
        }).catch(error => {
            console.log(error.response.data);
            ToastrHelper.error(error.response.data)
        })
    }

    const rejectRequest = () => {
        FriendshipService.rejectFriendRequest(requestId).then((response) => {
            setIsRequestReceived(false)
        }).catch(error => {
            console.log(error.response.data);
            ToastrHelper.error(error.response.data)
        })
    }


    const sendFriendRequest = () => {
        FriendshipService.sendFriendRequest(userInfo.id).then((response) => {
            setIsRequestSent(true)
            ToastrHelper.success("Friend request sent!")
        }).catch(error => {
            console.log(error.response.data);
            ToastrHelper.error(error.response.data)
        })
    }

    return (
        <React.Fragment>
            <div className="row">
                <form>
                    <span id="imgContainer">
                        <h1 id="imePrezime">{userInfo.firstName}  {userInfo.lastName}</h1>
                        <div className="imagePicker">
                            <img id="profile_img" src={process.env.REACT_APP_BASE_URL + '/users/picture/' + userInfo.picture} alt="..." />
                        </div>
                    </span>
                    <div className="grid-container">
                        <div className="grid-item" />
                        <div className="grid-item" />
                        <div className="grid-item">
                            <div className="form-group">
                                <label htmlFor="firstName">First Name</label>
                                <input type="text" value={userInfo.firstName} className="form-control" disabled />
                            </div>
                        </div>
                        <div className="grid-item">
                            <div className="form-group">
                                <label htmlFor="lastName">Last Name</label>
                                <input type="text" value={userInfo.lastName} className="form-control" disabled />
                            </div>
                        </div>
                        <div className="grid-item">
                            <div className="form-group">
                                <label htmlFor="email">Email</label>
                                <input type="text" value={userInfo.email} className="form-control" disabled />
                            </div>
                        </div>
                        <div className="grid-item">
                            <div className="form-group">
                                <label htmlFor="registration">Registration date</label>
                                <input type="text" value={userInfo.registrationDate} className="form-control" disabled />
                            </div>
                        </div>

                        {isRequestSent && (
                            <React.Fragment>
                                <div className="grid-item d-flex justify-content-center align-items-center" style={{ textAlign: "center" }} >
                                    <h4 style={{ color: 'red' }}>Waiting for {userInfo.firstName} to accept friend request...</h4>
                                </div>
                                <div className="grid-item">
                                </div>
                            </React.Fragment>
                        )}

                        {isRequestReceived && (
                            <React.Fragment>
                                <div className="grid-item d-flex justify-content-center align-items-center" >
                                    <h3>{userInfo.firstName} wants to be your friend:</h3>
                                </div>
                                <div className="grid-item d-flex justify-content-center align-items-center" style={{ borderWidth: '2px', borderStyle: 'solid', borderRadius: '20px' }}>
                                    <button type="button" className="btn btnAccept" style={{ width: 160 }} onClick={acceptRequest} >Accept</button>
                                    <button type="button" className="btn btnReject" style={{ width: 160 }} onClick={rejectRequest} >Reject</button>
                                </div>
                            </React.Fragment>
                        )}

                        {!isRequestReceived && !isRequestSent && !isFriend && isRelationshipLoaded && (
                            <React.Fragment>
                                <div className="grid-item d-flex justify-content-center align-items-center " >
                                    <button type="button" className="btn btnAccept" style={{ width: 160 }} onClick={sendFriendRequest} >Add as friend</button>
                                </div>
                                <div className="grid-item ">
                                </div>
                            </React.Fragment>
                        )}
                    </div>
                </form>
            </div>

            {posts.length === 0 && !isFriend && (
                <div className="row" style={{ marginTop: 60 }}>
                    <div className="col-3" />
                    <div className="col-6 mx-auto" style={{ textAlign: "center" }} >
                        <h2>To view user's posts you must be friends!</h2>
                    </div>
                    <div className="col-3" />
                </div>
            )}


            {posts.length === 0 && isFriend && postsLoaded && (
                <div className="row" style={{ marginTop: 60 }}>
                    <div className="col-3" />
                    <div className="col-6 mx-auto" style={{ textAlign: "center" }} >
                        <h2>{userInfo.firstName} hasn't posted anything yet!</h2>
                    </div>
                    <div className="col-3" />
                </div>
            )}

            {isFriend && posts.length > 0 && (
                <div className="row" style={{ marginTop: 40 }}>
                    <div className="col-2" />
                    <div className="col-6 mx-auto" >
                        <h2>{userInfo.firstName}'s posts:</h2>
                    </div>
                    <div className="col-4" />
                </div>
            )}


            {isFriend && posts.map(post => (
                <PostsOtherProfile key={post.id} post={post} onLike={likePost} onDislike={dislikePost} />
            ))}

        </React.Fragment>
    )
}

export default OtherUserPage
