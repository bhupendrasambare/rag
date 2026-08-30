import {
  useState,
} from 'react';

import {
  Button,
} from 'react-bootstrap';

import {
  useNavigate,
  Outlet,
} from 'react-router-dom';

import {
  useAuthStore,
} from '../store';

import '../assets/styles/AppLayout.css';


interface SidebarItem {
  label: string;
  icon: string;
  path: string;
}


const mainItems: SidebarItem[] = [

  {
    label: 'Dashboard',
    icon: 'bi-speedometer2',
    path: '/dashboard',
  },

  {
    label: 'Documents',
    icon: 'bi-file-earmark-text',
    path: '/documents',
  },

  {
    label: 'Chat',
    icon: 'bi-chat-dots',
    path: '/chat',
  },

  {
    label: 'Profile',
    icon: 'bi-person',
    path: '/profile',
  },

];


const bottomItems: SidebarItem[] = [

  {
    label: 'Settings',
    icon: 'bi-gear',
    path: '/settings',
  },

];


const AppLayout = () => {

  const navigate =
    useNavigate();

  const logout =
    useAuthStore(
      (state) => state.logout,
    );

  const [
    collapsed,
    setCollapsed,
  ] = useState(false);


  const handleLogout =
    () => {

      logout();

      navigate(
        '/login',
        { replace: true },
      );

    };


  return (
    <div
      className={
        collapsed
          ? 'app-layout sidebar-collapsed'
          : 'app-layout'
      }
    >

      <aside className="app-sidebar">

        {/* Header */}

        <div className="sidebar-header">

          <div className="sidebar-logo">

            <i className="bi bi-database-fill" />

            {!collapsed && (
              <span>
                RAG
              </span>
            )}

          </div>


          <Button
            variant="link"
            className="sidebar-toggle"
            onClick={() =>
              setCollapsed(
                (value) => !value,
              )
            }
            aria-label={
              collapsed
                ? 'Open sidebar'
                : 'Close sidebar'
            }
          >

            <i
              className={
                collapsed
                  ? 'bi bi-chevron-right'
                  : 'bi bi-chevron-left'
              }
            />

          </Button>

        </div>


        {/* Navigation */}

        <nav className="sidebar-navigation">

          <div className="sidebar-section">

            {!collapsed && (
              <div className="sidebar-section-title">
                MENU
              </div>
            )}


            {mainItems.map(
              (item) => (

                <button
                  key={item.path}
                  type="button"
                  className="sidebar-item"
                  onClick={() =>
                    navigate(
                      item.path,
                    )
                  }
                  title={
                    collapsed
                      ? item.label
                      : undefined
                  }
                >

                  <i
                    className={`bi ${item.icon}`}
                  />

                  {!collapsed && (
                    <span>
                      {item.label}
                    </span>
                  )}

                </button>

              ),
            )}

          </div>


          {/* Bottom */}

          <div className="sidebar-bottom">

            {bottomItems.map(
              (item) => (

                <button
                  key={item.path}
                  type="button"
                  className="sidebar-item"
                  onClick={() =>
                    navigate(
                      item.path,
                    )
                  }
                  title={
                    collapsed
                      ? item.label
                      : undefined
                  }
                >

                  <i
                    className={`bi ${item.icon}`}
                  />

                  {!collapsed && (
                    <span>
                      {item.label}
                    </span>
                  )}

                </button>

              ),
            )}


            <button
              type="button"
              className="sidebar-item sidebar-logout"
              onClick={handleLogout}
              title={
                collapsed
                  ? 'Logout'
                  : undefined
              }
            >

              <i className="bi bi-box-arrow-right" />

              {!collapsed && (
                <span>
                  Logout
                </span>
              )}

            </button>

          </div>

        </nav>

      </aside>

      <main className="app-main">

        <Outlet />

      </main>

    </div>
  );
};


export default AppLayout;