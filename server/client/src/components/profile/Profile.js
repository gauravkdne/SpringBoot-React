import React, {Component} from 'react';
import './Profile.css';
import NotFound from './../../common/NotFound';
import ServerError from './../../common/ServerError';
import ProjectList from './../../components/project/ProjectList';
import {getUserProfile} from './../../util/APIUtils';
import LoadingIndicator from './../../common/LoadingIndicator';
import {BIDS_PLACED_BY_USER, BIDS_WON_BY_USER, ROLE_SELLER, USER_CREATED_PROJECTS} from './../../constants/AppConstants'

import {Tabs} from 'antd'

const TabPane = Tabs.TabPane;

class Profile extends Component {
    loadUserProfile = (username) => {
        this.setState({
            isLoading: true
        });

        getUserProfile(username)
            .then(response => {
                this.setState({
                    user: response,
                    isSeller: response.roles.some(e => e.name === ROLE_SELLER),
                    isLoading: false
                });
            }).catch(error => {
            if (error.status === 404) {
                this.setState({
                    notFound: true,
                    isLoading: false
                });
            } else {
                this.setState({
                    serverError: true,
                    isLoading: false
                });
            }
        });
    }

    constructor(props) {
        super(props);
        this.state = {
            user: '',
            isSeller: '',
            isLoading: false
        }
    }

    componentDidMount() {
        const username = this.props.match.params.username;
        this.loadUserProfile(username);
    }

    componentWillReceiveProps(nextProps) {
        if (this.props.match.params.username !== nextProps.match.params.username) {
            this.loadUserProfile(nextProps.match.params.username);
        }
    }

    render() {
        if (this.state.isLoading) {
            return <LoadingIndicator/>;
        }

        if (this.state.notFound) {
            return <NotFound/>;
        }

        if (this.state.serverError) {
            return <ServerError/>;
        }

        const tabBarStyle = {
            textAlign: 'center'
        };

        if (!this.state.isSeller) {
            return (
                <div className="profile">
                    {
                        this.state.user ? (
                            <div className="user-profile">
                                <div className="user-details">
                                    <div className="user-summary">
                                        <div className="full-name">{this.state.user.name}</div>
                                        <div className="username">@{this.state.user.username}</div>
                                    </div>
                                </div>
                                <div className="user-project-details">
                                    <Tabs defaultActiveKey="1"
                                          animated={false}
                                          tabBarStyle={tabBarStyle}
                                          size="large"
                                          className="profile-tabs">
                                        <TabPane tab={`Bids Won`} key="1">
                                            <ProjectList username={this.props.match.params.username}
                                                         type={BIDS_WON_BY_USER}/>
                                        </TabPane>
                                        <TabPane tab={`Placed Bids`} key="2">
                                            <ProjectList username={this.props.match.params.username}
                                                         type={BIDS_PLACED_BY_USER}/>
                                        </TabPane>
                                    </Tabs>
                                </div>
                            </div>

                        ) : null
                    }
                </div>
            );
        } else {
            return (
                <div className="profile">
                    {
                        this.state.user ? (
                            <div className="user-profile">
                                <div className="user-details">
                                    <div className="user-summary">
                                        <div className="full-name">{this.state.user.name}</div>
                                        <div className="username">@{this.state.user.username}</div>
                                    </div>
                                </div>
                                <div className="user-project-details">
                                    <Tabs defaultActiveKey="1"
                                          animated={false}
                                          tabBarStyle={tabBarStyle}
                                          size="large"
                                          className="profile-tabs">
                                        <TabPane tab={`Posted Projects`} key="1">
                                            <ProjectList username={this.props.match.params.username}
                                                         type={USER_CREATED_PROJECTS}/>
                                        </TabPane>
                                    </Tabs>
                                </div>
                            </div>

                        ) : null
                    }
                </div>
            );
        }
    }
}

export default Profile;