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

import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store';
import { authService } from '../services/auth.service';


const LoginPage = () => {

  const navigate = useNavigate();

  const login = useAuthStore(
    (state) => state.login,
  );

  const [email, setEmail] =
    useState('');

  const [password, setPassword] =
    useState('');

  const [loading, setLoading] =
    useState(false);

  const [error, setError] =
    useState('');

  const handleSubmit = async (
    event: FormEvent<HTMLFormElement>,
  ) => {

    event.preventDefault();

    setError('');
    setLoading(true);

    try {

      const response =
        await authService.login({
          email,
          password,
        });

      login(
        response.accessToken,
        response.refreshToken,
        response.user,
      );

      navigate(
        '/dashboard',
        { replace: true },
      );

    } catch (error: any) {

      setError(
        error?.response?.data?.message ||
        error?.message ||
        'Unable to login.',
      );

    } finally {

      setLoading(false);
    }
  };

  return (
    <div className="login-page min-vh-100 d-flex align-items-center">

      <Container>

        <Row className="justify-content-center">

          <Col
            xs={12}
            sm={10}
            md={7}
            lg={5}
            xl={4}
          >

            <Card className="shadow border-0">

              <Card.Body className="p-4 p-md-5">

                <div className="text-center mb-4">

                  <div className="mb-3">
                    <i
                      className="bi bi-database-fill fs-1"
                    />
                  </div>

                  <h2 className="fw-bold">
                    RAG
                  </h2>

                  <p className="text-muted mb-0">
                    Document Intelligence
                  </p>

                </div>

                {error && (
                  <Alert
                    variant="danger"
                    dismissible
                    onClose={() => setError('')}
                  >
                    {error}
                  </Alert>
                )}

                <Form
                  onSubmit={handleSubmit}
                >

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
                    className="mb-4"
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
                      autoComplete="current-password"
                    />

                  </Form.Group>

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

                        Signing in...
                      </>
                    ) : (
                      <>
                        <i className="bi bi-box-arrow-in-right me-2" />

                        Sign In
                      </>
                    )}

                  </Button>

                </Form>

              </Card.Body>

            </Card>

            <div className="text-center mt-3 text-muted small">
              Local RAG Document Search
            </div>

          </Col>

        </Row>

      </Container>

    </div>
  );
};

export default LoginPage;