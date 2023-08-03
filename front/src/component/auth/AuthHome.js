import React, { useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Avatar,
  Button,
  CssBaseline,
  Box,
  Typography,
  Container,
} from '@mui/material/';
import { createTheme, ThemeProvider } from '@mui/material/styles';

const AuthHome = () => {
  
  const theme = createTheme();
  const navigate = useNavigate();
  const containerRef = useRef(null);

  useEffect(() => {
    if (containerRef.current) {
      containerRef.current.classList.add('loaded');
    }
  }, []);

  return (
    <ThemeProvider theme={theme} style={{backgroundColor:'rgb(255,0,0)'}}>
      <Container component="main" maxWidth="xs" className='container-loginForm' ref={containerRef}>
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
            환영합니다!
          </Typography>
              <Button
                onClick={() => navigate('/login')}
                fullWidth
                variant="contained"
                sx={{ mt: 3, mb: 2 }}
                size="large"
              >
                로그인
              </Button>
              <Button
                onClick={() => navigate('/signUp')}
                fullWidth
                variant="contained"
                sx={{ mt: 1, mb: 2 }}
                size="large"
              >
                회원가입
              </Button>
        </Box>
      </Container>
    </ThemeProvider>
  );
};

export default AuthHome;