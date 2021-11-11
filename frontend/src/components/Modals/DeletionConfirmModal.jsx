import { Modal } from "react-bootstrap"

function DeletionConfirmModal({ showConfirm, handleConfirmClose, text, onClickHandler, id }) {
    return (
        <Modal show={showConfirm} onHide={handleConfirmClose} centered>
            <Modal.Header closeButton>
                <Modal.Title>{text}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <h6 style={{ textAlign: "center", color: "red" }}>This process cannot be undone.</h6>
            </Modal.Body>
            <Modal.Footer>
                <button className="btn btn-secondary" onClick={() => handleConfirmClose()}>Cancel</button>
                <button className="btn btn-danger" onClick={() => onClickHandler(id)}>Delete</button>
            </Modal.Footer>
        </Modal>
    )
}

export default DeletionConfirmModal
