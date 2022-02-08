# doc-mngr

## Description
Desktop application that aims to find documents quickly based on the content and metadata of said documents. 

## Backend
- clojure

## Frontend
- clojurescript
- reagant
- electron
- shadow-cljs

### Setup
```shell
npm install
```

### Dev
```shell
shadow-cljs watch app
```
This should ouput :
```shell
shadow-cljs - HTTP server available at http://localhost:8000
shadow-cljs - server version: 2.17.0 running at http://localhost:9631
shadow-cljs - nREPL server started on port 36561
shadow-cljs - watching build :app
```
`shadow-cljs` will look for changes in the `src` folder and hot reload them. 
It's possible to connect to the shadow-cljs REPL by using the port (here `36561`).

To run `electron`:
```shell
electron .
```

# Test
## Run all tests
```shell
clj -X:test
```

## Run specific tests with Cursive (IDE)
- create a test REPL
  - Run -> Edit Configurations...
  - '+' -> Clojure REPL -> Local
  - Configuration -> How to run it -> Run with deps -> Set Aliases to test
- then use Cursive commands to run the tests