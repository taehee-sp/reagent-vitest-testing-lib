import "@testing-library/jest-dom";

if (typeof global !== "undefined") {
    await import('vitest-canvas-mock');
}

const oldError = console.error;

console.error = (...args) => {
	if (args[0].includes('Error: Uncaught')) {
		return;
	}
	if (args[0].includes('The above error occurred in the')) {
		return;
	}

	if (args[0].includes('Warning: ReactDOM.render is no longer supported in React 18')) {
		return;
	}
	if (args[0].includes('should be wrapped into act')) {
		return;
	}

	oldError(args[0]);
};
