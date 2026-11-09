<#
.SYNOPSIS
    Build (and publish) the Docker images for the OEX microservices with one command.

.DESCRIPTION
    Runs `mvn compile jib:build` for each selected service. Jib's `jib:build` goal talks
    straight to a container registry's API - it does not need a local Docker daemon, but it
    DOES immediately publish the image to whatever registry is configured in that service's
    pom.xml (currently Docker Hub, under `bernardking08/<service>:oex`). Run `docker login`
    once before using this script.

.PARAMETER Services
    Names of the services to build (e.g. "authentication" "result"). If omitted, all
    services are built.

.PARAMETER Parallel
    Build the selected services concurrently instead of one at a time. Each service's
    Maven output is written to docs\.build-logs\<service>.log.

.EXAMPLE
    .\build-images.ps1
    Builds and publishes all 7 services, one at a time.

.EXAMPLE
    .\build-images.ps1 authentication result
    Builds and publishes only the named services.

.EXAMPLE
    .\build-images.ps1 -Parallel
    Builds and publishes all services concurrently.

.EXAMPLE
    .\build-images.ps1 -Parallel authentication examination notification result
    Combine -Parallel with specific service names.
#>

[CmdletBinding()]
param(
    [switch]$Parallel,

    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$Services
)

$ErrorActionPreference = "Stop"

$ScriptDir   = $PSScriptRoot
$ProjectRoot = Split-Path $ScriptDir -Parent
$LogDir      = Join-Path $ScriptDir ".build-logs"

$DefaultServices = @(
    "eurekaserver",
    "configserver",
    "gatewayserver",
    "authentication",
    "examination",
    "notification",
    "result"
)

if (-not $Services -or $Services.Count -eq 0) {
    $Services = $DefaultServices
}

# Reads the Jib <to><image> value straight out of the service's pom.xml so the image ref
# reported here can never drift out of sync with what was actually just published. Every
# pom also has a <from><image> (the base image), so the match must be scoped to <to>.
function Get-ImageRef {
    param([string]$Service)

    $pomPath = Join-Path $ProjectRoot "$Service\pom.xml"
    $content = Get-Content -Raw -Path $pomPath
    $match = [regex]::Match($content, '<to>\s*<image>([^<]*)</image>', "Singleline")
    if (-not $match.Success) {
        return $null
    }
    return $match.Groups[1].Value -replace '\$\{project\.artifactId\}', $Service
}

function Invoke-ServiceBuild {
    param([string]$Service)

    $servicePath = Join-Path $ProjectRoot $Service
    if (-not (Test-Path $servicePath)) {
        throw "No such service folder: $Service"
    }

    Push-Location $servicePath
    try {
        mvn compile jib:build
        if ($LASTEXITCODE -ne 0) {
            throw "mvn compile jib:build failed for $Service (exit code $LASTEXITCODE)"
        }
    }
    finally {
        Pop-Location
    }
}

Write-Host "==> Services to build: $($Services -join ', ')"

if ($Parallel) {
    New-Item -ItemType Directory -Force -Path $LogDir | Out-Null

    $jobs = foreach ($service in $Services) {
        Write-Host "==> Starting build for $service (log: docs\.build-logs\$service.log)"
        Start-Job -Name $service -ScriptBlock {
            param($ProjectRoot, $Service)

            Set-Location (Join-Path $ProjectRoot $Service)
            mvn compile jib:build
            exit $LASTEXITCODE
        } -ArgumentList $ProjectRoot, $service
    }

    $jobs | Wait-Job | Out-Null

    $failed = @()
    foreach ($job in $jobs) {
        $logPath = Join-Path $LogDir "$($job.Name).log"
        Receive-Job -Job $job -Keep | Out-File -FilePath $logPath -Encoding utf8
        if ($job.State -ne "Completed" -or $job.ChildJobs[0].JobStateInfo.State -eq "Failed") {
            $failed += $job.Name
        }
        Remove-Job -Job $job
    }

    Write-Host ""
    if ($failed.Count -eq 0) {
        Write-Host "==> All images built and published successfully: $($Services -join ', ')"
    }
    else {
        Write-Host "==> Build FAILED for: $($failed -join ', ')"
        Write-Host "    See logs in $LogDir for details"
        exit 1
    }
}
else {
    foreach ($service in $Services) {
        Write-Host "==> Building and publishing image for $service"
        Invoke-ServiceBuild -Service $service
        $imageRef = Get-ImageRef -Service $service
        if ($imageRef) {
            Write-Host "    Published: $imageRef"
        }
    }
    Write-Host "==> All images built and published: $($Services -join ', ')"
}
