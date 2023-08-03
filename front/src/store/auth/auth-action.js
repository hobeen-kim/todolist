import {GET, POST} from "./fetch-action";

const createTokenHeader = (accessToken) => {
    return {
      withCredentials: true,
      headers: {
        'Authorization': 'Bearer ' + accessToken
      }
    }
  }

const calculateRemainingTime = (expirationTime) => {
    const currentTime = new Date().getTime();
    const adjExpirationTime = new Date(expirationTime).getTime();
    const remainingDuration = adjExpirationTime - currentTime;
    return remainingDuration;
};

export const retrieveStoredToken = () => {

  if(document.cookie === '') return null;

  const cookieArray = document.cookie.split('; ');

  const accessTokenCookie = cookieArray.find(cookie => cookie.startsWith('accessToken'));

  if(!accessTokenCookie) return null;

  const accessToken = accessTokenCookie.split('=')[1];

  return accessToken;
}

export const setTokenHandler = (accessToken, refreshToken) => {

    document.cookie = `accessToken=${accessToken}; path=/; domain=localhost; samesite=none; secure`;
    document.cookie = `refreshToken=${refreshToken}; path=/; domain=localhost; samesite=none; secure`;
}

export const signupActionHandler = (name, username, password, email) => {
    const URL = '/v1/api/members'
    const signupObject = {name, username, password, email};
    
    const response = POST(URL, signupObject, {});
    return response;
};

export const loginActionHandler = (username, password) => {
    const URL = '/v1/api/auth/login';
    const loginObject = { username, password };
    const loginHeader = {
      withCredentials: true
    }
    const response = POST(URL, loginObject, loginHeader);
  
    return response;
};
  
export const getUserActionHandler = (accessToken) => {
  const URL = '/v1/api/members/my-info'
  const response = GET(URL, createTokenHeader(accessToken));
  
  return response;
}

  export const userHandler = (userObj) => {
    const obj = {
      "memberId": userObj.memberId, 
      "email": userObj.email, 
      "name": userObj.name, 
      "nickname": userObj.nickname, 
      "authority": userObj.authority
    }
  
    localStorage.setItem('userObj', JSON.stringify(obj));
  }
