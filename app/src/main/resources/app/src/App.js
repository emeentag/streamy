import React from "react";
import {
	Grid,
	Container,
	Typography,
	FormGroup,
	FormControlLabel,
	Switch,
	TableContainer,
	Table,
	TableHead,
	TableRow,
	TableCell,
	TableBody,
	Paper,
	CircularProgress,
	Button,
	Fab,
} from "@material-ui/core";
import Alert from "@material-ui/lab/Alert";
import { withStyles } from "@material-ui/core/styles";
import FileSelector from "./components/FileSelector";
import Style from "./Style";
import CheckIcon from "@material-ui/icons/Check";
import BlurLinear from "@material-ui/icons/BlurLinear";
class App extends React.Component {
	constructor(props) {
		super(props);

		this.state = {
			isRealtime: false,
			files: null,
			isFilesLoading: true,
			isFileLoadingFailed: false,
		};
	}

	componentDidMount() {
		this.loadFiles();
	}

	realtimeCheckHandler(e) {
		this.setState({
			[e.currentTarget.name]: e.currentTarget.checked,
		});
	}

	uploadSuccessHandler() {
		console.log("upload is done!");
	}

	uploadFailHandler() {
		console.log("upload is failed!");
	}

	loadFiles() {
		fetch("files", {
			method: "GET",
		})
			.then((response) => response.json())
			.then((data) => {
				this.setState({
					...this.state,
					isFilesLoading: false,
					isFileLoadingFailed: false,
					files: data,
				});
				console.log(data);
			})
			.catch((error) => {
				this.setState({
					...this.state,
					files: null,
					isFilesLoading: false,
					isFileLoadingFailed: true,
				});
			});
	}

	getFileList() {
		if (this.state.isFilesLoading) {
			return <CircularProgress />;
		} else if (this.state.isFileLoadingFailed) {
			return (
				<Alert severity="error" elevation={6} variant="filled">
					There was an error while fetching the files.
				</Alert>
			);
		} else if (!this.state.isFilesLoading && this.state.files.length > 0) {
			return (
				<TableContainer
					component={Paper}
					className={this.props.classes.tableContainer}
				>
					<Table
						stickyHeader
						className={this.props.classes.table}
						aria-label="File list"
					>
						<TableHead>
							<TableRow>
								<TableCell>No</TableCell>
								<TableCell>File name</TableCell>
								<TableCell align="right">File size</TableCell>
								<TableCell align="right">Action</TableCell>
							</TableRow>
						</TableHead>
						<TableBody>
							{this.state.files.reverse().map((row, idx) => (
								<TableRow hover key={row.fileName}>
									<TableCell>{this.state.files.length - idx}</TableCell>
									<TableCell component="th" scope="row">
										{row.fileName}
									</TableCell>
									<TableCell align="right">{row.fileSize} MB</TableCell>
									<TableCell align="right">
										{/* <Fab
											aria-label="save"
											color="primary"
											className={buttonClassname}
											onClick={handleButtonClick}
										>
											{success ? <CheckIcon /> : <BlurLinear />}
										</Fab>
										{loading && (
											<CircularProgress
												size={68}
												className={classes.fabProgress}
											/>
										)} */}
									</TableCell>
								</TableRow>
							))}
						</TableBody>
					</Table>
				</TableContainer>
			);
		} else {
			return (
				<Alert severity="warning" elevation={6} variant="filled">
					There is no file for processing.
				</Alert>
			);
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
							<Grid container direction="column" spacing={5}>
								<Grid item className={this.props.classes.fileSelector}>
									<FileSelector
										label="Realtime Data File"
										defaultText="Select a file to upload."
										inputId="upload-file"
										isRealtime={this.state.isRealtime}
										uploadSuccessHandler={this.uploadSuccessHandler.bind(this)}
										uploadFailHandler={this.uploadFailHandler.bind(this)}
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
					<Grid item>
						<Grid container justify="center">
							{this.getFileList()}
						</Grid>
					</Grid>
				</Grid>
			</Container>
		);
	}
}

export default withStyles(Style.JSS)(App);
