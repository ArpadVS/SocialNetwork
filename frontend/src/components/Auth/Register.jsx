import React from "react";
import { ToastrHelper } from '../../utils/toastrj';
import { isEmail } from "validator";
import { useHistory } from "react-router-dom";

import { useState } from 'react'

import AuthService from "../../api/services/AuthService"

function Register() {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [firstName, setFirstName] = useState('')
    const [lastName, setLastName] = useState('')

    const history = useHistory();

    const onSubmit = (e) => {
        e.preventDefault()

        if (!isEmail(email)) {
            ToastrHelper.warn('Email format is not valid!')
            return
        } else if (password.length < 4) {
            ToastrHelper.warn("Password is too short!")
            return
        } else if (firstName.length < 2 || lastName.length < 2) {
            ToastrHelper.warn("Name is too short!")
            return
        }

        console.log({ email, password, firstName, lastName });


        AuthService.register(email, password, firstName, lastName).then((response) => {
            ToastrHelper.success("Registration successful!.\n You can now log in.")
            console.log(response.data);
            history.push("/login");
        }).catch(error => {
            console.log(error.response.data);
            ToastrHelper.error(error.response.data + "\n Use a different email!")
        })

        setEmail('')
        setPassword('')
        setFirstName('')
        setLastName('')
    }

    return (

        <div className="card card-container">
            <img
                src="//ssl.gstatic.com/accounts/ui/avatar_2x.png"
                alt="profile-img"
                className="profile-img-card"
            />
            <form className='register-form' onSubmit={onSubmit}>
                <div className='form-control'>
                    <label>Email</label>
                    <input type='email' required placeholder='Enter email' value={email} onChange={
                        e => setEmail(e.target.value)
                    } />
                </div>

                <div className='form-control'>
                    <label>Password</label>
                    <input type='password' required placeholder='Enter password' value={password} onChange={
                        e => setPassword(e.target.value)
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

                <div style={{ textAlign: "center" }}>
                    <input type='submit' value='Register' className='btn btn-outline-dark form-button' />
                </div>

            </form>
        </div>
    )
}
export default Register;
