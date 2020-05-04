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
		};
	};
}
