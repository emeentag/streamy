import React, { Component } from "react";
import Style from "../Style";
import {
	Snackbar,
	CircularProgress,
	TableContainer,
	Table,
	TableHead,
	TableRow,
	TableCell,
	Paper,
	Grid,
	Fab,
	TableBody,
} from "@material-ui/core";
import { withStyles } from "@material-ui/core/styles";
import Alert from "@material-ui/lab/Alert";
import CheckIcon from "@material-ui/icons/Check";
import BlurLinear from "@material-ui/icons/BlurLinear";
import DeleteIcon from "@material-ui/icons/Delete";

class FileList extends Component {
	constructor(props) {
		super(props);

		this.state = {
			isFileProcessing: false,
			isFileProcessingSuccess: false,
			isFileDeleting: false,
			isFileDeletingSuccess: false,
		};
	}

	processFile(fileName) {
		this.setState({
			...this.state,
			isFileProcessing: true,
		});

		// make processing request.
	}

	deleteFile(fileName, idx) {
		this.setState({
			...this.state,
			isFileDeleting: true,
		});

		fetch("files/" + fileName, {
			method: "DELETE",
		}).then((data) => {
			if (data.status === 200) {
				this.setState({
					...this.state,
					isFileDeleting: false,
					isFileDeletingSuccess: true,
				});

				this.props.loadFiles();
			}
		});
	}

	fabClickHandler(e) {
		this.currentElement = e.id;

		if (this.currentElement.startsWith("process-fab")) {
			this.processFile(e.fileName, e.idx);
		} else if (this.currentElement.startsWith("delete-fab")) {
			this.deleteFile(e.fileName, e.idx);
		}
	}

	isFabProloading(currentElement) {
		let isPreloading = false;

		if (currentElement.startsWith("process-fab")) {
			isPreloading =
				this.state.isFileProcessing && this.currentElement === currentElement;
		} else if (currentElement.startsWith("delete-fab")) {
			isPreloading =
				this.state.isFileDeleting && this.currentElement === currentElement;
		}

		return isPreloading;
	}

	getFabIcon(currentElement) {
		let currentIcon;

		if (currentElement.startsWith("process-fab")) {
			if (this.state.isFileProcessingSuccess) {
				currentIcon = <CheckIcon />;
			} else {
				currentIcon = <BlurLinear />;
			}
		} else if (currentElement.startsWith("delete-fab")) {
			currentIcon = <DeleteIcon />;
		}

		return currentIcon;
	}

	generateFileRows(files) {
		let numFiles = files.length;

		let rows = files.map((row, idx) => {
			let currentIDX = numFiles - idx;
			let currentProcessFabId = `process-fab-${currentIDX}`;
			let currentDeleteFabId = `delete-fab-${currentIDX}`;

			return (
				<TableRow hover key={row.fileName}>
					<TableCell>{currentIDX}</TableCell>
					<TableCell component="th" scope="row">
						{row.fileName}
					</TableCell>
					<TableCell align="right">{row.fileSize} MB</TableCell>
					<TableCell
						align="right"
						className={this.props.classes.fileProcessingCell}
					>
						<Grid container direction="row" spacing={2} justify="flex-end">
							<Grid item>
								<Fab
									id={currentProcessFabId}
									aria-label="process"
									color="default"
									className={this.props.classes.fileProcessFab}
									onClick={this.fabClickHandler.bind(this, {
										id: currentProcessFabId,
										idx: currentIDX,
										fileName: row.fileName,
									})}
									size="small"
								>
									{this.getFabIcon(currentProcessFabId)}
								</Fab>
								{this.isFabProloading(currentProcessFabId) && (
									<CircularProgress
										size={50}
										className={this.props.classes.fabFileProcessingProgress}
									/>
								)}
							</Grid>
							<Grid item>
								<Fab
									id={currentDeleteFabId}
									aria-label="delete"
									color="secondary"
									className={this.props.classes.fileDeleteFab}
									onClick={this.fabClickHandler.bind(this, {
										id: currentDeleteFabId,
										idx: currentIDX,
										fileName: row.fileName,
									})}
									size="small"
								>
									{this.getFabIcon(currentDeleteFabId)}
								</Fab>
								{this.isFabProloading(currentDeleteFabId) && (
									<CircularProgress
										size={50}
										className={this.props.classes.fabFileDeleteProgress}
									/>
								)}
							</Grid>
						</Grid>
					</TableCell>
				</TableRow>
			);
		});

		return rows;
	}

	getFileTable() {
		let files = this.props.file.files;

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
					<TableBody>{this.generateFileRows(files)}</TableBody>
				</Table>
			</TableContainer>
		);
	}

	render() {
		if (this.props.file.isFileLoadingSuccess) {
			if (this.props.file.files.length <= 0) {
				return (
					<Snackbar
						open={true}
						anchorOrigin={{ vertical: "bottom", horizontal: "left" }}
					>
						<Alert severity="warning" elevation={6} variant="filled">
							There is no file for processing.
						</Alert>
					</Snackbar>
				);
			} else {
				// Render the list in table.
				return this.getFileTable();
			}
		} else {
			// Show alert.
			return (
				<Snackbar
					open={true}
					anchorOrigin={{ vertical: "bottom", horizontal: "left" }}
				>
					<Alert elevation={6} variant="filled" severity="error">
						There was an error while fetching the files.
					</Alert>
				</Snackbar>
			);
		}
	}
}

export default withStyles(Style.JSS)(FileList);
