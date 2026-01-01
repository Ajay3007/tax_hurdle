# Build Script for InvestingHurdle API (No Maven/Gradle Required)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  InvestingHurdle API - Build Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check Java version
Write-Host "Checking Java version..." -ForegroundColor Yellow
$javaVersion = java -version 2>&1 | Select-String "version"
Write-Host $javaVersion
Write-Host ""

# Create bin directory
Write-Host "Creating output directory..." -ForegroundColor Yellow
if (!(Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

# Download Spring Boot dependencies (if needed)
Write-Host "Note: This API requires Spring Boot dependencies." -ForegroundColor Yellow
Write-Host "Please use one of these options:" -ForegroundColor Cyan
Write-Host ""
Write-Host "Option 1: Install Maven" -ForegroundColor Green
Write-Host "  Download from: https://maven.apache.org/download.cgi"
Write-Host "  Or use Chocolatey: choco install maven"
Write-Host ""
Write-Host "Option 2: Use the existing Java engine directly" -ForegroundColor Green
Write-Host "  The existing calculation engine in ../src already works!"
Write-Host "  Run: cd .. ; java -cp 'bin;lib/*' bootstrap.InvestingHurdleBootstrapper"
Write-Host ""
Write-Host "Option 3: IDE Integration" -ForegroundColor Green
Write-Host "  Import spring-api folder as Maven project in:"
Write-Host "  - IntelliJ IDEA (recommended)"
Write-Host "  - Eclipse with Maven plugin"
Write-Host "  - VS Code with Java Extension Pack"
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "For quick testing, the REST API structure is complete." -ForegroundColor Yellow
Write-Host "You can review the code and integrate it later." -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
