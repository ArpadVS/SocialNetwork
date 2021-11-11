import { Modal } from 'react-bootstrap';
import { useState } from 'react'
import UserService from '../../api/services/UserService'
import { ToastrHelper } from '../../utils/toastrj';

function UpdateUserModal({ show, handleClose, updateUser, email, firstName, lastName, setEmail, setFirstName, setLastName }) {

    const [selectedFile, setSelectedFile] = useState(null)

    const fileSelectedHandler = (e) => {
        setSelectedFile(e.target.files[0])
    }

    const fileUploadHandler = (e) => {
        if (!selectedFile) {
            ToastrHelper.warn("Please select an image first!")
            return
        }
        const formData = new FormData()
        formData.append("picture", selectedFile, selectedFile.name)

        console.log(selectedFile);
        UserService.uploadPicture(formData).then((response) => {
            ToastrHelper.success("Profile picture updated. Reload page to see the change.")

        }).catch(error => {
            console.log(error.response.data);
            ToastrHelper.error(error.response.data)
        })
    }

    return (
        <Modal show={show} onHide={handleClose} centered>
            <Modal.Header closeButton>
                <Modal.Title>Update user info</Modal.Title>
            </Modal.Header>

            <Modal.Body>

                <form id="updateForm" className='update-post-form' onSubmit={updateUser}>
                    <div className='form-control'>
                        <label>Email</label>
                        <input type='email' required placeholder='Enter email' value={email} onChange={
                            e => setEmail(e.target.value)
                        } />
                    </div>

                    <div className='form-control'>
                        <label>First name</label>
                        <input type='text' required placeholder='Enter first name' value={firstName} onChange={
                            e => setFirstName(e.target.value)
                        } />
                    </div>

                    <div className='form-control'>
                        <label>Last name</label>
                        <input type='text' required placeholder='Enter last name' value={lastName} onChange={
                            e => setLastName(e.target.value)
                        } />
                    </div>

                </form>

                <div className='form-control'>
                    <label>Update profile picture
                    </label>
                    <input type="file" accept="image/*" onChange={e => fileSelectedHandler(e)} name="picture" id="form-control-image" />
                    <button className="btn-primary" onClick={fileUploadHandler} >Upload</button>
                </div>
            </Modal.Body>
            <Modal.Footer>
                <input type='submit' value='Post' form="updateForm" className='btnSubmit btn-block' />
            </Modal.Footer>

        </Modal>
    )
}

export default UpdateUserModal
