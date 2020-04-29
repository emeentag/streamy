import React from "react";
import { Grid, Container, Typography } from "@material-ui/core";
import { withStyles } from "@material-ui/core/styles";
import FileSelector from "./FileSelector";
import Style from "./Style";
class App extends React.Component {
	render() {
		return (
			<Container maxWidth="sm" className={this.props.classes.container}>
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
						<Grid container direction="column" spacing={5}>
							<Grid item>
								<FileSelector
									label="Realtime Data File"
									defaultText="Select a file to upload."
									inputId="realtime-data"
								/>
							</Grid>
							<Grid item>
								<FileSelector
									label="Batch Data File"
									defaultText="Select a file to upload."
									inputId="batch-data"
								/>
							</Grid>
						</Grid>
					</Grid>
				</Grid>
			</Container>
		);
	}
}

export default withStyles(Style.JSS)(App);
