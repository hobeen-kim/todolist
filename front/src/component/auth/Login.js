import AuthContext from '../../store/auth/auth-context';
import {
  Avatar,
  Button,
  CssBaseline,
  TextField,
  FormControl,
  FormHelperText,
  Grid,
  Box,
  Typography,
  Container,
} from '@mui/material/';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import styled from 'styled-components';
import React, { useState, useRef, useContext, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const FormHelperTexts = styled(FormHelperText)`
  width: 100%;
  padding-left: 16px;
  font-weight: 700 !important;
  color: #d32f2f !important;
`;

const Boxs = styled(Box)`
  padding-bottom: 40px !important;
`;

const Login = () => {

  const theme = createTheme();
  const authCtx = useContext(AuthContext);
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);
  const containerRef = useRef(null);

  useEffect(() => {
    if (containerRef.current) {
      containerRef.current.classList.add('loaded');
    }
  }, []);

  useEffect(() => {
    if (authCtx.isLoggedIn) {
        navigate('/', { replace: true });
    }
    }, [authCtx.isLoggedIn, navigate]);

  const submitHandler = async (event) => {

    event.preventDefault();
    const data = new FormData(event.currentTarget);
    const Username = data.get('username');
    const Password = data.get('password');
    
    setIsLoading(true);
    authCtx.login(Username, Password);
    setIsLoading(false);

    if (authCtx.isLoggedIn) {
      navigate("/", { replace: true });
    }
  }


    

    return (
        <ThemeProvider theme={theme}>
        <Container component="main" maxWidth="xs" className='container-loginForm' ref={containerRef} >
          <CssBaseline />
          <Box
            sx={{
              marginTop: 8,
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
            }}
          >
            <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }} />
            <Typography component="h1" variant="h5">
              로그인
            </Typography>
            <Boxs component="form" noValidate onSubmit={submitHandler} sx={{ mt: 3 }}>
              <FormControl component="fieldset" variant="standard">
                <Grid container spacing={2}>
                <Grid item xs={12}>
                    <TextField
                      required
                      autoFocus
                      fullWidth
                      id="username"
                      name="username"
                      label="ID"
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <TextField
                      required
                      fullWidth
                      type="password"
                      id="password"
                      name="password"
                      label="비밀번호 (숫자+영문자+특수문자 8자리 이상)"
                    />
                  </Grid>
                </Grid>
                <Button
                  type="submit"
                  fullWidth
                  variant="contained"
                  sx={{ mt: 3, mb: 2 }}
                  size="large"
                >
                  로그인
                </Button>
                <Button
                  onClick={() => navigate('/')}
                  fullWidth
                  variant="contained"
                  sx={{ mt: 1, mb: 0 }}
                  size="large"
                >
                  취  소
                </Button>
              </FormControl>
            </Boxs>
          </Box>
        </Container>
      </ThemeProvider>
      );
}

export default Login;