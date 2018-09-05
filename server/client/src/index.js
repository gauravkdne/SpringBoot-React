import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './app/App';
import registerServiceWorker from './registerServiceWorker';
import {BrowserRouter as Router} from 'react-router-dom';
import moment from 'moment/min/moment-with-locales';
import Moment from 'react-moment';

// Sets the moment instance to use.
Moment.globalMoment = moment;
// Set the output format for every react-moment instance.
Moment.globalFormat = 'YYYY-MM-DD [at] hh:mm:ss a';
// Use a <span> tag for every react-moment instance.
Moment.globalElement = 'span';
// Upper case all rendered dates.
// Moment.globalFilter = (d) => {
//     return d.toUpperCase();
// };

ReactDOM.render(
    <Router>
        <App />
    </Router>,
    document.getElementById('root')
);

registerServiceWorker();
