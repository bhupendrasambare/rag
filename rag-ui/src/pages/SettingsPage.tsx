import {
  useEffect,
  useState,
} from 'react';

import {
  Alert,
  Button,
  Card,
  Col,
  Form,
  Row,
  Spinner,
  Tab,
  Tabs,
} from 'react-bootstrap';

import {
  useAuthStore,
} from '../store';

import {
  userService,
} from '../services/user.service';

import {
  authService,
} from '../services/auth.service';

import type {
  ChangePasswordRequest,
  UpdateProfileRequest,
  UserProfileResponse,
} from '../types/auth';


type SettingsTab =
  | 'profile'
  | 'edit-profile'
  | 'security';


const SettingsPage = () => {

  const user =
    useAuthStore(
      (state) => state.user,
    );

  const updateUser =
    useAuthStore(
      (state) => state.updateUser,
    );


  const [
    profile,
    setProfile,
  ] = useState<UserProfileResponse | null>(null);

  const [
    loadingProfile,
    setLoadingProfile,
  ] = useState(true);

  const [
    savingProfile,
    setSavingProfile,
  ] = useState(false);

  const [
    changingPassword,
    setChangingPassword,
  ] = useState(false);

  const [
    error,
    setError,
  ] = useState('');

  const [
    success,
    setSuccess,
  ] = useState('');


  const [
    firstName,
    setFirstName,
  ] = useState(user?.firstName ?? '');

  const [
    lastName,
    setLastName,
  ] = useState(user?.lastName ?? '');

  const [
    email,
    setEmail,
  ] = useState(user?.email ?? '');

  const [
    profileImage,
    setProfileImage,
  ] = useState(
    user?.profileImage ?? '',
  );


  const [
    currentPassword,
    setCurrentPassword,
  ] = useState('');

  const [
    newPassword,
    setNewPassword,
  ] = useState('');

  const [
    confirmPassword,
    setConfirmPassword,
  ] = useState('');


  useEffect(() => {

    loadProfile();

  }, []);


  const loadProfile =
    async () => {

      setLoadingProfile(true);

      setError('');

      try {

        const data =
          await userService.getProfile();

        setProfile(data);

        setFirstName(
          data.firstName,
        );

        setLastName(
          data.lastName,
        );

        setEmail(
          data.email,
        );

        setProfileImage(
          data.profileImage ?? '',
        );

      } catch (error: any) {

        const message =
          error?.response?.data?.message ||
          error?.message ||
          'Unable to load profile.';

        setError(message);

      } finally {

        setLoadingProfile(false);
      }
    };


  const clearMessages =
    () => {

      setError('');
      setSuccess('');
    };


  const handleProfileSubmit =
    async (
      event: React.FormEvent<HTMLFormElement>,
    ) => {

      event.preventDefault();

      clearMessages();

      setSavingProfile(true);

      const request: UpdateProfileRequest = {
        firstName,
        lastName,
        email,
        profileImage:
          profileImage.trim() || null,
      };

      try {

        const updated =
          await userService.updateProfile(
            request,
          );

        setProfile(updated);

        setFirstName(
          updated.firstName,
        );

        setLastName(
          updated.lastName,
        );

        setEmail(
          updated.email,
        );

        setProfileImage(
          updated.profileImage ?? '',
        );

        /*
         * Keep Zustand/localStorage
         * synchronized with backend.
         */
        updateUser({
          id: updated.id,
          firstName: updated.firstName,
          lastName: updated.lastName,
          email: updated.email,
          profileImage:
            updated.profileImage,
          role: updated.role,
          active: updated.active,
        });

        setSuccess(
          'Profile updated successfully.',
        );

      } catch (error: any) {

        const message =
          error?.response?.data?.message ||
          error?.message ||
          'Unable to update profile.';

        setError(message);

      } finally {

        setSavingProfile(false);
      }
    };


  const handlePasswordSubmit =
    async (
      event: React.FormEvent<HTMLFormElement>,
    ) => {

      event.preventDefault();

      clearMessages();

      if (
        newPassword !== confirmPassword
      ) {

        setError(
          'New password and confirm password must match.',
        );

        return;
      }

      if (
        currentPassword === newPassword
      ) {

        setError(
          'New password must be different from current password.',
        );

        return;
      }

      setChangingPassword(true);

      const request: ChangePasswordRequest = {
        currentPassword,
        newPassword,
        confirmPassword,
      };

      try {

        await authService.changePassword(
          request,
        );

        setCurrentPassword('');
        setNewPassword('');
        setConfirmPassword('');

        setSuccess(
          'Password changed successfully. Please sign in again if you were logged out of another session.',
        );

      } catch (error: any) {

        const message =
          error?.response?.data?.message ||
          error?.message ||
          'Unable to change password.';

        setError(message);

      } finally {

        setChangingPassword(false);
      }
    };


  if (loadingProfile) {

    return (
      <div
        className="
          min-vh-100
          d-flex
          align-items-center
          justify-content-center
        "
      >

        <Spinner animation="border" />

      </div>
    );
  }


  return (
    <div className="p-4">

      <div className="mb-4">

        <h2 className="fw-bold mb-1">
          Settings
        </h2>

        <p className="text-muted mb-0">
          Manage your profile and account security.
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


      {success && (

        <Alert
          variant="success"
          dismissible
          onClose={() => setSuccess('')}
        >
          {success}
        </Alert>

      )}


      <Card className="border-0 shadow-sm">

        <Card.Body className="p-0">

          <Tabs
            defaultActiveKey="profile"
            className="px-3 pt-3"
            onSelect={() =>
              clearMessages()
            }
          >

            {/* ================================================= */}
            {/* PROFILE */}
            {/* ================================================= */}

            <Tab
              eventKey="profile"
              title={
                <>
                  <i className="bi bi-person me-2" />
                  Profile
                </>
              }
            >

              <div className="p-4">

                <Row>

                  <Col
                    xs={12}
                    md={4}
                    className="text-center mb-4 mb-md-0"
                  >

                    {profile?.profileImage ? (

                      <img
                        src={
                          profile.profileImage
                        }
                        alt="Profile"
                        className="
                          rounded-circle
                          border
                          object-fit-cover
                        "
                        style={{
                          width: 120,
                          height: 120,
                        }}
                      />

                    ) : (

                      <div
                        className="
                          rounded-circle
                          bg-light
                          border
                          d-inline-flex
                          align-items-center
                          justify-content-center
                        "
                        style={{
                          width: 120,
                          height: 120,
                        }}
                      >

                        <i
                          className="
                            bi
                            bi-person
                            fs-1
                            text-secondary
                          "
                        />

                      </div>

                    )}

                    <h5 className="mt-3 mb-1">

                      {profile?.firstName}{' '}
                      {profile?.lastName}

                    </h5>

                    <p className="text-muted">
                      {profile?.email}
                    </p>

                  </Col>


                  <Col xs={12} md={8}>

                    <h5 className="fw-bold mb-4">
                      Account Information
                    </h5>

                    <Row className="g-3">

                      <Col md={6}>

                        <div className="text-muted small">
                          First Name
                        </div>

                        <div className="fw-semibold">
                          {profile?.firstName}
                        </div>

                      </Col>


                      <Col md={6}>

                        <div className="text-muted small">
                          Last Name
                        </div>

                        <div className="fw-semibold">
                          {profile?.lastName}
                        </div>

                      </Col>


                      <Col md={6}>

                        <div className="text-muted small">
                          Email
                        </div>

                        <div className="fw-semibold">
                          {profile?.email}
                        </div>

                      </Col>


                      <Col md={6}>

                        <div className="text-muted small">
                          Role
                        </div>

                        <div className="fw-semibold">
                          {profile?.role}
                        </div>

                      </Col>


                      <Col md={6}>

                        <div className="text-muted small">
                          Account Status
                        </div>

                        <div>

                          <span
                            className={
                              profile?.active
                                ? 'badge text-bg-success'
                                : 'badge text-bg-danger'
                            }
                          >
                            {profile?.active
                              ? 'Active'
                              : 'Inactive'}
                          </span>

                        </div>

                      </Col>


                      <Col md={6}>

                        <div className="text-muted small">
                          Member Since
                        </div>

                        <div className="fw-semibold">
                          {profile?.createdAt
                            ? new Date(
                                profile.createdAt,
                              ).toLocaleDateString()
                            : '-'}
                        </div>

                      </Col>

                    </Row>

                  </Col>

                </Row>

              </div>

            </Tab>


            {/* ================================================= */}
            {/* EDIT PROFILE */}
            {/* ================================================= */}

            <Tab
              eventKey="edit-profile"
              title={
                <>
                  <i className="bi bi-pencil me-2" />
                  Edit Profile
                </>
              }
            >

              <div className="p-4">

                <h5 className="fw-bold mb-1">
                  Edit Profile
                </h5>

                <p className="text-muted mb-4">
                  Update your personal information.
                </p>


                <Form
                  onSubmit={
                    handleProfileSubmit
                  }
                >

                  <Row>

                    <Col md={6}>

                      <Form.Group
                        className="mb-3"
                      >

                        <Form.Label>
                          First Name
                        </Form.Label>

                        <Form.Control
                          type="text"
                          value={firstName}
                          onChange={(event) =>
                            setFirstName(
                              event.target.value,
                            )
                          }
                          maxLength={100}
                          required
                        />

                      </Form.Group>

                    </Col>


                    <Col md={6}>

                      <Form.Group
                        className="mb-3"
                      >

                        <Form.Label>
                          Last Name
                        </Form.Label>

                        <Form.Control
                          type="text"
                          value={lastName}
                          onChange={(event) =>
                            setLastName(
                              event.target.value,
                            )
                          }
                          maxLength={100}
                          required
                        />

                      </Form.Group>

                    </Col>


                    <Col xs={12}>

                      <Form.Group
                        className="mb-3"
                      >

                        <Form.Label>
                          Email
                        </Form.Label>

                        <Form.Control
                          type="email"
                          value={email}
                          onChange={(event) =>
                            setEmail(
                              event.target.value,
                            )
                          }
                          maxLength={100}
                          required
                        />

                        <Form.Text className="text-muted">
                          Changing your email will also
                          change the email used for login.
                        </Form.Text>

                      </Form.Group>

                    </Col>


                    <Col xs={12}>

                      <Form.Group
                        className="mb-4"
                      >

                        <Form.Label>
                          Profile Image URL
                        </Form.Label>

                        <Form.Control
                          type="url"
                          placeholder="https://example.com/profile.jpg"
                          value={profileImage}
                          onChange={(event) =>
                            setProfileImage(
                              event.target.value,
                            )
                          }
                        />

                      </Form.Group>

                    </Col>

                  </Row>


                  <Button
                    type="submit"
                    variant="dark"
                    disabled={savingProfile}
                  >

                    {savingProfile ? (

                      <>
                        <Spinner
                          size="sm"
                          className="me-2"
                        />

                        Saving...

                      </>

                    ) : (

                      <>
                        <i className="bi bi-check-lg me-2" />
                        Save Changes
                      </>

                    )}

                  </Button>

                </Form>

              </div>

            </Tab>


            {/* ================================================= */}
            {/* SECURITY */}
            {/* ================================================= */}

            <Tab
              eventKey="security"
              title={
                <>
                  <i className="bi bi-shield-lock me-2" />
                  Security
                </>
              }
            >

              <div className="p-4">

                <h5 className="fw-bold mb-1">
                  Change Password
                </h5>

                <p className="text-muted mb-4">
                  Keep your account secure by using a
                  strong password.
                </p>


                <Form
                  onSubmit={
                    handlePasswordSubmit
                  }
                >

                  <Row>

                    <Col
                      xs={12}
                      md={8}
                      lg={6}
                    >

                      <Form.Group
                        className="mb-3"
                      >

                        <Form.Label>
                          Current Password
                        </Form.Label>

                        <Form.Control
                          type="password"
                          value={currentPassword}
                          onChange={(event) =>
                            setCurrentPassword(
                              event.target.value,
                            )
                          }
                          required
                          autoComplete="current-password"
                        />

                      </Form.Group>


                      <Form.Group
                        className="mb-3"
                      >

                        <Form.Label>
                          New Password
                        </Form.Label>

                        <Form.Control
                          type="password"
                          value={newPassword}
                          onChange={(event) =>
                            setNewPassword(
                              event.target.value,
                            )
                          }
                          required
                          minLength={8}
                          autoComplete="new-password"
                        />

                        <Form.Text className="text-muted">
                          Password must contain at least
                          8 characters.
                        </Form.Text>

                      </Form.Group>


                      <Form.Group
                        className="mb-4"
                      >

                        <Form.Label>
                          Confirm New Password
                        </Form.Label>

                        <Form.Control
                          type="password"
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
                            newPassword !==
                              confirmPassword
                          }
                        />

                        <Form.Control.Feedback type="invalid">
                          Passwords do not match.
                        </Form.Control.Feedback>

                      </Form.Group>


                      <Button
                        type="submit"
                        variant="dark"
                        disabled={
                          changingPassword
                        }
                      >

                        {changingPassword ? (

                          <>
                            <Spinner
                              size="sm"
                              className="me-2"
                            />

                            Updating...

                          </>

                        ) : (

                          <>
                            <i className="bi bi-key me-2" />
                            Update Password
                          </>

                        )}

                      </Button>

                    </Col>

                  </Row>

                </Form>

              </div>

            </Tab>

          </Tabs>

        </Card.Body>

      </Card>

    </div>
  );
};


export default SettingsPage;