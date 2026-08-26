import React from 'react';
import ReactDOM from 'react-dom/client';

import App from './App';

import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css';

import './assets/styles/global.css';
import './assets/styles/theme.css';
import './assets/styles/layout.css';
import './assets/styles/components.css';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
);