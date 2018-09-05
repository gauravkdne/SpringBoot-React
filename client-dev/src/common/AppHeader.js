import React, {Component} from 'react';
import {Link, withRouter} from 'react-router-dom';
import './AppHeader.css';
import {Dropdown, Icon, Input, Layout, Menu, Tooltip} from 'antd';
import {ROLE_SELLER} from "./../constants/AppConstants";

const Header = Layout.Header;

class AppHeader extends Component {
    handleMenuClick = ({key}) => {
        if (key === "logout") {
            this.props.onLogout();
        }
    };

    render() {
        let menuItems = [];

        if (this.props.currentUser) {
            //avoiding array splice as number of elements is small
            menuItems.push(
                <Menu.Item key="/">
                    <Tooltip placement="bottomRight" title="Home">
                        <Link to="/">
                            <Icon type="home" className="nav-icon"/>
                        </Link>
                    </Tooltip>
                </Menu.Item>,);
                menuItems.push(<Menu.Item key="/project/search">
                    <Tooltip placement="bottom" title="Search by Project Id">
                        <Input.Search
                            placeholder="Enter Project Id"
                            onSearch={value => this.handleSearch(value)}
                            style={{width: 160}}
                        />
                    </Tooltip>
                </Menu.Item>,);

            if (this.props.currentUser.role === ROLE_SELLER) {
                menuItems.push(<Menu.Item key="/project/new">
                    <Tooltip placement="bottomRight" title="Create a new project">
                        <Link to="/project/new">
                            <Icon type="plus" className="nav-icon"/>
                        </Link>
                    </Tooltip>
                </Menu.Item>,);
            }
            menuItems.push(
                <Menu.Item key="/profile" className="profile-menu">
                    <ProfileDropdownMenu
                        currentUser={this.props.currentUser}
                        handleMenuClick={this.handleMenuClick}/>
                </Menu.Item>);
        } else {
            menuItems = [
                <Menu.Item key="/login">
                    <Link to="/login">Login</Link>
                </Menu.Item>,
                <Menu.Item key="/signup">
                    <Link to="/signup">Signup</Link>
                </Menu.Item>
            ];
        }

        return (
            <Header className="app-header">
                <div className="container">
                    <div className="app-title">
                        <Link to="/">Job Marketplace </Link>
                    </div>
                    <Menu
                        className="app-menu"
                        mode="horizontal"
                        selectedKeys={[this.props.location.pathname]}
                        style={{lineHeight: '64px'}}>
                        {menuItems}
                    </Menu>
                </div>
            </Header>
        );
    }

    handleSearch = (query) => {
        this.props.history.push({pathname: '/project/search', query})
    }
}

function ProfileDropdownMenu(props) {
    const dropdownMenu = (
        <Menu onClick={props.handleMenuClick} className="profile-dropdown-menu">
            <Menu.Item key="user-info" className="dropdown-item" disabled>
                <div className="user-full-name-info">
                    {props.currentUser.name}
                </div>
                <div className="username-info">
                    @{props.currentUser.username}
                </div>
            </Menu.Item>
            <Menu.Divider/>
            <Menu.Item key="profile" className="dropdown-item">
                <Link to={`/users/${props.currentUser.username}`}>Profile</Link>
            </Menu.Item>
            <Menu.Item key="logout" className="dropdown-item">
                Logout
            </Menu.Item>
        </Menu>
    );

    return (
        <Tooltip placement="rightBottom" title="User Profile">
            <Dropdown
                overlay={dropdownMenu}
                trigger={['click']}
                getPopupContainer={() => document.getElementsByClassName('profile-menu')[0]}>
                <a className="ant-dropdown-link">
                    <Icon type="user" className="nav-icon" style={{marginRight: 0}}/> <Icon type="down"/>
                </a>
            </Dropdown>
        </Tooltip>
    );
}


export default withRouter(AppHeader);