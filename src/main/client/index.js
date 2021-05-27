import React from 'react';
import ReactDOM from 'react-dom';
import App from './App.react';

ReactDOM.render(
    <App/>,
    document.getElementById('root')
);

/*
WIP FIX FOR REFRESH

import React from 'react';
import ReactDOM from 'react-dom';
import { HashRouter } from "react-router-dom";

import App from './App.react';

ReactDOM.render(
    <App/>,
    document.getElementById('root')
);

ReactDOM.render(
  <HashRouter>
    <App />
  </HashRouter>,
  document.getElementById("root")
);
*/
