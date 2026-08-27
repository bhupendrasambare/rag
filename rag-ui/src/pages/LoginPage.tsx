import {
  useState,
  type FormEvent,
} from 'react';

import {
  Alert,
  Button,
  Card,
  Col,
  Container,
  Form,
  Row,
  Spinner,
} from 'react-bootstrap';

import {
  useNavigate,
} from 'react-router-dom';

import {
  useAuthStore,
} from '../store';

import {
  authService,
} from '../services/auth.service';

type AuthMode =
  | 'login'
  | 'register';

const LoginPage = () => {

  const navigate =
    useNavigate();

  const login =
    useAuthStore(
      (state) => state.login,
    );

  const [
    mode,
    setMode,
  ] = useState<AuthMode>('login');

  const [
    firstName,
    setFirstName,
  ] = useState('');

  const [
    lastName,
    setLastName,
  ] = useState('');

  const [
    email,
    setEmail,
  ] = useState('');

  const [
    password,
    setPassword,
  ] = useState('');

  const [
    confirmPassword,
    setConfirmPassword,
  ] = useState('');

  const [
    loading,
    setLoading,
  ] = useState(false);

  const [
    error,
    setError,
  ] = useState('');

  const [
    success,
    setSuccess,
  ] = useState('');

  const resetMessages =
    () => {

      setError('');
      setSuccess('');
    };

  const switchMode =
    (newMode: AuthMode) => {

      resetMessages();

      setMode(newMode);

      setPassword('');
      setConfirmPassword('');
    };

  const handleSubmit =
    async (
      event: FormEvent<HTMLFormElement>,
    ) => {

      event.preventDefault();

      resetMessages();

      if (
        mode === 'register' &&
        password !== confirmPassword
      ) {

        setError(
          'Password and confirm password must match.',
        );

        return;
      }

      setLoading(true);

      try {

        if (mode === 'login') {

          /*
           * Backend:
           *
           * {
           *   success: true,
           *   data: {
           *     accessToken,
           *     refreshToken,
           *     user
           *   }
           * }
           *
           * authService.login()
           * returns the `data` object.
           */

          const response =
            await authService.login({
              email: email.trim(),
              password,
            });

          /*
           * Store expects LoginResponse.
           */

          login(response);

          /*
           * Authentication is now complete.
           */

          navigate(
            '/dashboard',
            {
              replace: true,
            },
          );

          return;
        }

        /*
         * Registration.
         */

        await authService.register({

          firstName:
            firstName.trim(),

          lastName:
            lastName.trim(),

          email:
            email.trim(),

          password,

          confirmPassword,

        });

        setSuccess(
          'Registration successful. You can now sign in.',
        );

        setMode('login');

        setPassword('');
        setConfirmPassword('');

      } catch (error: any) {

        const response =
          error?.response?.data;

        const message =
          response?.message ||
          response?.errorMessage ||
          response?.data?.message ||
          error?.message ||
          (
            mode === 'login'
              ? 'Unable to login.'
              : 'Unable to register.'
          );

        setError(message);

      } finally {

        setLoading(false);
      }
    };

  return (
    <div
      className="
        login-page
        min-vh-100
        d-flex
        align-items-center
      "
    >

      <Container>

        <Row
          className="
            justify-content-center
          "
        >

          <Col
            xs={12}
            sm={10}
            md={8}
            lg={6}
            xl={5}
          >

            <Card
              className="
                shadow
                border-0
                mt-3
              "
            >

              <Card.Body
                className="
                  p-4
                  p-md-5
                "
              >

                <div
                  className="
                    text-center
                    mb-4
                  "
                >

                  <div className="mb-3">

                    <i
                      className="
                        bi
                        bi-database-fill
                        fs-1
                      "
                    />

                  </div>

                  <h2
                    className="
                      fw-bold
                      mb-1
                    "
                  >
                    RAG
                  </h2>

                  <p
                    className="
                      text-muted
                      mb-0
                    "
                  >
                    Document Intelligence
                  </p>

                </div>

                <div
                  className="
                    btn-group
                    w-100
                    mb-4
                  "
                  role="group"
                >

                  <Button
                    type="button"
                    variant={
                      mode === 'login'
                        ? 'dark'
                        : 'outline-dark'
                    }
                    onClick={() =>
                      switchMode('login')
                    }
                  >
                    Sign In
                  </Button>

                  <Button
                    type="button"
                    variant={
                      mode === 'register'
                        ? 'dark'
                        : 'outline-dark'
                    }
                    onClick={() =>
                      switchMode('register')
                    }
                  >
                    Register
                  </Button>

                </div>

                {error && (

                  <Alert
                    variant="danger"
                    dismissible
                    onClose={() =>
                      setError('')
                    }
                  >
                    {error}
                  </Alert>

                )}

                {success && (

                  <Alert
                    variant="success"
                    dismissible
                    onClose={() =>
                      setSuccess('')
                    }
                  >
                    {success}
                  </Alert>

                )}

                <Form
                  onSubmit={handleSubmit}
                >

                  {mode === 'register' && (

                    <Row>

                      <Col md={6}>

                        <Form.Group
                          className="mb-3"
                          controlId="firstName"
                        >

                          <Form.Label>
                            First Name
                          </Form.Label>

                          <Form.Control
                            type="text"
                            placeholder="First name"
                            value={firstName}
                            onChange={(event) =>
                              setFirstName(
                                event.target.value,
                              )
                            }
                            required
                            autoComplete="given-name"
                          />

                        </Form.Group>

                      </Col>

                      <Col md={6}>

                        <Form.Group
                          className="mb-3"
                          controlId="lastName"
                        >

                          <Form.Label>
                            Last Name
                          </Form.Label>

                          <Form.Control
                            type="text"
                            placeholder="Last name"
                            value={lastName}
                            onChange={(event) =>
                              setLastName(
                                event.target.value,
                              )
                            }
                            required
                            autoComplete="family-name"
                          />

                        </Form.Group>

                      </Col>

                    </Row>

                  )}

                  <Form.Group
                    className="mb-3"
                    controlId="email"
                  >

                    <Form.Label>
                      Email
                    </Form.Label>

                    <Form.Control
                      type="email"
                      placeholder="Enter your email"
                      value={email}
                      onChange={(event) =>
                        setEmail(
                          event.target.value,
                        )
                      }
                      required
                      autoComplete="email"
                    />

                  </Form.Group>

                  <Form.Group
                    className="mb-3"
                    controlId="password"
                  >

                    <Form.Label>
                      Password
                    </Form.Label>

                    <Form.Control
                      type="password"
                      placeholder="Enter your password"
                      value={password}
                      onChange={(event) =>
                        setPassword(
                          event.target.value,
                        )
                      }
                      required
                      minLength={8}
                      autoComplete={
                        mode === 'login'
                          ? 'current-password'
                          : 'new-password'
                      }
                    />

                  </Form.Group>

                  {mode === 'register' && (

                    <Form.Group
                      className="mb-4"
                      controlId="confirmPassword"
                    >

                      <Form.Label>
                        Confirm Password
                      </Form.Label>

                      <Form.Control
                        type="password"
                        placeholder="Confirm your password"
                        value={confirmPassword}
                        onChange={(event) =>
                          setConfirmPassword(
                            event.target.value,
                          )
                        }
                        required
                        minLength={8}
                        autoComplete="new-password"
                        isInvalid={
                          confirmPassword.length > 0 &&
                          password !== confirmPassword
                        }
                      />

                      <Form.Control.Feedback
                        type="invalid"
                      >
                        Passwords do not match.
                      </Form.Control.Feedback>

                    </Form.Group>

                  )}

                  <Button
                    type="submit"
                    variant="dark"
                    className="w-100"
                    disabled={loading}
                  >

                    {loading ? (

                      <>
                        <Spinner
                          size="sm"
                          className="me-2"
                        />

                        {mode === 'login'
                          ? 'Signing in...'
                          : 'Creating account...'}
                      </>

                    ) : (

                      <>
                        <i
                          className={
                            mode === 'login'
                              ? 'bi bi-box-arrow-in-right me-2'
                              : 'bi bi-person-plus me-2'
                          }
                        />

                        {mode === 'login'
                          ? 'Sign In'
                          : 'Create Account'}
                      </>

                    )}

                  </Button>

                </Form>

              </Card.Body>

            </Card>

            <div
              className="
                text-center
                mt-3
                text-muted
                small
              "
            >
              Local RAG Document Search
            </div>

          </Col>

        </Row>

      </Container>

    </div>
  );
};

export default LoginPage;