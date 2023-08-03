import axios from 'axios';
import { TestURI } from '../../utility/uri';

const uri = TestURI;

const fetchAuth = async (fetchData) => {
  const method = fetchData.method;
  const url = fetchData.url;
  const data = fetchData.data;
  const header = fetchData.header;

  try {
    const response =
      (method === 'get' && (await axios.get(uri + url, header))) ||
      (method === 'post' && (await axios.post(uri + url, data, header))) ||
      (method === 'put' && (await axios.put(uri + url, data, header))) ||
      (method === 'patch' && (await axios.patch(uri + url, data, header))) ||
      (method === 'delete' && (await axios.delete(uri + url, header)));

    if(response.data.message==='만료된 토큰입니다.'){

      const refreshTokenUrl = '/auth/refresh';
      const refreshTokenHeader = {
        withCredentials: 'include',
        headers: {
          'X-Expired-Access-Token': header.headers.Authorization
        }
      }
      const refreshResponse = await axios.get(uri + refreshTokenUrl, refreshTokenHeader)

      if(refreshResponse.status===401){
        alert('로그인이 만료되었습니다. 다시 로그인해주세요.');
        return null;
      }else{
        localStorage.setItem('accessToken', refreshResponse.data.accessToken);
        localStorage.setItem('accessTokenExpirationTime', String(refreshResponse.data.accessTokenExpirationTime));
        const accessToken = refreshResponse.data.accessToken;
        const accessTokenHeader = {
          headers: {
            'Authorization': 'Bearer ' + accessToken
          }
        }
        const response =
          (method === 'get' && (await axios.get(uri + url, accessTokenHeader))) ||
          (method === 'post' && (await axios.post(uri + url, data, accessTokenHeader))) ||
          (method === 'put' && (await axios.put(uri + url, data, accessTokenHeader))) ||
          (method === 'patch' && (await axios.patch(uri + url, data, accessTokenHeader))) ||
          (method === 'delete' && (await axios.delete(uri + url, accessTokenHeader)));
        return response;
      }
    }

    if (!response) {
      alert('false!');
      return null;
    }

    return response;
  } catch (err) {
    
    if (axios.isAxiosError(err)) {
      const serverError = err;
      if (serverError && serverError.response) {
        return null;
      }
    }
    return null;
  }
};

const GET = (url, header) => {
  const response = fetchAuth({ method: 'get', url, header });
  return response;
};

const POST = (url, data, header) => {
  const response = fetchAuth({ method: 'post', url, data, header });
  return response;
};

const PUT = async (url, data, header) => {
  const response = fetchAuth({ method: 'put', url, data, header });
  return response;
};

const PATCH = async (url, data, header) => {
  const response = fetchAuth({ method: 'patch', url, data, header });
  return response;
};

const DELETE = async (url, header) => {
  const response = fetchAuth({ method: 'delete', url, header });
  return response;
};



export { GET, POST, PUT, PATCH, DELETE };
