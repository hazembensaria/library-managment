param(
    [string]$ImageTag = "latest",
    [string]$ComposeFile = "docker-compose.yml"
)

Write-Host "Deploying image: $ImageTag"

Write-Host "Stopping existing containers (if any)..."
powershell -Command "docker-compose -f $ComposeFile down" 2>$null

Write-Host "Pulling image: $ImageTag"
docker pull $ImageTag | Write-Host

Write-Host "Starting containers..."
powershell -Command "docker-compose -f $ComposeFile up -d --build"

Write-Host "Deployment finished. Current containers:"
docker ps --filter "name=library-backend" --format "table {{.Names}}	{{.Image}}	{{.Status}}"

Write-Host "To rollback, re-run with a previous image tag or run 'docker-compose down' and start the older tag."
