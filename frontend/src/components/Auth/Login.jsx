import React from "react";
import { ToastrHelper } from '../../utils/toastrj';
import { isEmail } from "validator";

import { useState } from 'react'

import AuthService from "../../api/services/AuthService"

function Login(props) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')

  const onSubmit = (e) => {
    e.preventDefault()

    if (!isEmail(email)) {
      ToastrHelper.warn('Email format is not valid!')
      return
    } else if (password.length < 4) {
      ToastrHelper.warn("Password is too short!")
      return
    }

    //console.log({ email, password });

    AuthService.login(email, password).then((response) => {
      //ToastrHelper.success("Login succesful")
      setEmail('')
      setPassword('')
      console.log(response.data);
      if (response.data.accessToken) {
        localStorage.setItem("user", JSON.stringify(response.data));
      }
      props.refresh()
    }).catch(error => {

      console.log(error.response.data);
      ToastrHelper.error(error.response.data)
    })

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

        <div style={{ textAlign: "center" }}>
          <input type='submit' value='Login' className='btn btn-outline-dark form-button' />
        </div>
      </form>
    </div>
  )
}

export default Login
