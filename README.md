# Fluvii
Scala web framework using Http4s and Laminar


## Development mode

Run in SBT (uses fast JS compilation, not optimized):

```
sbt> ~runDev
```

And open http://localhost:9000/frontend

This will restart the server on any changes: shared code, client/server, assets.

## Tests
It is a prerequisite to have jsdom installed, in order for the frontend tests to run. Proposal:
```
yarn add jsdom
```
Then move into an sbt console and run tests as normal

## Production mode

Run in SBT (uses full JS optimization):

```
sbt> ~runProd
```

## Docker packaging

```
sbt> backend/docker:publishLocal
```

Will publish the docker image with fully optimised JS code, and you can run the container:

```bash
✗ docker run --rm -p 8080:8080 backend:0.1.0-SNAPSHOT

Running server on http://0.0.0.0:8080 (mode: prod)
```
