param(
    [string]$TomcatHome = $env:CATALINA_HOME,
    [string]$JavaHome = $env:JAVA_HOME,
    [ValidateRange(1024, 65535)]
    [int]$Port = 8080
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot

if (-not $TomcatHome) {
    throw 'Specify Tomcat 10.1: -TomcatHome "C:/path/to/apache-tomcat-10.1.x"'
}
$TomcatHome = (Resolve-Path -LiteralPath $TomcatHome).Path
if (-not (Test-Path -LiteralPath (Join-Path $TomcatHome 'lib/catalina.jar'))) {
    throw 'TomcatHome must point to the Tomcat installation directory.'
}
if (-not $JavaHome) {
    throw 'Set JAVA_HOME to JDK 21, or supply -JavaHome "C:/path/to/jdk-21".'
}
$java = Join-Path $JavaHome 'bin/java.exe'
if (-not (Test-Path -LiteralPath $java)) {
    throw "Java executable not found: $java"
}
$javaVersion = (& $java --version | Out-String)
if ($LASTEXITCODE -ne 0 -or $javaVersion -notmatch '(?:openjdk|java) 21[.\s]') {
    throw 'Use JDK 21 to build and run this project.'
}
$envFile = Join-Path $projectRoot '.env'
if (-not (Test-Path -LiteralPath $envFile)) {
    throw 'Create .env from .env.example and configure MySQL before starting.'
}

# Check before building or changing an existing local Tomcat deployment.
$listener = [System.Net.Sockets.TcpListener]::new([System.Net.IPAddress]::Loopback, $Port)
try {
    $listener.Start()
} catch {
    throw "Port $Port is already in use. Stop that server or supply -Port 8081."
} finally {
    $listener.Stop()
}

$oldJavaHome = $env:JAVA_HOME
$oldEnvFile = $env:APP_ENV_FILE
$oldCatalinaHome = $env:CATALINA_HOME
$oldCatalinaBase = $env:CATALINA_BASE
$oldCatalinaTmpdir = $env:CATALINA_TMPDIR
$oldJreHome = $env:JRE_HOME
Push-Location $projectRoot
try {
    $env:JAVA_HOME = $JavaHome
    $env:APP_ENV_FILE = $envFile
    & mvn package
    if ($LASTEXITCODE -ne 0) {
        throw 'Maven build failed. Tomcat was not started.'
    }

    # All generated files stay inside this project; the installed Tomcat is read-only.
    # Keep the running server and its deployed WAR outside Maven's clean directory.
    $tomcatBase = Join-Path $projectRoot '.runtime/tomcat'
    foreach ($directory in @('conf', 'logs', 'temp', 'webapps', 'work')) {
        New-Item -ItemType Directory -Path (Join-Path $tomcatBase $directory) -Force | Out-Null
    }
    Copy-Item -Path (Join-Path $TomcatHome 'conf/*') -Destination (Join-Path $tomcatBase 'conf') -Recurse -Force
    $serverXml = @"
<?xml version="1.0" encoding="UTF-8"?>
<Server port="-1">
  <Service name="Catalina">
    <Connector address="127.0.0.1" port="$Port" protocol="HTTP/1.1" connectionTimeout="20000" />
    <Engine name="Catalina" defaultHost="localhost">
      <Host name="localhost" appBase="webapps" unpackWARs="true" autoDeploy="false" />
    </Engine>
  </Service>
</Server>
"@
    Set-Content -LiteralPath (Join-Path $tomcatBase 'conf/server.xml') -Value $serverXml -Encoding UTF8

    # Deploy a separate copy so rebuilding target does not touch files held by Tomcat.
    Copy-Item -LiteralPath (Join-Path $projectRoot 'target/CRUD-1.1.0.war') `
        -Destination (Join-Path $tomcatBase 'webapps/CRUD.war') -Force

    Write-Host "Configuration: $envFile"
    Write-Host "Open http://localhost:$Port/CRUD/ (Ctrl+C to stop)"
    $env:CATALINA_HOME = $TomcatHome
    $env:CATALINA_BASE = $tomcatBase
    $env:CATALINA_TMPDIR = Join-Path $tomcatBase 'temp'
    $env:JRE_HOME = $JavaHome
    & (Join-Path $TomcatHome 'bin/catalina.bat') run
    if ($LASTEXITCODE -ne 0) {
        throw "Tomcat exited with code $LASTEXITCODE. Check .runtime/tomcat/logs."
    }
} finally {
    $env:JAVA_HOME = $oldJavaHome
    $env:APP_ENV_FILE = $oldEnvFile
    $env:CATALINA_HOME = $oldCatalinaHome
    $env:CATALINA_BASE = $oldCatalinaBase
    $env:CATALINA_TMPDIR = $oldCatalinaTmpdir
    $env:JRE_HOME = $oldJreHome
    Pop-Location
}
