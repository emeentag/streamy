export default class Style {
	static JSS = (theme) => {
		return {
			uploadInput: {
				display: "none",
			},
			container: {
				paddingTop: theme.spacing(8),
				paddingBottom: theme.spacing(8),
			},
			fileUpload: {
				marginTop: theme.spacing(5),
			},
			fileSelector: {
				height: theme.spacing(13),
			},
			table: {
				minWidth: 650,
			},
			fabProgress: {
				color: theme.palette.primary,
				position: "absolute",
				top: -2,
				left: -2,
				zIndex: 1,
			},
			uploadBtn: {
				position: "relative",
			},
			uploadFailedFab: {
				backgroundColor: "#f44336",
			},

			uploadSuccessFab: {
				backgroundColor: "#4caf50",
			},
			tableContainer: {
				maxHeight: 485,
			},
			fileProcessFab: {},
			isRealtimeCheck: {
				paddingTop: 10,
			},
			fileProcessingCell: {
				position: "relative",
			},
			fabFileProcessingProgress: {
				color: "#555555",
				position: "absolute",
				top: 11,
				left: 39,
				zIndex: 1,
			},
			fabFileDeleteProgress: {
				color: "#f50057",
				position: "absolute",
				top: 11,
				left: 95,
				zIndex: 1,
			},
		};
	};
}
