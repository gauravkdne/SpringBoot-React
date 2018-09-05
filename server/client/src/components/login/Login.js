import React, {Component} from 'react';
import './Login.css';
import {login} from './../../util/APIUtils';
import {ACCESS_TOKEN} from './../../constants/AppConstants';
import {Link} from 'react-router-dom';

import {Button, Form, Icon, Input, message, Tooltip} from 'antd';

const FormItem = Form.Item;

class Login extends Component {
    render() {
        const AntWrappedLoginForm = Form.create()(LoginForm);
        return (
            <div className="login-container">
                <div className="login-box">
                    <AntWrappedLoginForm onLogin={this.props.onLogin}/>
                </div>
            </div>
        );
    }
}

class LoginForm extends Component {
    handleSubmit = (event) => {
        event.preventDefault();
        this.props.form.validateFields((err, values) => {
            if (!err) {
                const loginRequest = Object.assign({}, values);
                login(loginRequest)
                    .then(response => {
                        localStorage.setItem(ACCESS_TOKEN, response.accessToken);
                        this.props.onLogin();
                    }).catch(error => {
                    if (error.status === 401) {
                        message.error('Your Username or Password is incorrect. Please try again!');
                    } else if (error.status === 500) {
                        message.error('Please enter valid user credentials');
                    } else {
                        message.error(error.message || 'Sorry! Something went wrong. Please try again!');
                    }
                });
            }
        });
    };

    render() {
        const {getFieldDecorator} = this.props.form;
        return (
            <div>
                <h1>Login</h1>
                <Form onSubmit={this.handleSubmit} className="login-form">
                    <FormItem>
                        {getFieldDecorator('usernameOrEmail', {
                            rules: [{required: true, message: 'Please input your username or email!'}],
                        })(
                            <Input
                                prefix={<Icon type="user"/>}
                                size="large"
                                name="usernameOrEmail"
                                placeholder="Username or Email"/>
                        )}
                    </FormItem>
                    <FormItem>
                        {getFieldDecorator('password', {
                            rules: [{required: true, message: 'Please input your Password!'}],
                        })(
                            <Input
                                prefix={<Icon type="lock"/>}
                                size="large"
                                name="password"
                                type="password"
                                placeholder="Password"/>
                        )}
                    </FormItem>
                    <FormItem>
                        <Tooltip placement="bottomLeft" title="Log into Job Marketplace">
                            <Button type="primary" htmlType="submit" size="large"
                                    className="login-form-button">Login</Button>
                        </Tooltip>
                    </FormItem>
                    <div>
                        Or <Link to="/signup">Register</Link>
                    </div>
                </Form>
            </div>
        );
    }
}

export default Login;