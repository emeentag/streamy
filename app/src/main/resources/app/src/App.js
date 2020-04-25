import React from "react";
import { Grid, Container, Typography } from "@material-ui/core";

export default class App extends React.Component {
	constructor(props) {
		super(props);
	}

	render() {
		return (
			<React.Fragment>
				<Container maxWidth="sm">
					<Grid container>
						<Grid item>
							<Typography variant="h3">This is Grid 1</Typography>
						</Grid>
						<Grid item>
							<Typography variant="h3">This is Grid 2</Typography>
						</Grid>
					</Grid>
				</Container>
			</React.Fragment>
		);
	}
}

// function App() {
//   return (
//     <div className="App">
//       <header className="App-header">
//         <img src={logo} className="App-logo" alt="logo" />
//         <p>
//           Edit <code>src/App.js</code> and save to reload.
//         </p>
//         <a
//           className="App-link"
//           href="https://reactjs.org"
//           target="_blank"
//           rel="noopener noreferrer"
//         >
//           Learn React
//         </a>
//       </header>
//     </div>
//   );
// }

// export default App;
