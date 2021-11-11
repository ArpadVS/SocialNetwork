import React, { Fragment } from 'react'
import useAxiosFriendRequests from '../../api/hooks/useAxiosFriendRequests'
import { Table } from "react-bootstrap"
import { ToastrHelper } from '../../utils/toastrj';
import { useHistory } from "react-router-dom";
import FriendshipService from '../../api/services/FriendshipService';

function FriendRequests() {
    const { friendRequests, error, loadingRequests } = useAxiosFriendRequests()
    const history = useHistory()

    const visitPageHandler = (userId) => {
        history.push("/user/" + userId);
    }

    const acceptRequestHandler = (requestId) => {
        FriendshipService.acceptFriendRequest(requestId).then((response) => {
            window.location.reload()
        }).catch(error => {
            console.log(error.response.data);
            ToastrHelper.error(error.response.data)
        })
    }

    const rejectRequestHandler = (requestId) => {
        FriendshipService.rejectFriendRequest(requestId).then((response) => {
            window.location.reload()
        }).catch(error => {
            console.log(error.response.data);
            ToastrHelper.error(error.response.data)
        })
    }

    return (
        <Fragment>
            <div className="row justify-content-md-center" style={{ marginTop: 20 }} >
                {loadingRequests ? (
                    <div></div>
                ) : (
                    <div className="row">
                        <div className="col-2" />
                        <div className="col-8 cardPosts" style={{ marginTop: 200 }}>

                            {!loadingRequests && error && (
                                <h3 style={{ textAlign: "center" }}>Sadly we cannot retrieve the friend requests!</h3>
                            )}
                            {!loadingRequests && friendRequests.length === 0 && (
                                <h3 style={{ textAlign: "center" }}>You haven't got any friend requests!</h3>
                            )}

                            {!loadingRequests && friendRequests.length > 0 && (
                                <Fragment>
                                    <h2>You have {friendRequests.length} friend requests! <br /><br /></h2>
                                    <Table striped bordered hover variant="dark" style={{ textAlign: "center", verticalAlign: "center" }}>
                                        <thead>

                                        </thead>
                                        <tbody>
                                            {
                                                friendRequests.map(req => {
                                                    return (
                                                        <tr key={req.requestId}>
                                                            <td >
                                                                <img src={process.env.REACT_APP_BASE_URL + '/users/picture/' + req.picture} alt="..." style={{ width: 50, height: 50 }} />
                                                            </td>
                                                            <td className="align-middle" >{req.firstName} {req.lastName}</td>
                                                            <td className="align-middle">{req.email}</td>
                                                            <td className="align-middle">
                                                                <button className="btn-primary" onClick={(e) => visitPageHandler(req.senderId)}>
                                                                    View profile
                                                                </button>
                                                            </td>
                                                            <td className="align-middle">
                                                                <button className="btn-success" onClick={(e) => acceptRequestHandler(req.requestId)}>
                                                                    Accept
                                                                </button>
                                                            </td>
                                                            <td className="align-middle">
                                                                <button className="btn-danger" onClick={(e) => rejectRequestHandler(req.requestId)}>
                                                                    Reject
                                                                </button>
                                                            </td>
                                                        </tr>
                                                    )
                                                })
                                            }
                                        </tbody>
                                    </Table>
                                </Fragment>
                            )}

                        </div>
                        <div className="col-2" />
                    </div>
                )}
            </div>
        </Fragment>
    )
}

export default FriendRequests
