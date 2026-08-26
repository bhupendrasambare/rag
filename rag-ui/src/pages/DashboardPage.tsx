import {
  Card,
  Col,
  Container,
  Row,
} from 'react-bootstrap';
import { useAuthStore } from '../store';


const DashboardPage = () => {

  const user = useAuthStore(
    (state) => state.user,
  );

  return (
    <Container fluid className="py-4">

      <Row>

        <Col>

          <h2 className="fw-bold">
            Dashboard
          </h2>

          <p className="text-muted">
            Welcome back, {user?.firstName}.
          </p>

        </Col>

      </Row>

      <Row className="g-4 mt-1">

        <Col
          xs={12}
          md={4}
        >

          <Card className="border-0 shadow-sm h-100">

            <Card.Body>

              <div className="mb-3">
                <i className="bi bi-file-earmark-text fs-2" />
              </div>

              <Card.Title>
                Documents
              </Card.Title>

              <Card.Text className="text-muted">
                Upload and manage your documents.
              </Card.Text>

            </Card.Body>

          </Card>

        </Col>

        <Col
          xs={12}
          md={4}
        >

          <Card className="border-0 shadow-sm h-100">

            <Card.Body>

              <div className="mb-3">
                <i className="bi bi-chat-dots fs-2" />
              </div>

              <Card.Title>
                Chat
              </Card.Title>

              <Card.Text className="text-muted">
                Ask questions about your documents.
              </Card.Text>

            </Card.Body>

          </Card>

        </Col>

        <Col
          xs={12}
          md={4}
        >

          <Card className="border-0 shadow-sm h-100">

            <Card.Body>

              <div className="mb-3">
                <i className="bi bi-person fs-2" />
              </div>

              <Card.Title>
                Profile
              </Card.Title>

              <Card.Text className="text-muted">
                Manage your account information.
              </Card.Text>

            </Card.Body>

          </Card>

        </Col>

      </Row>

    </Container>
  );
};

export default DashboardPage;