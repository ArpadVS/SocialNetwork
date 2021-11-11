import React, { Fragment } from 'react'
import useAxiosGetUsers from '../../api/hooks/useAxiosGetUsers';
import { Table } from "react-bootstrap"
import { useHistory } from "react-router-dom";

function Users() {

    const { users, error, loadingUsers } = useAxiosGetUsers()
    const history = useHistory()

    const visitPageHandler = (userId) => {
        history.push("/user/" + userId);
    }

    return (
        <Fragment>
            <div className="row justify-content-md-center" style={{ marginTop: 20 }} >
                {loadingUsers ? 
                    null
                 : (
                    <div className="row">
                        <div className="col-2" />
                        <div className="col-8 cardPosts" style={{ marginTop: 70 }}>

                            {!loadingUsers && error && (
                                <h3 style={{ textAlign: "center" }}>Sadly we cannot retrieve the friend requests!</h3>
                            )}
                            {console.log(users)}
                            {!loadingUsers && users.length > 0 && (

                                <Fragment>
                                    <h2 style={{ textAlign: "center" }}>There are {users.length} users! <br /><br /></h2>
                                    <Table striped bordered hover variant="light" style={{ textAlign: "center", verticalAlign: "center" }}>
                                        <tbody>
                                            {
                                                users.map(user => {
                                                    return (
                                                        <tr key={user.requestId}>
                                                            <td >
                                                                <img src={process.env.REACT_APP_BASE_URL + '/users/picture/' + user.picture} alt="..." style={{ width: 50, height: 50 }} />
                                                            </td>
                                                            <td className="align-middle" >{user.firstName} {user.lastName}</td>
                                                            <td className="align-middle">{user.email}</td>
                                                            <td className="align-middle">
                                                                <button className="btn-primary" onClick={(e) => visitPageHandler(user.id)}>
                                                                    View profile
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

export default Users
