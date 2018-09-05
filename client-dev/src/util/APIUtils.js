import {ACCESS_TOKEN, API_BASE_URL, PROJECT_LIST_SIZE} from './../constants/AppConstants';

const request = (options) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
    })

    if (localStorage.getItem(ACCESS_TOKEN)) {
        headers.append('Authorization', 'Bearer ' + localStorage.getItem(ACCESS_TOKEN))
    }

    const defaults = {headers: headers};
    options = Object.assign({}, defaults, options);

    return fetch(options.url, options)
        .then(response =>
            response.json().then(json => {
                if (!response.ok) {
                    return Promise.reject(json);
                }
                return json;
            })
        );
};

export function getAllProjects(page, size) {
    page = page || 0;
    size = size || PROJECT_LIST_SIZE;

    return request({
        url: API_BASE_URL + "/projects?page=" + page + "&size=" + size,
        method: 'GET'
    });
}

export function getProjectById(id) {
    return request({
        url: API_BASE_URL + "/projects/" + id,
        method: 'GET'
    });
}

export function createProject(projectData) {
    return request({
        url: API_BASE_URL + "/projects",
        method: 'POST',
        body: JSON.stringify(projectData)
    });
}

export function placeBid(bidInfo) {
    return request({
        url: API_BASE_URL + "/projects/placeBid",
        method: 'POST',
        body: JSON.stringify(bidInfo)
    });
}

export function login(loginRequest) {
    return request({
        url: API_BASE_URL + "/auth/signin",
        method: 'POST',
        body: JSON.stringify(loginRequest)
    });
}

export function signup(signupRequest) {
    return request({
        url: API_BASE_URL + "/auth/signup",
        method: 'POST',
        body: JSON.stringify(signupRequest)
    });
}

export function checkUsernameAvailability(username) {
    return request({
        url: API_BASE_URL + "/user/checkUsernameAvailability?username=" + username,
        method: 'GET'
    });
}

export function checkEmailAvailability(email) {
    return request({
        url: API_BASE_URL + "/user/checkEmailAvailability?email=" + email,
        method: 'GET'
    });
}

export function getCurrentUser() {
    if (!localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({
        url: API_BASE_URL + "/user/me",
        method: 'GET'
    });
}

export function getUserProfile(username) {
    return request({
        url: API_BASE_URL + "/users/" + username,
        method: 'GET'
    });
}

export function getUserPlacedBids(username, page, size) {
    page = page || 0;
    size = size || PROJECT_LIST_SIZE;

    return request({
        url: API_BASE_URL + "/users/" + username + "/bids?page=" + page + "&size=" + size,
        method: 'GET'
    });
}

export function getBidsWonBy(username, page, size) {
    page = page || 0;
    size = size || PROJECT_LIST_SIZE;

    return request({
        url: API_BASE_URL + "/users/" + username + "/bidsWon?page=" + page + "&size=" + size,
        method: 'GET'
    });
}

export function getProjectsCreatedByUser(username, page, size) {
    page = page || 0;
    size = size || PROJECT_LIST_SIZE;

    return request({
        url: API_BASE_URL + "/users/" + username + "/projects?page=" + page + "&size=" + size,
        method: 'GET'
    });
}