import React, { Component } from "react";
import { Button, TextField, Grid } from "@material-ui/core";
import { withStyles } from "@material-ui/core/styles";
import Style from "./Style";

class FileSelector extends Component {
	constructor(props) {
		super(props);
		this.filePath = React.createRef();
	}

	onChangeHandler(e) {
		let file = e.currentTarget.files[0];
		this.filePath.current.value = file.name;
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
						inputRef={this.filePath}
					/>
				</Grid>
				<Grid item sm={3}>
					<input
						accept=".json,.csv"
						className={this.props.classes.uploadInput}
						id={this.props.inputId}
						type="file"
						onChange={this.onChangeHandler.bind(this)}
					/>
					<label htmlFor={this.props.inputId}>
						<Button variant="contained" color="primary" component="span">
							Upload
						</Button>
					</label>
				</Grid>
			</Grid>
		);
	}
}

export default withStyles(Style.JSS)(FileSelector);
