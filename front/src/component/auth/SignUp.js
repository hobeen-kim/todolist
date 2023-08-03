import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
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
import { useContext } from 'react';
import AuthContext from '../../store/auth/auth-context';
import {Validation} from '../../store/validation/validation';

// mui의 css 우선순위가 높기때문에 important를 설정 - 실무하다 보면 종종 발생 우선순위 문제
const FormHelperTexts = styled(FormHelperText)`
  width: 100%;
  padding-left: 16px;
  font-weight: 700 !important;
  color: #d32f2f !important;
`;

const Boxs = styled(Box)`
  padding-bottom: 40px !important;
`;

const SignUp = () => {
  
  const theme = createTheme();
  const authCtx = useContext(AuthContext);
  const validation = useContext(Validation);
  const [usernameError, setUsernameError] = useState('');
  const [emailError, setEmailError] = useState('');
  const [passwordState, setPasswordState] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [nameError, setNameError] = useState('');
  const [nickNameError, setNickNameError] = useState('');
  const navigate = useNavigate();
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

  const onhandlePost = async (data) => {
    const { username, email, name, password, nickName } = data;

    authCtx.signup(username, email, password, name, nickName);
    navigate('/login');

  };

  const handleSubmit = (event) => {
    event.preventDefault();

    const data = new FormData(event.currentTarget);
    const joinData = {
      username: data.get('username'),
      email: data.get('email'),
      name: data.get('name'),
      password: data.get('password'),
      rePassword: data.get('rePassword'),
      nickName: data.get('nickName'),
    };
    const { username, email, name, password, rePassword, nickName } = joinData;
    // 아이디 유효성 체크
    const usernameCheck = validation.usernameValidator(username);
    setUsernameError(usernameCheck);

    // 이메일 유효성 체크
    const emailCheck = validation.emailValidator(email);
    setEmailError(emailCheck);

    // 비밀번호 유효성 체크
    const passwordCheck = validation.passwordValidator(password);
    setPasswordState(passwordCheck);

    // 비밀번호 같은지 체크
    if (password !== rePassword) setPasswordError('비밀번호가 일치하지 않습니다.');
    setPasswordError('');

    // 이름 유효성 검사
    const nameCheck = validation.nameValidator(name, 1, 20);
    setNameError(nameCheck);
    

    //닉네임 유효성 검사
    const nickNameCheck = validation.nicknameValidator(nickName);
    setNickNameError(nickNameCheck);
  
    if (
      !usernameCheck && 
      !emailCheck && 
      !passwordCheck && 
      password === rePassword && 
      !nameCheck && 
      !nickNameCheck
      ) {
        onhandlePost(joinData);
      }
  };

  return (
    <div style={{height:"1000px"}}>
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
            회원가입
          </Typography>
          <Boxs component="form" noValidate onSubmit={handleSubmit} sx={{ mt: 3 }}>
            <FormControl component="fieldset" variant="standard">
              <Grid container spacing={2}>
              <Grid item xs={12}>
                  <TextField
                    required
                    autoFocus
                    fullWidth
                    id="username"
                    name="username"
                    label="ID (4글자 이상 20글자 이하)"
                    error={usernameError !== '' || false}
                  />
                </Grid>
                <FormHelperTexts>{usernameError}</FormHelperTexts>
                <Grid item xs={12}>
                  <TextField
                    required
                    autoFocus
                    fullWidth
                    type="email"
                    id="email"
                    name="email"
                    label="이메일 주소"
                    error={emailError !== '' || false}
                  />
                </Grid>
                <FormHelperTexts>{emailError}</FormHelperTexts>
                <Grid item xs={12}>
                  <TextField
                    required
                    fullWidth
                    type="password"
                    id="password"
                    name="password"
                    label="비밀번호 (숫자+영문자+특수문자 8자리 이상)"
                    error={passwordState !== '' || false}
                  />
                </Grid>
                <FormHelperTexts>{passwordState}</FormHelperTexts>
                <Grid item xs={12}>
                  <TextField
                    required
                    fullWidth
                    type="password"
                    id="rePassword"
                    name="rePassword"
                    label="비밀번호 재입력"
                    error={passwordError !== '' || false}
                  />
                </Grid>
                <FormHelperTexts>{passwordError}</FormHelperTexts>
                <Grid item xs={12}>
                  <TextField
                    required
                    fullWidth
                    id="name"
                    name="name"
                    label="이름"
                    error={nameError !== '' || false}
                  />
                </Grid>
                <FormHelperTexts>{nameError}</FormHelperTexts>
                <Grid item xs={12}>
                  <TextField
                    required
                    fullWidth
                    id="nickName"
                    name="nickName"
                    label="닉네임 (1글자 이상 20글자 이하)"
                    error={nickNameError !== '' || false}
                  />
                </Grid>
                <FormHelperTexts>{nickNameError}</FormHelperTexts>
              </Grid>
              <Button
                type="submit"
                fullWidth
                variant="contained"
                sx={{ mt: 3, mb: 2 }}
                size="large"
              >
                회원가입
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
    </div>
  );
};

export default SignUp;