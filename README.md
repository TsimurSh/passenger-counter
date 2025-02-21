## Getting Started
- Setup environment var. in docker-compose.yml
- Create vehicleCapacities.json and set it as `volumes` in docker-compose.yml config
----------------------------------------------------------
### examples:
##### video-url: http://video.eu/
##### video-account: "api"
##### video-key: "key"
##### tc-url: https://tc.eu/api/v1/key/ 
##### key-agency: "key/agency/agency_id"
----------------------------------------------------------
## Build 
```shell
./gradlew assemble
```

## Run
Requirement:
- `docker` - installed.

Command:
```shell
docker compose up -d
```
## Check
http://localhost:8081/v1/busloads
