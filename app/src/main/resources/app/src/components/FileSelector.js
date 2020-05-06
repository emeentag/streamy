import React, { Component } from "react";
import { Button, TextField, Grid } from "@material-ui/core";
import { withStyles } from "@material-ui/core/styles";
import Style from "../Style";

class FileSelector extends Component {
	constructor(props) {
		super(props);

		this.inputText = React.createRef();
		this.inputFile = React.createRef();

		this.state = {
			file: null,
		};
	}

	fileSelectHandler(e) {
		let selectedFile = e.currentTarget.files[0];
		this.inputText.current.value = selectedFile.name;

		this.setState({
			...this.state,
			file: selectedFile,
		});
	}

	clickHandler(e) {
		if (e.currentTarget.id === "cancel-btn") {
			this.cancelSelection();
		}
	}

	processHandler(e) {
		let formData = new FormData();
		formData.append("file", this.state.file);
		formData.append("isRealtime", this.props.isRealtime);

		fetch("files/upload", {
			method: "POST",
			body: formData,
		})
			.then((data) => {
				console.log(data);
				this.returnToInitialState();
				this.props.uploadSuccessHandler();
			})
			.catch((error) => {
				console.log(error);
				this.returnToInitialState();
				this.props.uploadFailHandler();
			});
	}

	returnToInitialState() {
		this.cancelSelection();
	}

	cancelSelection() {
		this.inputFile.value = null;
		this.inputText.current.value = this.props.label;

		this.setState({
			...this.state,
			file: null,
		});
	}

	getButtons() {
		let fileSelected = (
			<React.Fragment>
				<Grid item>
					<Button
						id="process-btn"
						variant="contained"
						color="default"
						component="span"
						onClick={this.processHandler.bind(this)}
					>
						Process
					</Button>
				</Grid>
				<Grid item>
					<Button
						id="cancel-btn"
						variant="contained"
						color="secondary"
						component="span"
						onClick={this.clickHandler.bind(this)}
					>
						Cancel
					</Button>
				</Grid>
			</React.Fragment>
		);

		let fileNotSelected = (
			<Grid item>
				<label htmlFor={this.props.inputId}>
					<Button
						variant="contained"
						color="primary"
						component="span"
						name="upload-btn"
					>
						Upload
					</Button>
				</label>
			</Grid>
		);

		return (
			<Grid container direction="row" spacing={1} justify="flex-end">
				{this.state.file ? fileSelected : fileNotSelected}
			</Grid>
		);
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

					{this.getButtons()}
				</Grid>
			</Grid>
		);
	}
}

export default withStyles(Style.JSS)(FileSelector);
