import { Modal } from "react-bootstrap"

function UpdatePostModal({ showUpdate, handleUpdatePostClose, newText, setNewText, updatePost }) {
    return (
        <Modal show={showUpdate} onHide={handleUpdatePostClose} centered>
            <Modal.Header closeButton>
                <Modal.Title>Update post</Modal.Title>
            </Modal.Header>

            <form onSubmit={updatePost}>
                <Modal.Body>
                    <div className="form-group shadow-textarea">
                        <textarea className="form-control z-depth-1" maxLength='300' rows="4" placeholder="Write something here..." style={{ backgroundColor: "aliceblue" }}
                            value={newText} onChange={
                                e => setNewText(e.target.value)
                            } required></textarea>
                    </div>
                </Modal.Body>
                <Modal.Footer>
                    <input type='submit' value='Update' className='btnSubmit btn-block' />
                </Modal.Footer>

            </form>
        </Modal>
    )
}

export default UpdatePostModal
