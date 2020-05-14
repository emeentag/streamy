import React, { Component } from "react";
import {
	TextField,
	Grid,
	Fab,
	CircularProgress,
	Snackbar,
} from "@material-ui/core";
import { withStyles } from "@material-ui/core/styles";
import Style from "../Style";
import CheckIcon from "@material-ui/icons/Check";
import SaveIcon from "@material-ui/icons/Save";
import FailIcon from "@material-ui/icons/PriorityHigh";
import Alert from "@material-ui/lab/Alert";

class FileSelector extends Component {
	constructor(props) {
		super(props);

		this.inputText = React.createRef();
		this.inputFile = React.createRef();

		this.state = {
			file: null,
			uploadInProgress: false,
			uploadSuccess: false,
		};
	}

	fileSelectHandler(e) {
		let selectedFile = e.currentTarget.files[0];
		this.inputText.current.value = selectedFile.name;

		this.uploadFile(selectedFile);
	}

	uploadFile(file) {
		this.setState({
			...this.state,
			file: file,
			uploadInProgress: true,
		});

		let formData = new FormData();
		formData.append("file", file);
		formData.append("isRealtime", this.props.isRealtime);

		fetch("files/upload", {
			method: "POST",
			body: formData,
		})
			.then((response) => response.json())
			.then((data) => {
				this.setState({
					...this.state,
					uploadInProgress: false,
					uploadSuccess: true,
				});
			})
			.catch((error) => {
				console.log(error);
				this.setState({
					...this.state,
					uploadInProgress: false,
					uploadSuccess: false,
				});
			});
	}

	uploadCompleted(isSuccess) {
		this.returnToInitialState();

		isSuccess
			? this.props.uploadSuccessHandler()
			: this.props.uploadFailHandler();
	}

	returnToInitialState() {
		this.inputFile.value = null;
		this.inputText.current.value = this.props.label;

		this.setState({
			file: null,
			uploadInProgress: false,
			uploadSuccess: false,
		});
	}

	render() {
		return (
			<Grid container spacing={3} justify="center" alignItems="center">
				<Grid item sm={9}>
					<TextField
						disabled
						label={this.props.label}
						defaultValue={this.props.defaultText}
						variant="outlined"
						fullWidth
						inputRef={this.inputText}
					/>
				</Grid>
				<Grid item sm={3}>
					<input
						accept=".json,.csv,.txt"
						className={this.props.classes.uploadInput}
						id={this.props.inputId}
						type="file"
						onChange={this.fileSelectHandler.bind(this)}
						ref={(element) => (this.inputFile = element)}
					/>
					<Grid container direction="row" spacing={1} justify="flex-end">
						<Grid item className={this.props.classes.uploadBtn}>
							<label htmlFor={this.state.file ? "" : this.props.inputId}>
								<Fab
									aria-label="save"
									className={
										this.state.file && !this.state.uploadInProgress
											? this.state.uploadSuccess
												? this.props.classes.uploadSuccessFab
												: this.props.classes.uploadFailedFab
											: this.props.classes.uploadDefaultFab
									}
									color="primary"
									component="span"
								>
									{this.state.file && !this.state.uploadInProgress ? (
										this.state.uploadSuccess ? (
											<CheckIcon />
										) : (
											<FailIcon />
										)
									) : (
										<SaveIcon />
									)}
								</Fab>
								{this.state.uploadInProgress && (
									<CircularProgress
										size={68}
										className={this.props.classes.fabProgress}
									/>
								)}
							</label>
						</Grid>
					</Grid>
				</Grid>
				{this.state.file && !this.state.uploadInProgress && (
					<Snackbar
						open={true}
						autoHideDuration={2000}
						onClose={this.uploadCompleted.bind(this, this.state.uploadSuccess)}
						anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
					>
						{this.state.uploadSuccess ? (
							<Alert elevation={6} variant="filled" severity="success">
								File uploaded.
							</Alert>
						) : (
							<Alert elevation={6} variant="filled" severity="error">
								Upload failed.
							</Alert>
						)}
					</Snackbar>
				)}
			</Grid>
		);
	}
}

export default withStyles(Style.JSS)(FileSelector);
