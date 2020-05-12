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
	Fab,
	Snackbar,
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
			file: {
				isRealtime: false,
				files: null,
				isFilesLoading: true,
				isFileLoadingSuccess: false,
				isFileProcessing: false,
				isFileProcessingSuccess: false,
			},
			alert: {
				isAlertSnackbarOpen: true,
			},
		};

		this.currentFab = React.createRef();
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
						files: data,
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

	clickHandler(e) {
		let currentId = e.currentTarget.id;
		if (currentId.startsWith("process-fab")) {
			this.currentFab = currentId;
			this.setState({
				...this.state,
				file: {
					...this.state.file,
					isFileProcessing: true,
				},
			});

			// make processing request.
		}
	}

	preLoadCurrentFab(curFab) {
		return this.state.file.isFileProcessing && this.currentFab === curFab;
	}

	getFileTable() {
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
						{this.state.file.files.reverse().map((row, idx) => (
							<TableRow hover key={row.fileName}>
								<TableCell>{this.state.file.files.length - idx}</TableCell>
								<TableCell component="th" scope="row">
									{row.fileName}
								</TableCell>
								<TableCell align="right">{row.fileSize} MB</TableCell>
								<TableCell
									align="right"
									className={this.props.classes.fileProcessingCell}
								>
									<Fab
										id={`process-fab-${this.state.file.files.length - idx}`}
										aria-label="save"
										color="default"
										className={this.props.classes.fileProcessFab}
										onClick={this.clickHandler.bind(this)}
										size="small"
									>
										{this.state.file.isFileProcessingSuccess ? (
											<CheckIcon />
										) : (
											<BlurLinear />
										)}
									</Fab>
									{this.preLoadCurrentFab(
										`process-fab-${this.state.file.files.length - idx}`
									) && (
										<CircularProgress
											size={50}
											className={this.props.classes.fabFileProcessingProgress}
										/>
									)}
								</TableCell>
							</TableRow>
						))}
					</TableBody>
				</Table>
			</TableContainer>
		);
	}

	getFileList() {
		if (this.state.file.isFilesLoading) {
			return (
				<Snackbar open={true}>
					<CircularProgress />
				</Snackbar>
			);
		} else if (!this.state.file.isFileLoadingSuccess) {
			return (
				<Snackbar
					open={this.state.alert.isAlertSnackbarOpen}
					autoHideDuration={5000}
					onClose={(e) => {
						this.setState({
							...this.state,
							alert: {
								...this.state.alert,
								isAlertSnackbarOpen: false,
							},
						});
					}}
				>
					<Alert elevation={6} variant="filled" severity="error">
						There was an error while fetching the files.
					</Alert>
				</Snackbar>
			);
		} else if (
			!this.state.file.isFilesLoading &&
			this.state.file.files.length > 0
		) {
			return this.getFileTable();
		} else {
			return (
				<Snackbar
					open={this.state.alert.isAlertSnackbarOpen}
					autoHideDuration={5000}
					onClose={(e) => {
						this.setState({
							...this.state,
							alert: {
								...this.state.alert,
								isAlertSnackbarOpen: false,
							},
						});
					}}
				>
					<Alert severity="warning" elevation={6} variant="filled">
						There is no file for processing.
					</Alert>
				</Snackbar>
			);
		}
	}

	processHandler(e) {
		// let formData = new FormData();
		// formData.append("file", this.state.file);
		// formData.append("isRealtime", this.props.isRealtime);
		// fetch("files/upload", {
		// 	method: "POST",
		// 	body: formData,
		// })
		// 	.then((data) => {
		// 		console.log(data);
		// 		this.returnToInitialState();
		// 		this.props.uploadSuccessHandler();
		// 	})
		// 	.catch((error) => {
		// 		console.log(error);
		// 		this.returnToInitialState();
		// 		this.props.uploadFailHandler();
		// 	});
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
