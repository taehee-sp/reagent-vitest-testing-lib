# vitest + @testing-library + reagent

install dependencies

```sh  
pnpm install
```

compile tests in `src/test/reagent_test.cljs` into `target/vitest/js` by shadow-cljs. (see `shadow-cljs.edn`)

```sh
pnpm unit:compile
```

in another terminal, watch and run compile output by vitest.

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
