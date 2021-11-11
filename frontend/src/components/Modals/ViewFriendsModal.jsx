import { Modal, Table } from "react-bootstrap"

function ViewFriendsModal({ showFriends, handleFriendsClose, friends, friendsLoaded, visitFriendPage, error }) {
    return (
        <Modal show={showFriends} onHide={handleFriendsClose} centered dialogClassName='my-modal' >
            <Modal.Header closeButton>
                <Modal.Title>You have {friends.length} friends!</Modal.Title>
            </Modal.Header>

            <Modal.Body>
                {friends.length === 0 && friendsLoaded && (
                    <h2>Add friends to see their posts!</h2>
                )}

                {error && friendsLoaded && (
                    <h2>An error occured while getting data!</h2>
                )}
                {friends.length > 0 && (
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
                            {friends.map(friend => (
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
                <button className="btn" onClick={handleFriendsClose}>
                    Close
                </button>
            </Modal.Footer>

        </Modal>
    )
}

export default ViewFriendsModal
