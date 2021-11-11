import { ToastContainer } from 'react-toastify';
import { Switch, Route, Link, useHistory } from "react-router-dom";
import 'react-toastify/dist/ReactToastify.css';
import { ToastrHelper } from './utils/toastrj';
import { useEffect, useState } from 'react'
import { Modal, Table } from 'react-bootstrap';

import 'bootstrap/dist/css/bootstrap.min.css';
import "./App.css";

import Login from "./components/Auth/Login"
import Register from './components/Auth/Register';
import PrivateRoute from './components/Auth/PrivateRoute';
import Posts from './components/Post/Posts';
import MainPage from './pages/MainPage';
import ProfilePage from './pages/ProfilePage';
import OtherUserPage from './pages/OtherUserPage';

import AuthService from "./api/services/AuthService"
import UserService from "./api/services/UserService"
import Users from './components/User/Users';
import FriendRequests from './components/FriendRequest/FriendRequests';


function App() {
  const [currentUser, setCurrentUser] = useState(null)
  const [searchTerm, setSearchTerm] = useState("")


  const [usersSearched, setUsersSearched] = useState([])
  const [showResult, setShowResult] = useState(false)
  const [resultLoaded, setResultLoaded] = useState(false)

  const history = useHistory();

  useEffect(() => {
    const user = setCurrentUser(AuthService.getCurrentUser())
    if (user) {
      setCurrentUser(user)
    }
  }, [])


  const handleClose = () => {
    setShowResult(false)
    setResultLoaded(false)
    setUsersSearched([])
    setSearchTerm("")
  }
  const handleOpen = () => setShowResult(true);

  const visitFriendPage = (id) => {
    setShowResult(false)
    history.push("/user/" + id);
    window.location.reload()
  }


  const updateAfterLogin = () => {
    setCurrentUser(AuthService.getCurrentUser())
    history.push("/home");
    window.location.reload()

  }

  const searchUsers = (e) => {
    e.preventDefault()
    UserService.searchUsers(searchTerm.trim()).then((response) => {
      console.log(response.data);
      setResultLoaded(true)
      setUsersSearched(response.data)
      handleOpen()
    }).catch(error => {
      console.log(error.response.data);
      ToastrHelper.error(error.response.data)
      setResultLoaded(true)
    })
  }

  const isLoggedIn = () => {
    const u = AuthService.getCurrentUser()
    return u ? true : false
  }

  const logOut = () => {
    AuthService.logout();
  };

  return (
    <div>
      <nav className="navbar navbar-expand-sm navbar-dark bg-dark" >

        <div className="container-fluid ">
          <Link to={"/home"} className="navbar-brand mb-0 h1">
            Social Network
          </Link>

          {currentUser && (
            <div className="navbar-nav">
              <li className="nav-item">
                <Link to={"/home"} className="nav-link">
                  Home
                </Link>
              </li>
              <li className="nav-item">
                <Link to={"/users"} className="nav-link">
                  Users
                </Link>
              </li>
              <li className="nav-item">
                <Link to={"/requests"} className="nav-link">
                  Friend requests
                </Link>
              </li>
            </div>)}
          {currentUser && (
            <div className="navbar-nav">

              <form className="d-flex" onSubmit={searchUsers}>
                <input type="search" placeholder="Search users" aria-label="Search" value={searchTerm} onChange={e => setSearchTerm(e.target.value)}
                  style={{ marginRight: 5 }} required />
                <button className="btn-primary" type="submit">Search</button>
              </form>
            </div>

          )}

          {currentUser ? (
            <div className="navbar-nav">
              <li className="nav-item">
                <Link to={"/profile"} className="nav-link">
                  {currentUser.firstName}
                </Link>
              </li>
              <li className="nav-item">
                <a href="/login" className="nav-link" onClick={logOut}>
                  Log Out
                </a>
              </li>
              <li className="nav-item" style={{ width: 30 }} />
            </div>
          ) : (
            <div className="navbar-nav ml-auto">
              <li className="nav-item">
                <Link to={"/login"} className="nav-link">
                  Login
                </Link>
              </li>

              <li className="nav-item">
                <Link to={"/register"} className="nav-link">
                  Register
                </Link>
              </li>
              <li className="nav-item" style={{ width: 30 }} />
            </div>
          )}
        </div>
      </nav>

      <div className="container">
        <Switch>
          <Route exact path="/login" > <Login refresh={updateAfterLogin} /> </Route>
          <Route exact path="/register" component={Register} />
          <PrivateRoute authed={isLoggedIn()} path='/home' component={MainPage} />
          <PrivateRoute authed={isLoggedIn()} path='/profile' component={ProfilePage} />
          <PrivateRoute authed={isLoggedIn()} exact path='/user' component={MainPage} />
          <PrivateRoute authed={isLoggedIn()} path='/user' component={OtherUserPage} />
          <PrivateRoute authed={isLoggedIn()} exact path='/users' component={Users} />
          <PrivateRoute authed={isLoggedIn()} path='/requests' component={FriendRequests} />
          <PrivateRoute authed={isLoggedIn()} path='/posts' component={Posts} />
          <PrivateRoute authed={isLoggedIn()} exact path='/' component={MainPage} />
        </Switch>
      </div>

      <Modal show={showResult} onHide={handleClose} dialogClassName='my-modal' centered>
        <Modal.Header closeButton>
          <Modal.Title>Search result for term '{searchTerm}'</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          {usersSearched.length === 0 && resultLoaded && (
            <h2>No users found!</h2>
          )}
          {usersSearched.length > 0 && (
            <Table striped bordered hover variant="dark" style={{ textAlign: "center" }}>
              <thead>
                <tr>
                  <th>First Name</th>
                  <th>Last Name</th>
                  <th>Email</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {usersSearched.map(friend => (
                  <tr key={'friend_' + friend.id}>
                    <td >{friend.firstName}</td>
                    <td >{friend.lastName}</td>
                    <td >{friend.email}</td>
                    <td >
                      <button className="btn-primary" onClick={() => visitFriendPage(friend.id)}>
                        Visit profile
                      </button>
                    </td>
                  </tr>
                )
                )}
              </tbody>
            </Table>
          )}
        </Modal.Body>
        <Modal.Footer>
          <button className="btn" onClick={handleClose}>
            Close
          </button>
        </Modal.Footer>

      </Modal>

      <ToastContainer
        position="bottom-right"
        autoClose={3500}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
      />
    </div>
  );
}

export default App;
