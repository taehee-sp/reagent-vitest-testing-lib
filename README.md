# vitest + @testing-library + reagent

install dependencies

```sh  
pnpm install
```

shadow-cljs compile tests into `target/vitest/js` without esm dependencies. (see `shadow-cljs.edn`)

```sh
pnpm unit:compile
```

vitest watch and run compile output files.

```sh
pnpm unit
```

with beautiful ui
```sh
pnpm unit --ui
```

## browser mode
you can run component tests in real browser.

first, install playwright test browser.
```sh
pnpm exec playwright install
```

```sh  
pnpm unit:browser
```

you can run component tests in background (also known as headless mode)

```sh
pnpm unit:headless
```
