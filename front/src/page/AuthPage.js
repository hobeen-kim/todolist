import '../public/css/startingPage.css';
import { Fragment } from "react";
import { Routes, Route } from "react-router-dom";
import AuthHome from "../component/auth/AuthHome";
import Login from "../component/auth/Login";
import SignUp from "../component/auth/SignUp";
const AuthPage = () => {

  return (
    <Fragment>
        <Routes>
            <Route path="/" element={<AuthHome />} />
            <Route path="/login" element={<Login />} />
            <Route path="/signUp" element={<SignUp />} />
        </Routes>
    </Fragment>
  );
};

export default AuthPage;