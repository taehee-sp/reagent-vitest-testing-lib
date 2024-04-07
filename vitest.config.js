// https://vitejs.dev/config/
export default {
    define: {
        'process.env': process.env
    },
    test: {
        globals: true,
        environment: 'jsdom',
        optimizer: {
            web: {
                include: ['vitest-canvas-mock']
            }
        },
        exclude: ['node_modules', 'dist', '.idea', '.git', '.cache', 'e2e'],
        include: ['target/vitest/js/main.js'],
        setupFiles: ['./src/test/setupTest.js'],
        css: true,
        pool: 'vmThreads',
        poolOptions: {
            useAtomics: true
        },
        environmentOptions: {
            jsdom: {
                resources: 'usable',
            },
        },
        onConsoleLog(log, type) {
            if (log.includes('inside a test was not wrapped in act')) return false;
            if (log.includes('shadow-cljs')) return false;
            if (log.includes('Error: Uncaught')) return false;
            if (log.includes('The above error occurred in the')) return false;
            if (log.includes('React does not recognize')) return false;
            if (log.includes('for a non-boolean attribute')) return false;
            if (log.includes('Warning: ReactDOM.render is no longer supported in React 18')) return false;
            if (log.includes('should be wrapped into act')) return false;

        },
    }
}
