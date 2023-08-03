import './App.css';
import Main from './page/Main';
import { Routes, Route } from 'react-router-dom';
import { useEffect, useContext } from 'react';
import AuthContext from './store/auth/auth-context';
import AuthPage from './page/AuthPage';



function App() {

  const authCtx = useContext(AuthContext);

  useEffect(() => {
    if(authCtx.isLoggedIn){
      authCtx.getUser();
    }
  }, [authCtx.isLoggedIn]);


  return (
    <>
    <Routes>
      {/* <Route path="/*" element={authCtx.isLoggedIn ? <Main /> : <AuthPage/>} /> */}
      <Route path="/*" element={ <Main /> } />
    </Routes>
    </>
  );
}

export default App;
