import React, { useState, useEffect } from 'react'
import { ToastrHelper } from '../utils/toastrj';
import { isEmail } from "validator";
import { useHistory } from "react-router-dom";
import AuthService from "../api/services/AuthService"
import UserService from "../api/services/UserService"
import PostService from "../api/services/PostService"

import "./ProfilePage.css";
import UpdateUserModal from '../components/Modals/UpdateUserModal';
import UpdatePostModal from '../components/Modals/UpdatePostModal';
import ViewFriendsModal from '../components/Modals/ViewFriendsModal';
import PostsMyProfile from '../components/Post/PostsMyProfile';
import useAxiosGetFriends from '../api/hooks/userAxiosGetFriends';

function ProfilePage() {
    const [show, setShow] = useState(false);
    const [showFriends, setShowFriends] = useState(false);
    const [email, setEmail] = useState("");
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");

    const [posts, setPosts] = useState([])
    const [postsLoaded, setPostsLoaded] = useState(false)

    const { friends, error, friendsLoaded } = useAxiosGetFriends()

    const [showUpdate, setShowUpdate] = useState(false)
    const [updateId, setUpdateId] = useState(-1)
    const [prevText, setPrevText] = useState("")
    const [newText, setNewText] = useState("")

    const history = useHistory();

    let emptyMe = {
        "firstName": "",
        "lastName": "",
        "email": "",
        "picture": "default.png",
        "registrationDate": "",
    }
    const [me, setMe] = useState(emptyMe)

    // open-close user update modal
    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    // open-close view friends modal
    const handleFriendsClose = () => setShowFriends(false);
    const handleFriendsShow = () => setShowFriends(true);

    // open-close update post modal
    const handleUpdatePostClose = () => {
        setUpdateId(-1)
        setNewText("")
        setPrevText("")
        setShowUpdate(false)
    }
    const handleUpdatePostShow = (id, text) => {
        setUpdateId(id)
        setNewText(text)
        setPrevText(text)
        setShowUpdate(true)
    }

    useEffect(() => {
        whoami()
        getMyPosts()
    }, [])

    const whoami = () => {
        AuthService.whoami().then((response) => {
            console.log(response.data)
            setMe(response.data)
            setEmail(response.data.email)
            setFirstName(response.data.firstName)
            setLastName(response.data.lastName)
        }).catch(error => {
            console.log(error.response.data);
            ToastrHelper.error(error.response.data)
        })
    }

    const getMyPosts = () => {
        PostService.getPostsFromUser(AuthService.getCurrentUser().userId).then((response) => {
            console.log(response.data)
            setPosts(response.data)
            setPostsLoaded(true)
        }).catch(error => {
            console.log(error.response.data);
            ToastrHelper.error(error.response.data)
            setPostsLoaded(true)
        })
    }

    const visitFriendPage = (id) => {
        handleFriendsClose(false)
        history.push("/user/" + id);
    }

    const updatePost = (e) => {
        e.preventDefault()
        // if no change just close modal and reset states
        if (prevText === newText) {
            handleUpdatePostClose()
            return
        }
        PostService.updatePost(updateId, newText).then((response) => {
            //updating view on front if succeeded update on the back
            setPosts(
                posts.map((post) =>
                    post.id === updateId ? { ...post, text: newText } : post
                )
            )
            handleUpdatePostClose()
            ToastrHelper.success("Post updated")
        }).catch(error => {
            console.log(error.response.data);
            ToastrHelper.error(error.response.data)
            handleUpdatePostClose()
        })
    }

    const deletePost = (id) => {
        PostService.deletePost(id).then((response) => {
            //filter all posts which are not the one deleted
            setPosts(posts.filter(p => p.id !== id))
            ToastrHelper.success("Post deleted")
        }).catch(error => {
            console.log(error.response.data);
            ToastrHelper.error(error.response.data)
        })
    }

    const updateUser = (e) => {

        e.preventDefault()

        if (!isEmail(email)) {
            ToastrHelper.warn('Email format is not valid!')
            return
        } else if (firstName.length < 2 || lastName.length < 2) {
            ToastrHelper.warn("Name is too short!")
            return
        }

        UserService.updateUser(email, firstName, lastName).then((response) => {
            let updatedUser = { ...me, firstName: firstName, lastName: lastName, email: email }
            console.log(updatedUser);
            setMe(updatedUser)
            handleClose()
            ToastrHelper.success("Updated user info")

        }).catch(error => {
            console.log(error.response.data);
            setEmail(me.email)
            setFirstName(me.firstName)
            setLastName(me.lastName)
            ToastrHelper.error(error.response.data + "\n Use a different email!")
        })
    }

    return (
        <React.Fragment>
            <div className="row">
                <form>
                    <span id="imgContainer">
                        <h1 id="imePrezime">{me.firstName}  {me.lastName}</h1>
                        <div className="imagePicker">
                            <img id="profile_img" src={process.env.REACT_APP_BASE_URL + '/users/picture/' + me.picture} alt="..." />
                        </div>
                    </span>
                    <div className="grid-container">
                        <div className="grid-item" />
                        <div className="grid-item" />
                        <div className="grid-item">
                            <div className="form-group">
                                <label htmlFor="firstName">First Name</label>
                                <input type="text" value={me.firstName} className="form-control" disabled />
                            </div>
                        </div>
                        <div className="grid-item">
                            <div className="form-group">
                                <label htmlFor="lastName">Last Name</label>
                                <input type="text" value={me.lastName} className="form-control" disabled />
                            </div>
                        </div>
                        <div className="grid-item">
                            <div className="form-group">
                                <label htmlFor="email">Email</label>
                                <input type="text" value={me.email} className="form-control" disabled />
                            </div>
                        </div>
                        <div className="grid-item">
                            <div className="form-group">
                                <label htmlFor="registration">Registration date</label>
                                <input type="text" value={me.registrationDate} className="form-control" disabled />
                            </div>
                        </div>

                        <div className="grid-item d-flex justify-content-center align-items-center" >
                            <button type="button" className="btn btn-outline-dark" style={{ width: 160 }} onClick={handleFriendsShow}>Friend list</button>
                        </div>
                        <div className="grid-item d-flex justify-content-center align-items-center">
                            <button type="button" className="btn btn-outline-dark" style={{ width: 160 }} onClick={handleShow}>Update Profile</button>
                        </div>

                        <input type="hidden" id="userId" defaultValue={25} />
                    </div>
                </form>

                <UpdateUserModal show={show} handleClose={handleClose} updateUser={updateUser} email={email} firstName={firstName} lastName={lastName}
                    setEmail={setEmail} setFirstName={setFirstName} setLastName={setLastName} />

                <UpdatePostModal showUpdate={showUpdate} handleUpdatePostClose={handleUpdatePostClose} newText={newText} setNewText={setNewText} updatePost={updatePost} />

                <ViewFriendsModal showFriends={showFriends} handleFriendsClose={handleFriendsClose} friends={friends} friendsLoaded={friendsLoaded} visitFriendPage={visitFriendPage} error={error} />

            </div>

            {posts.length > 0 && (
                <div className="row" style={{ marginTop: 40 }}>
                    <div className="col-2" />
                    <div className="col-6 " >
                        <h2>My posts:</h2>
                    </div>
                    <div className="col-4" />
                </div>
            )}

            {posts.length === 0 && postsLoaded && (
                <div className="row" style={{ marginTop: 40 }}>
                    <div className="col-3" />
                    <div className="col-6 mx-auto" style={{ textAlign: "center" }} >
                        <h2>You have not posted anything yet!</h2>
                    </div>
                    <div className="col-3" />
                </div>
            )}

            {posts.map(post => (
                <PostsMyProfile key={post.id} post={post} handleUpdatePostShow={handleUpdatePostShow} deletePost={deletePost} />
            ))}

        </React.Fragment>
    )
}

export default ProfilePage
