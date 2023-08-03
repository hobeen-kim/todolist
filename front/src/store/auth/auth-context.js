import React, { useState,createContext, useEffect, useCallback } from "react";
import * as authAction from './auth-action'; 

export const AuthContext = createContext({
  userObj: {username: '', email: '', name: '', authority: ''},
  isLoggedIn: false,
  signup: (username, email, password, name) =>  {},
  login: (username, password) => {},
  logout: () => {},
  getUser: () => {}
});

export const AuthContextProvider = ({children}) => {

  const [userObj, setUserObj] = useState([]);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  const signupHandler = (name, username, password, email) => {
    const response = authAction.signupActionHandler(name, username, password, email);
    response.then((result) => {
      if (result !== null) {
        window.alert("회원가입이 완료되었습니다. 로그인해주세요.")
      }
    }).catch((err) => {
      window.alert('에러가 발생했습니다. 다시 시도해주세요.');
    });
  };

  const loginHandler = (username, password) => {

    const data = authAction.loginActionHandler(username, password);
    data.then((result) => {

      if (result !== null && result.status === 200) {
        setIsLoggedIn(true);

        const accessHeader = result.headers['authorization'];
        if (accessHeader && accessHeader.startsWith('Bearer ')){
          const accessToken = accessHeader.slice(7);
          authAction.setTokenHandler(accessToken, result.headers['refresh']);
          const getData = authAction.getUserActionHandler(accessToken);
          getData.then((result) => {
            if (result !== null) {
              const userData = result.data;
              setUserObj(userData);
            }
          })
        }
      }
    }) 
  };

  const getUserHandler = () => {
    const token = authAction.retrieveStoredToken();
    const data = authAction.getUserActionHandler(token);
    data.then((result) => {
      if (result !== null && result.data.code === 200) {
        const userData = result.data;
        setUserObj(userData);
      }
    })
  }


  const contextValue ={
    userObj,
    isLoggedIn,
    signup: signupHandler,
    login: loginHandler,
    getUser: getUserHandler
    }
  
  return(
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  )
}

export default AuthContext;