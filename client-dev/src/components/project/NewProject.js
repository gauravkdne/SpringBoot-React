import React, {Component} from 'react';
import './NewProject.css'
import {BUDGET_REGEX, DESCRIPTION_MAX_LENGTH, NAME_MAX_LENGTH, NAME_MIN_LENGTH} from './../../constants/AppConstants'
import {createProject} from './../../util/APIUtils';
import {Button, Card, DatePicker, Form, Input, message, Tooltip} from 'antd';

const FormItem = Form.Item;
const {TextArea} = Input;

class NewProject extends Component {
    constructor(props) {
        super(props);
        this.state = {
            name: {
                value: ''
            },
            description: {
                value: ''
            },
            budget: {
                value: ''
            },
            bidExpiry: {
                value: ''
            }
        }
    }

    handleSubmit = (event) => {
        event.preventDefault();
        const projectData = {
            name: this.state.name.value,
            description: this.state.description.value,
            budget: this.state.budget.value,
            bidExpiry: this.state.bidExpiry.value
        };

        createProject(projectData)
            .then(response => {
                message.success('Project created successfully');
                this.props.history.push("/");
            }).catch(error => {
            if (error.status === 401) {
                this.props.handleLogout('/login', 'error', 'You have been logged out. Please login create project.');
            } else {
                message.error(error.message || 'Sorry! Something went wrong. Please try again!');
            }
        });
    }

    isFormInvalid = () => {
        return !(this.state.name.validateStatus === 'success' &&
            this.state.description.validateStatus === 'success' &&
            this.state.budget.validateStatus === 'success' &&
            this.state.bidExpiry.validateStatus === 'success'
        );
    }

    handleInputChange = (event, validate) => {
        const target = event.target;
        const inputName = target.name;
        const inputValue = target.value;

        this.setState({
            [inputName]: {
                value: inputValue,
                ...validate(inputValue)
            }
        });
    }

    validateName = (name) => {
        if (name.length < NAME_MIN_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `Name is too short (Minimum ${NAME_MIN_LENGTH} characters needed.)`
            }
        } else if (name.length > NAME_MAX_LENGTH) {
            return {
                validationStatus: 'error',
                errorMsg: `Name is too long (Maximum ${NAME_MAX_LENGTH} characters allowed.)`
            }
        } else {
            return {
                validateStatus: 'success',
                errorMsg: null,
            };
        }
    }

    validateBudget = (budget) => {
        if (!budget) {
            return {
                validateStatus: 'error',
                errorMsg: 'Budget cannot not be empty'
            }
        }
        if (!BUDGET_REGEX.test(budget)) {
            return {
                validateStatus: 'error',
                errorMsg: 'Budget is not valid'
            }
        }
        return {
            validateStatus: 'success',
            errorMsg: null
        }
    }

    validateDescription = (description) => {
        if (description.length > DESCRIPTION_MAX_LENGTH) {
            return {
                validationStatus: 'error',
                errorMsg: `description is too long (Maximum ${DESCRIPTION_MAX_LENGTH} characters allowed.)`
            }
        } else {
            return {
                validateStatus: 'success',
                errorMsg: null
            }
        }
    }

    handleBidExpiry = (value) => {
        if (value === null) {
            return {
                validationStatus: 'error',
                errorMsg: `Enter a valid bid expiration date`
            }
        } else {
            this.setState({
                bidExpiry: {
                    value: value,
                    validateStatus: 'success'
                }
            });
        }
    }

    getDisabledDates = (current) => {
        return current && current.valueOf() < Date.now();
    }

    render() {
        return (
            <div className="new-project-container">
                <Card title="Enter Project Details">
                    <div className="new-project-box">
                        <Form onSubmit={this.handleSubmit} className="new-project-form">
                            {/*<h1></h1>*/}
                            <FormItem
                                label="Name"
                                validateStatus={this.state.name.validateStatus}
                                help={this.state.name.errorMsg}>
                                <Input
                                    size="default"
                                    name="name"
                                    autoComplete="off"
                                    placeholder="Name of the Project"
                                    value={this.state.name.value}
                                    onChange={(event) => this.handleInputChange(event, this.validateName)}/>
                            </FormItem>
                            <FormItem label="Description"
                                      hasFeedback
                                      validateStatus={this.state.description.validateStatus}
                                      help={this.state.description.errorMsg}>
                            <TextArea
                                size="default"
                                name="description"
                                autoComplete="off"
                                rows="5"
                                placeholder="Description of the Project"
                                value={this.state.description.value}
                                onChange={(event) => this.handleInputChange(event, this.validateDescription)}/>
                            </FormItem>
                            <FormItem
                                label="Budget"
                                hasFeedback
                                validateStatus={this.state.budget.validateStatus}
                                help={this.state.budget.errorMsg}>
                                <Input
                                    size="default"
                                    className="new-project-budget"
                                    name="budget"
                                    autoComplete="off"
                                    placeholder="Maximum Budget in USD"
                                    value={this.state.budget.value}
                                    onChange={(event) => this.handleInputChange(event, this.validateBudget)}/>
                            </FormItem>
                            <FormItem
                                label="Bid Expiration"
                                validateStatus={this.state.bidExpiry.validateStatus}
                                help={this.state.bidExpiry.errorMsg}>
                                <DatePicker
                                    showTime
                                    className="new-project-bid"
                                    format="YYYY-MM-DD HH:mm:ss"
                                    placeholder="Expiration Date"
                                    disabledDate={this.getDisabledDates}
                                    onChange={this.handleBidExpiry}/>
                            </FormItem>
                            <FormItem>
                                <Tooltip placement="rightBottom" title="Create a Project">
                                    <Button type="primary"
                                            htmlType="submit"
                                            size="large"
                                            className="create-project-form-button"
                                            disabled={this.isFormInvalid()}>Add Project</Button>
                                </Tooltip>
                            </FormItem>
                        </Form>
                    </div>
                </Card>
            </div>
        );
    }

}

export default NewProject;