import {
  Container,
  Nav,
  Navbar,
} from 'react-bootstrap';

import {
  Link,
  Outlet,
  useNavigate,
} from 'react-router-dom';

import { authService } from '../services/auth.service';
import { useAuthStore } from '../store';

const MainLayout = () => {

  const navigate = useNavigate();

  const user = useAuthStore(
    (state) => state.user,
  );

  const logout = useAuthStore(
    (state) => state.logout,
  );

  const handleLogout = async () => {

    try {
      await authService.logout();
    } catch {
      // Logout locally even if server request fails.
    } finally {

      logout();

      navigate(
        '/login',
        { replace: true },
      );
    }
  };

  return (
    <div className="min-vh-100 bg-light">

      <Navbar
        bg="dark"
        variant="dark"
        expand="lg"
      >

        <Container fluid>

          <Navbar.Brand
            as={Link}
            to="/dashboard"
          >
            <i className="bi bi-database me-2" />
            RAG
          </Navbar.Brand>

          <Navbar.Toggle />

          <Navbar.Collapse>

            <Nav className="me-auto">

              <Nav.Link
                as={Link}
                to="/dashboard"
              >
                Dashboard
              </Nav.Link>

              <Nav.Link
                as={Link}
                to="/documents"
              >
                Documents
              </Nav.Link>

              <Nav.Link
                as={Link}
                to="/chat"
              >
                Chat
              </Nav.Link>

              <Nav.Link
                as={Link}
                to="/settings"
              >
                Settings
              </Nav.Link>

            </Nav>

            <div className="d-flex align-items-center gap-3">

              <span className="text-white small">
                {user?.firstName} {user?.lastName}
              </span>

              <button
                type="button"
                className="btn btn-outline-light btn-sm"
                onClick={handleLogout}
              >
                <i className="bi bi-box-arrow-right me-1" />
                Logout
              </button>

            </div>

          </Navbar.Collapse>

        </Container>

      </Navbar>

      <main>
        <Outlet />
      </main>

    </div>
  );
};

export default MainLayout;