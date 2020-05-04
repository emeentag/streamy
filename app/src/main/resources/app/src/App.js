import React from "react";
import {
	Grid,
	Container,
	Typography,
	FormGroup,
	FormControlLabel,
	Switch,
} from "@material-ui/core";
import { withStyles } from "@material-ui/core/styles";
import FileSelector from "./components/FileSelector";
import Style from "./Style";
class App extends React.Component {
	constructor(props) {
		super(props);

		this.selectedFile = React.createRef();

		this.state = {
			isRealtime: false,
		};
	}

	fileSelectHandler(file) {
		this.selectedFile = file;
	}

	realtimeCheckHandler(e) {
		this.setState({
			[e.currentTarget.name]: e.currentTarget.checked,
		});
		console.log(e.currentTarget.checked);
	}

	processHandler(e) {
		console.log("process btn clicked!");
		console.log("this.selectedFile: " + this.selectedFile.name);

		let formData = new FormData();
		formData.append("file", this.selectedFile);
		formData.append("isRealtime", this.state.isRealtime);

		fetch("files/upload", {
			method: "POST",
			body: formData,
		})
			.then((data) => {
				console.log(data);
			})
			.catch((error) => {
				console.log(error);
			});
	}

	render() {
		return (
			<Container maxWidth="md" className={this.props.classes.container}>
				<Grid container direction="column" spacing={3}>
					<Grid item>
						<Typography variant="h2" gutterBottom>
							Streamy Data Processor
						</Typography>
					</Grid>
					<Grid item>
						<Typography variant="body1" gutterBottom>
							Please upload your realtime and batch data for processing.
							Realtime data is going to use for simulating the realtime event
							streams.
						</Typography>
					</Grid>
					<Grid item className={this.props.classes.fileUpload}>
						<FormGroup>
							<Grid container direction="column" spacing={5}>
								<Grid item className={this.props.classes.fileSelector}>
									<FileSelector
										label="Realtime Data File"
										defaultText="Select a file to upload."
										inputId="realtime-data"
										processHandler={this.processHandler.bind(this)}
										fileSelectHandler={this.fileSelectHandler.bind(this)}
									/>
								</Grid>
								<Grid item>
									<FormControlLabel
										control={
											<Switch
												checked={this.state.isRealtime}
												onChange={this.realtimeCheckHandler.bind(this)}
												name="isRealtime"
												color="primary"
											/>
										}
										label="Simulate Realtime"
									/>
								</Grid>
							</Grid>
						</FormGroup>
					</Grid>
				</Grid>
			</Container>
		);
	}
}

export default withStyles(Style.JSS)(App);
