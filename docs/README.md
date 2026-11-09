# docs

## build-images.ps1

Builds and publishes the Docker images for all 7 OEX microservices with one command,
instead of running `mvn compile jib:build` by hand in each service folder.

### What it actually does

Each service's `pom.xml` has a Jib plugin configured like this:

```xml
<to>
    <image>bernardking08/${project.artifactId}:oex</image>
</to>
```

Running `mvn compile jib:build` inside a service folder compiles it and sends the built
image **straight to Docker Hub** - Jib talks to the registry's API directly, it does not
go through your local Docker daemon at all. That means:

- You do **not** need Docker Desktop running for this.
- You **do** need to be logged in to the registry first (see Prerequisites below).
- Every run of the script **publishes** an image, there is no "build only, don't push" mode
  here (that's what `jib:dockerBuild` is for, which this script does not use).

This script just loops that one Maven command over whichever services you ask for, and can
run them one at a time or all at once.

### Prerequisites

1. **Maven** (`mvn`) available on your `PATH`.
2. **Docker Hub login**, once, from any terminal:
   ```powershell
   docker login
   ```
   (Jib reuses the credentials Docker already has saved after this - you do not need
   Docker running afterward, just logged in once.)
3. **PowerShell 7+** (`pwsh`). Run scripts from the `docs` folder itself, or point at the
   full path from anywhere in the repo.

### Usage

All examples assume you're running from inside the `docs` folder. If you're elsewhere in
the repo, replace `.\build-images.ps1` with the full path, e.g.
`.\docs\build-images.ps1`.

| Command | What it builds |
|---|---|
| `.\build-images.ps1` | All 7 services, one after another |
| `.\build-images.ps1 authentication` | Just `authentication` |
| `.\build-images.ps1 authentication result` | Just `authentication` and `result` |
| `.\build-images.ps1 -Parallel` | All 7 services, at the same time |
| `.\build-images.ps1 -Parallel examination notification` | Named services, at the same time |

The services are: `eurekaserver`, `configserver`, `gatewayserver`, `authentication`,
`examination`, `notification`, `result`.

**Typical day-to-day use** - you changed one service and want to ship just that:

```powershell
.\docs\build-images.ps1 result
```

**Typical "ship everything" use** - after a round of changes across several services:

```powershell
.\docs\build-images.ps1 -Parallel
```

### Reading the output

- **Sequential mode** (no `-Parallel`): output streams to your terminal as each service
  builds, in order. If one fails, the script stops immediately and reports a non-zero exit
  code - later services in the list are not attempted.
- **Parallel mode** (`-Parallel`): all selected services build at once in the background.
  Each one's full Maven output is written to its own log file under
  `docs\.build-logs\<service>.log` once it finishes (that folder is created automatically
  and is safe to delete between runs). A summary line at the end lists which services
  succeeded and which failed.

### Troubleshooting

- **"mvn compile jib:build failed for \<service>"** - open that service's log (parallel
  mode) or scroll up in your terminal (sequential mode) and look for the actual Maven/Jib
  error. The most common cause is not being logged in to Docker Hub yet (step 2 above).
- **"No such service folder: \<name>"** - check the spelling against the service list
  above; the script matches folder names under the repo root exactly.
- Want to rename the Docker Hub account or tag? Edit the `<to><image>` line in each
  service's `pom.xml` - the script always reads that value back out, so it can't go stale.
