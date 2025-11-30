$ErrorActionPreference = 'Stop'
$appiumPort = 4723
$appiumUrl = "http://127.0.0.1:$appiumPort/status"
$nodePath = 'C:\Program Files\nodejs\node.exe'
$appiumMainScript = 'C:\Users\Asim\AppData\Roaming\npm\node_modules\appium\build\lib\main.js'

Write-Host '=== Appium Service Manager ===' -ForegroundColor Cyan

function Test-AppiumRunning {
    try {
        $response = Invoke-WebRequest -Uri $appiumUrl -Method GET -TimeoutSec 2 -UseBasicParsing -ErrorAction SilentlyContinue
        return $response.StatusCode -eq 200
    } catch {
        return $false
    }
}

function Start-AppiumService {
    Write-Host 'Starting Appium server in background...' -ForegroundColor Green
    
    $job = Start-Job -ScriptBlock {
        param($nodePath, $appiumMainScript)
        & $nodePath $appiumMainScript
    } -ArgumentList $nodePath, $appiumMainScript
    
    Write-Host "Appium job started with ID: $($job.Id)" -ForegroundColor Green
    
    $maxWait = 15
    $waited = 0
    Write-Host 'Waiting for Appium to be ready...' -ForegroundColor Yellow
    
    while ($waited -lt $maxWait) {
        Start-Sleep -Seconds 1
        $waited++
        
        if (Test-AppiumRunning) {
            Write-Host "Appium server is ready! (took ${waited}s)" -ForegroundColor Green
            return $true
        }
        Write-Host '.' -NoNewline
    }
    
    Write-Host ''
    Write-Host 'Appium did not start within timeout' -ForegroundColor Red
    Receive-Job -Job $job
    return $false
}

try {
    if (Test-AppiumRunning) {
        Write-Host "Appium is already running at port $appiumPort" -ForegroundColor Green
        exit 0
    }
    
    Write-Host 'Appium is not running. Starting service...' -ForegroundColor Yellow
    
    Get-Process -Name 'node' -ErrorAction SilentlyContinue | Stop-Process -Force
    Start-Sleep -Seconds 2
    
    if (Start-AppiumService) {
        Write-Host '=== Appium Service Started Successfully ===' -ForegroundColor Green
        exit 0
    } else {
        Write-Host '=== Failed to Start Appium Service ===' -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
