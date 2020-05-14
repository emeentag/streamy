import React from "react";
import {
	Grid,
	Container,
	Typography,
	FormGroup,
	FormControlLabel,
	Switch,
	Snackbar,
	CircularProgress,
} from "@material-ui/core";
import { withStyles } from "@material-ui/core/styles";
import FileSelector from "./components/FileSelector";
import Style from "./Style";
import FileList from "./components/FileList";
class App extends React.Component {
	constructor(props) {
		super(props);

		this.state = {
			file: {
				isRealtime: false,
				files: null,
				isFilesLoading: true,
				isFileLoadingSuccess: false,
			},
		};

		this.currentFab = null;
	}

	componentDidMount() {
		this.loadFiles();
	}

	realtimeCheckHandler(e) {
		this.setState({
			...this.state,
			file: {
				...this.state.file,
				[e.currentTarget.name]: e.currentTarget.checked,
			},
		});
	}

	uploadSuccessHandler() {
		this.loadFiles();
	}

	uploadFailHandler() {}

	loadFiles() {
		this.setState({
			...this.state,
			file: {
				...this.state.file,
				isFilesLoading: true,
			},
		});

		fetch("files", {
			method: "GET",
		})
			.then((response) => response.json())
			.then((data) => {
				this.setState({
					...this.state,
					file: {
						...this.state.file,
						isFilesLoading: false,
						isFileLoadingSuccess: true,
						files: data.reverse(),
					},
				});
			})
			.catch((error) => {
				this.setState({
					...this.state,
					file: {
						...this.state.file,
						files: null,
						isFilesLoading: false,
						isFileLoadingSuccess: false,
					},
				});
			});
	}

	getFilesList() {
		if (this.state.file.isFilesLoading) {
			return (
				<Snackbar open={true}>
					<CircularProgress />
				</Snackbar>
			);
		} else {
			return (
				<Grid container justify="center">
					<FileList
						file={this.state.file}
						loadFiles={this.loadFiles.bind(this)}
					/>
				</Grid>
			);
		}
	}

	render() {
		return (
			<Container maxWidth="md" className={this.props.classes.container}>
				<Grid container direction="column" spacing={5}>
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
							<Grid container direction="column">
								<Grid item className={this.props.classes.fileSelector}>
									<FileSelector
										label="Realtime Data File"
										defaultText="Select a file to upload."
										inputId="upload-file"
										isRealtime={this.state.file.isRealtime}
										uploadSuccessHandler={this.uploadSuccessHandler.bind(this)}
										uploadFailHandler={this.uploadFailHandler.bind(this)}
									/>
									<FormControlLabel
										className={this.props.classes.isRealtimeCheck}
										control={
											<Switch
												checked={this.state.file.isRealtime}
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
					<Grid item>{this.getFilesList()}</Grid>
				</Grid>
			</Container>
		);
	}
}

export default withStyles(Style.JSS)(App);
