import React, {Component} from "react";
import "./ProjectSearch.css";
import {withRouter} from "react-router-dom";
import {BUDGET_REGEX, ROLE_BUYER, ROLE_SELLER} from './../../constants/AppConstants'
import {getProjectById, placeBid} from "./../../util/APIUtils";
import LoadingIndicator from "./../../common/LoadingIndicator";
import {Button, Card, Form, InputNumber, message, Tooltip} from "antd";
import Moment from "react-moment";

const FormItem = Form.Item;

class ProjectSearch extends Component {
    handlePlacedBidChange = (value) => {
        if (!BUDGET_REGEX.test(value)) {
            this.setState({
                validateStatus: 'error',
                placedBid: {
                    value: '',
                    errorMsg: 'Bid is not valid'
                },
            });
        } else {
            this.setState({
                placedBid: {
                    value: value,
                    errorMsg: null
                },
                validateStatus: 'success',
            });
        }
    }

    constructor(props) {
        super(props);
        this.state = {
            query: '',
            project: '',
            isLoading: true,
            currentBid: '',
            placedBid: {
                value: '',
                errorMsg: null
            },
            validateStatus: '',
            pageError: '',
            currentUser: ''
        };
    }

    componentWillMount() {
        if (this.props.location.query) {
            this.setState({
                query: this.props.location.query,
                isLoading: true,
                currentUser: this.props.currentUser
            }, () => {
                this.loadProjectInfo(this.state)
            });
        }
    }

    componentWillReceiveProps(nextProps) {
        this.setState({
            query: nextProps.location.query,
            isLoading: true,
            currentUser: this.props.currentUser
        }, () => {
            this.loadProjectInfo(this.state)
        });
    }

    loadProjectInfo = () => {
        let promise = getProjectById(this.state.query);

        if (!promise) {
            return;
        }

        this.setState({
            isLoading: true
        });

        promise
            .then(response => {
                this.setState({
                    project: response,
                    isLoading: false,
                    pageError: ""
                })
            }).catch(error => {
            this.setState({
                isLoading: false
            })
            if (error.status === 400) {
                let errorTitle = 'Enter a valid numeric project Id';
                this.setState({
                    pageError: errorTitle,
                    isLoading: false
                });
            }
            else if (error.status === 401) {
                this.props.handleLogout('/login', 'error', 'You have been logged out. Please login create project.');
            } else {
                let errorTitle = 'Project does not exist!';
                this.setState({
                    pageError: errorTitle,
                    isLoading: false
                });
            }
        });
    };

    handleSubmit = (event) => {
        event.preventDefault();
        const bidInfo = {
            projectId: this.state.project.id,
            bid: this.state.placedBid.value
        };

        placeBid(bidInfo)
            .then(response => {
                message.success('Bid placed successfully');
                this.props.history.push("/");
            }).catch(error => {
            if (error.status === 401) {
                this.props.handleLogout('/login', 'error', 'You have been logged out. Please login create project.');
            } else {
                message.error(error.message);
            }
        });
    }

    isFormInvalid = () => {
        return this.state.validateStatus !== 'success';
    }

    render() {
        if (!this.state.query) {
            return (
                <div className="page-not-found">
                    <div className="desc">
                        Enter Project Id in the Search Field
                    </div>
                </div>
            );
        } else if (this.state.isLoading) {
            return <LoadingIndicator/>;
        } else if (this.state.pageError) {
            return (
                <div className="page-not-found">
                    <div className="desc">
                        {this.state.pageError}
                    </div>
                </div>
            );
        } else {

            if (this.state.project.isBiddingExpired || this.state.currentUser.role === ROLE_SELLER) {
                return (
                    <div className="project-container">
                        <Card title="Project Details">
                            <div className="project-box">
                                <div className="project-prop-name"> Name:</div>
                                <div> {this.state.project.name} </div>
                                <div className="project-prop-name"> Description:</div>
                                <textarea style={{border: "dotted 1px"}} rows="5" cols="10"
                                          maxLength="400" readOnly value={this.state.project.description}></textarea>
                                <div className="project-prop-name"> Budget:</div>
                                <div> {this.state.project.budget} </div>
                                <div className="project-prop-name"> Bid Expiration:</div>
                                <div><Moment>{this.state.project.bidExpiry}</Moment></div>
                                {this.state.project.isBiddingExpired ? (
                                    <div className="bid-expired-msg"> Bidding is expired for this
                                        project!</div>) : null}
                            </div>
                        </Card>
                    </div>
                );
            } else {
                return (
                    <div className="project-container">
                        <Card title="Project Details">
                            <div className="project-box">
                                <div className="project-prop-name"> Name:</div>
                                <div> {this.state.project.name} </div>
                                <div className="project-prop-name"> Description:</div>
                                <textarea style={{border: "dotted 1px"}} rows="5" cols="10"
                                          maxLength="400" readOnly value={this.state.project.description}></textarea>
                                <div className="project-prop-name"> Budget:</div>
                                <div> {this.state.project.budget} </div>
                                <div className="project-prop-name"> Bid Expiration:</div>
                                <div><Moment>{this.state.project.bidExpiry}</Moment></div>
                                <div className="project-prop-name"> Lowest Bid:</div>
                                <div>
                                    {this.state.project.bid}<span className="currency">  USD</span>
                                </div>
                                <Form onSubmit={this.handleSubmit} className="place-bid-form">
                                    <div className="project-prop-name"> Your Bid:</div>
                                    <FormItem
                                        validateStatus={this.state.validateStatus}
                                        help={this.state.placedBid.errorMsg}>
                                        <InputNumber
                                            min={1}
                                            step={0.1}
                                            className="bid-input"
                                            onChange={this.handlePlacedBidChange}
                                        />
                                    </FormItem>
                                    <span className="currency">  USD</span>
                                    <FormItem>
                                        <Tooltip placement="bottomLeft" title="Place Bid on Project">
                                            <Button type="primary"
                                                    htmlType="submit"
                                                    size="large"
                                                    disabled={this.isFormInvalid()}
                                                    className="bid-submit-button">Place Bid</Button>
                                        </Tooltip>
                                    </FormItem>
                                </Form>
                            </div>
                        </Card>
                    </div>
                );
            }
        }
    }
}

export default withRouter(ProjectSearch);
