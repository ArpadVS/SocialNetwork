import { Modal } from 'react-bootstrap';

function WritePostModal({ show, text, handleClose, onSubmit, setText }) {
    return (
        <Modal show={show} onHide={handleClose} centered>
            <Modal.Header closeButton>
                <Modal.Title>Write a new post!</Modal.Title>
            </Modal.Header>

            <form className='create-post-form' onSubmit={onSubmit}>
                <Modal.Body>
                    <div className="form-group shadow-textarea">
                        <textarea className="form-control z-depth-1" maxLength='300' rows="4" placeholder="Write something here..." style={{ backgroundColor: "aliceblue" }}
                            value={text} onChange={
                                e => setText(e.target.value)
                            } required></textarea>
                    </div>
                </Modal.Body>
                <Modal.Footer>
                    <input type='submit' value='Post' className='btnSubmit btn-block' />
                </Modal.Footer>

            </form>
        </Modal>
    )
}

export default WritePostModal
